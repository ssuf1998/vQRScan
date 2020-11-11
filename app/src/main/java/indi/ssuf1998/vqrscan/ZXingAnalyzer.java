package indi.ssuf1998.vqrscan;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ZXingAnalyzer implements ImageAnalysis.Analyzer {
    private final MultiFormatReader reader = new MultiFormatReader();
    private static final List<Integer> SupportYuvFormats = Arrays.asList(
            ImageFormat.YUV_420_888,
            ImageFormat.YUV_422_888,
            ImageFormat.YUV_444_888
    );
    private static final int YSampleStep = 10;
    private static final int analyzeDelay = 500;
    private long lastTS;
    private long lastDetectTS;
    private int timeOutThreshold = 5000;
    private DetectListener mDetectListener;
    private LightListener mLightListener;
    private TimeOutListener mTimeOutListener;
    private final int[] innerSize = new int[2];

    private boolean pause;

    public ZXingAnalyzer() {
        final Map<DecodeHintType, Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());

        reader.setHints(hints);
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        if (!SupportYuvFormats.contains(image.getFormat())) {
            return;
        }

        final long cur = System.currentTimeMillis();
        if (pause || cur - lastTS <= analyzeDelay) {
            image.close();
            return;
        }

        final ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        buffer.rewind();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        long light = 0;
        for (int i = 0; i < data.length; i += YSampleStep) {
            light += (data[i] & 0xffL);
        }
        light /= (data.length / (float) YSampleStep);

        if (mLightListener != null && lastTS != 0) {
            mLightListener.light((int) (light));
        }

        final int deg = image.getImageInfo().getRotationDegrees();
        final int rotatedW = (deg % 180 == 0 ? image.getWidth() : image.getHeight());
        final int rotatedH = (deg % 180 == 0 ? image.getHeight() : image.getWidth());
        data = rotateImageArray(data,
                image.getWidth(), image.getHeight(),
                deg);

        final PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                data,
                rotatedW,
                rotatedH,
                0, 0,
                rotatedW, rotatedH,
                false
        );

        final BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        if (lastDetectTS == 0)
            lastDetectTS = cur;

        try {
            final Result result = reader.decode(bitmap);
            innerSize[0] = bitmap.getWidth();
            innerSize[1] = bitmap.getHeight();

            if (mDetectListener != null)
                mDetectListener.detect(result);

            lastDetectTS = cur;
        } catch (NotFoundException e) {
            if (cur - lastDetectTS >= timeOutThreshold) {
                if (mTimeOutListener != null) {
                    mTimeOutListener.timeout();
                }
                lastDetectTS = cur;
            }
        }

        image.close();
        lastTS = cur;
    }

    public Result analyzeFromBmp(Bitmap bmp) throws NotFoundException {
        final int width = bmp.getWidth();
        final int height = bmp.getHeight();
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        bmp.recycle();
        final RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        final BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        return reader.decode(bitmap);
    }

    // https://stackoverflow.com/questions/58113159/how-to-use-zxing-with-android-camerax-to-decode-barcode-and-qr-codes
    private byte[] rotateImageArray(byte[] arr, int w, int h, int deg) {
        if (deg == 0 || deg % 90 != 0)
            return arr;

        final byte[] rotatedArr = new byte[arr.length];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (deg == 90) {
                    rotatedArr[x * h + h - y - 1] = arr[x + y * w];
                } else if (deg == 180) {
                    rotatedArr[w * (h - y - 1) + w - x - 1] = arr[x + y * w];
                } else if (deg == 270) {
                    rotatedArr[y + x * h] = arr[y * w + w - x - 1];
                }
            }
        }

        return rotatedArr;
    }

    public float[] getCorrectCodeCenter(Result result,
                                        int w, int h,
                                        int deg) {
        final float[] pos = computeCodeCenter(result.getResultPoints());

        // 现在普遍手机屏幕的长宽比都大于4:3，就不考虑高了
        // 预览界面定为4:3会导致宽是跑出去的
        // 所以这个宽得改，乘1.33就好
        // 宽超出去了，超出的部分为(1.33w-w)/2=0.66w=1/6w，短的那边的坐标点就得移动以下
        final float correctedW = w * 4f / 3;
        if (deg == Surface.ROTATION_0) {
            pos[0] /= (innerSize[0] / correctedW);
            pos[1] /= (innerSize[1] / (float) h);
        } else if (deg == Surface.ROTATION_180) {
            pos[0] /= (innerSize[0] / correctedW);
            pos[1] /= (innerSize[1] / (float) h);
            pos[0] = correctedW - pos[0];
            pos[1] = h - pos[1];
        } else if (deg == Surface.ROTATION_90) {
            // 手机头朝左的情况
            final float tmpPos0 = pos[0] / (innerSize[0] / (float) h);
            final float tmpPos1 = pos[1] / (innerSize[1] / correctedW);

            pos[0] = correctedW - tmpPos1;
            pos[1] = tmpPos0;
        } else if (deg == Surface.ROTATION_270) {
            // 手机头朝右的情况
            final float tmpPos0 = pos[0] / (innerSize[0] / (float) h);
            final float tmpPos1 = pos[1] / (innerSize[1] / correctedW);

            pos[0] = tmpPos1;
            pos[1] = h - tmpPos0;
        }
        pos[0] -= w / 6f;
        // 一开始的时候横坐标出来的位置是以一半宽为基准镜像的……
        // 后面加入扫条形码的时候，发现竖屏扫不出来，横屏才可以
        // 于是发现analysis中的那个图是旋转过的，也就是90度才是竖屏模式！需要手动旋转
        // 大致认为输入的二维码是一个正方形，以原点旋转90度不就是镜像过去么！
        return pos;
    }

    private float[] computeCodeCenter(ResultPoint[] pts) {
        final float[] center = new float[]{0, 0};

        if (pts.length > 3) {
            for (ResultPoint pt : pts) {
                center[0] += pt.getX();
                center[1] += pt.getY();
            }

            center[0] /= pts.length;
            center[1] /= pts.length;
        } else if (pts.length == 1) {
            center[0] = pts[0].getX();
            center[1] = pts[0].getY();
        } else if (pts.length == 2) {
            center[0] = (pts[0].getX() + pts[1].getX()) / 2;
            center[1] = (pts[0].getY() + pts[1].getY()) / 2;
        } else if (pts.length == 3) {
            center[0] = (pts[0].getX() + pts[2].getX()) / 2;
            center[1] = (pts[0].getY() + pts[2].getY()) / 2;
        }

        return center;
    }

    public interface DetectListener {
        void detect(Result result);
    }

    public void setOnDetectListener(@NonNull DetectListener listener) {
        this.mDetectListener = Objects.requireNonNull(listener);
    }

    public interface LightListener {
        void light(int light);
    }

    public void setOnLightListener(@NonNull LightListener listener) {
        this.mLightListener = Objects.requireNonNull(listener);
    }

    public interface TimeOutListener {
        void timeout();
    }

    public void setOnTimeOutListener(@NonNull TimeOutListener listener) {
        this.mTimeOutListener = Objects.requireNonNull(listener);
    }

    public void setTimeOutThreshold(int timeOutThreshold) {
        this.timeOutThreshold = timeOutThreshold;
    }

    public int getTimeOutThreshold() {
        return timeOutThreshold;
    }

    public void pause() {
        pause = true;
    }

    public void resume() {
        pause = false;
    }
}
