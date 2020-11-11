package indi.ssuf1998.vqrscan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.core.TorchState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;

import org.greenrobot.greendao.database.Database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import indi.ssuf1998.bhbottomsheet.MenuBottomSheet;
import indi.ssuf1998.bhbottomsheet.MenuBottomSheetItem;
import indi.ssuf1998.vqrscan.databinding.ActivityMainBinding;
import indi.ssuf1998.vqrscan.greendao.DaoMaster;
import indi.ssuf1998.vqrscan.greendao.DaoSession;
import indi.ssuf1998.vqrscan.greendao.HistoryItemDao;

public class MainActivity extends mActivity {
    private ActivityMainBinding binding;

    private ListenableFuture<ProcessCameraProvider> providerFuture;
    private ProcessCameraProvider provider;
    private int deg;
    private Camera camera;
    private final ZXingAnalyzer analyzer = new ZXingAnalyzer();

    private ScanResultBottomSheet scanSheet;
    private MenuBottomSheet menuSheet;

    private boolean lowLight = false;

    private boolean exiting = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initDB();

        binding.getRoot().post(() -> {
            initUI();
            bindListeners();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        analyzer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        analyzer.resume();
    }

    @Override
    public void onBackPressed() {
        if (!exiting) {
            exiting = true;
            new Handler().postDelayed(() -> {
                exiting = false;
            }, 2000);
            Toasty.info(this, getString(R.string.exiting_msg), Toasty.LENGTH_SHORT).show();
        } else {
            System.exit(0);
        }
    }

    @Override
    public void initUI() {
        providerFuture = ProcessCameraProvider.getInstance(this);
        analyzer.setTimeOutThreshold(10000);
        scanSheet = new ScanResultBottomSheet(getString(R.string.scan_sheet_title));

        final List<MenuBottomSheetItem> items = new ArrayList<>();
        items.add(new MenuBottomSheetItem()
                .setItemText(getString(R.string.menu_item_settings))
                .setItemIcon(R.drawable.ic_baseline_settings_24)
        );
        items.add(new MenuBottomSheetItem()
                .setItemText(getString(R.string.menu_item_exit))
                .setItemIcon(R.drawable.ic_baseline_exit_to_app_24)
        );

        menuSheet = new MenuBottomSheet(getString(R.string.menu_sheet_title), items);
        menuSheet.setDecoration(new RecycleViewDivider(this));

        Utils.applyShadow(
                0, 0, 11,
                Color.BLACK, 128,
                binding.menuImgBtn,
                binding.pickPicImgBtn,
                binding.historyImgBtn
        );
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void bindListeners() {
        providerFuture.addListener(() -> {
            try {
                provider = providerFuture.get();
                if (askForPermissions()) {
                    bindCamera();
                }
            } catch (ExecutionException | InterruptedException ignore) {
            }
        }, ContextCompat.getMainExecutor(this));

        scanSheet.setOnShowListener(analyzer::pause);
        scanSheet.setOnDismissListener(() -> {
            analyzer.resume();
            camera.getCameraControl().setZoomRatio(1);
        });

        menuSheet.setOnItemClickListener(idx -> {
            if (idx == 0) {
                final Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            } else if (idx == 1) {
                System.exit(0);
            }

            menuSheet.dismiss();
        });

        menuSheet.setOnShowListener(analyzer::pause);
        menuSheet.setOnDismissListener(analyzer::resume);

        analyzer.setOnDetectListener(this::handleDetect);

        analyzer.setOnLightListener(light -> {
            if (light < 60) {
                lowLight = true;
                binding.mEffectView.getTorch().getAnimator().resume();
            } else {
                lowLight = false;
                binding.mEffectView.getTorch().dontDrawMe();
            }
        });
        analyzer.setOnTimeOutListener(() -> {
            final float zoom = camera.getCameraInfo().getZoomState().getValue().getZoomRatio();
            if (zoom < 2) {
                camera.getCameraControl().setZoomRatio(zoom + 0.5f);
            }
        });

        binding.mEffectView.setOnTouchListener((v, event) -> {
            final int action = event.getAction();
            if (action == MotionEvent.ACTION_UP) {
                if (camera.getCameraInfo().getTorchState().getValue() == TorchState.ON) {
                    camera.getCameraControl().enableTorch(false);
                } else {
                    if (lowLight) {
                        camera.getCameraControl().enableTorch(true);
                    } else {
                        setFocus(event.getX(), event.getY());
                    }
                }
            }
            return false;
        });

        binding.noPermissionLayout.tryAgainBtn.setOnClickListener(v -> askForPermissions());
        binding.noPermissionLayout.exitBtn.setOnClickListener(v -> System.exit(0));

        binding.menuImgBtn.setOnClickListener(v -> menuSheet.show(getSupportFragmentManager()));
        binding.pickPicImgBtn.setOnClickListener(v -> pickPic());

        binding.historyImgBtn.setOnClickListener(v -> {
            final Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    private void bindCamera() {
        final CameraSelector selector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        final Preview preview = new Preview.Builder()
                .build();

        final ImageAnalysis analysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        analysis.setAnalyzer(ContextCompat.getMainExecutor(this), analyzer);

        final OrientationEventListener mOrientationEventListener =
                new OrientationEventListener(this) {
                    @Override
                    public void onOrientationChanged(int orientation) {
                        if (analysis == null)
                            return;

                        if (orientation >= 45 && orientation < 135) {
                            deg = Surface.ROTATION_270;
                        } else if (orientation >= 135 && orientation < 225) {
                            deg = Surface.ROTATION_180;
                        } else if (orientation >= 225 && orientation < 315) {
                            deg = Surface.ROTATION_90;
                        } else {
                            deg = Surface.ROTATION_0;
                        }

                        analysis.setTargetRotation(deg);
                    }
                };
        mOrientationEventListener.enable();

        preview.setSurfaceProvider(binding.mPreviewView.getSurfaceProvider());

        camera = provider.bindToLifecycle(
                this, selector, preview, analysis);

        binding.mEffectView.getScanBar().getAnimator().start();

        binding.mEffectView.getTorch().dontDrawMe();
        binding.mEffectView.getTorch().getAnimator().start();
    }

    private void setFocus(float x, float y) {
        final MeteringPointFactory factory = new SurfaceOrientedMeteringPointFactory(
                binding.mPreviewView.getWidth(), binding.mPreviewView.getHeight());
        // 现在普遍手机屏幕的长宽比都大于4:3，就不考虑高了
        // 预览界面定为4:3会导致宽是跑出去的
        // 所以这个宽得改，乘1.33就好
        final MeteringPoint point = factory.createPoint(x * 4 / 3, y);

        camera.getCameraControl().startFocusAndMetering(
                new FocusMeteringAction
                        .Builder(point, FocusMeteringAction.FLAG_AF)
                        .setAutoCancelDuration(3, TimeUnit.SECONDS)
                        .build()
        );
    }

    private void handleDetect(Result result) {
        analyzer.pause();

        if (preferences.getBoolean(Const.SETTINGS_TOGGLE_VIBRATE, true)) {
            final Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(50);
        }

        final float[] center =
                analyzer.getCorrectCodeCenter(
                        result,
                        binding.mEffectView.getWidth(),
                        binding.mEffectView.getHeight(),
                        deg
                );
        binding.mEffectView.getDetectDot().setX((int) center[0]);
        binding.mEffectView.getDetectDot().setY((int) center[1]);
        binding.mEffectView.getDetectDot().getAnimator().start();

        new Handler().postDelayed(() -> showScanResult(result),
                binding.mEffectView.getDetectDot().getAnimator().getDuration());
    }

    private void showScanResult(Result result) {
        scanSheet.setResultText(result.getText());

        if (preferences.getBoolean(Const.SETTINGS_DIRECTLY_OPEN, false)
                && scanSheet.isResultOpenable()) {
            scanSheet.openResult(this);
        } else {
            scanSheet.show(getSupportFragmentManager());
        }

        if (preferences.getBoolean(Const.SETTINGS_TOGGLE_HISTORY, true)) {
            HistoryItem item = new HistoryItem(
                    scanSheet.getResultText(),
                    scanSheet.getResultType(),
                    new Date().getTime()
            );
            historyItemDao.insert(item);
        }
    }

    private void pickPic() {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, null),
                Const.PICK_IMG_REQUEST);
    }

    private boolean askForPermissions() {
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.CAMERA
        );
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    Const.ASK_FOR_CAMERA);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Const.ASK_FOR_CAMERA) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new Handler().post(() -> {
                    do {
                        bindCamera();

                        binding.noPermissionLayout.getRoot().setVisibility(View.GONE);
                        binding.scanLayout.setVisibility(View.VISIBLE);
                    } while (provider == null);
                });
            } else {
                binding.noPermissionLayout.getRoot().setVisibility(View.VISIBLE);
                binding.scanLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == Const.PICK_IMG_REQUEST) {
                final Uri uri = data.getData();
                try {
                    assert uri != null;
                    decodeFromUri(uri);
                } catch (IOException e) {
                    Toasty.error(MainActivity.this,
                            getString(R.string.err_file_not_find),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == Const.DIRECTLY_OPENED) {
            analyzer.resume();
        }
    }

    private void decodeFromUri(Uri uri) throws IOException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 4;
        final Bitmap bmp = BitmapFactory.decodeStream(
                this.getContentResolver().openInputStream(uri),
                null,
                options
        );

        new Handler().post(() -> {
                    try {
                        showScanResult(analyzer.analyzeFromBmp(bmp));
                    } catch (NotFoundException e) {
                        Toasty.error(MainActivity.this,
                                getString(R.string.err_not_detect),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // 数据库相关
    private HistoryItemDao historyItemDao;

    @Override
    protected void initDB() {
        if (!sharedHelper.contains("db_session")) {
            mOpenHelper helper = new mOpenHelper(this, "vqrscan-db");
            Database db = helper.getWritableDb();

            DaoSession session = new DaoMaster(db).newSession();
            historyItemDao = session.getHistoryItemDao();

            sharedHelper.putData("db_session", session);
        } else {
            historyItemDao = ((DaoSession) sharedHelper.getData("db_session")).getHistoryItemDao();
        }

        preferences = getSharedPreferences("vqrscan_pref", MODE_PRIVATE);
    }
}