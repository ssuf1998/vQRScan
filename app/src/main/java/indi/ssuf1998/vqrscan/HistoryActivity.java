package indi.ssuf1998.vqrscan;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Query;

import java.util.List;

import es.dmoral.toasty.Toasty;
import indi.ssuf1998.vqrscan.databinding.ActivityHistoryBinding;
import indi.ssuf1998.vqrscan.greendao.DaoMaster;
import indi.ssuf1998.vqrscan.greendao.DaoSession;
import indi.ssuf1998.vqrscan.greendao.HistoryItemDao;

public class HistoryActivity extends mActivity {
    private ActivityHistoryBinding binding;
    private ScanResultBottomSheet scanSheet;

    private List<HistoryItem> items;
    private HistoryAdapter adapter;

    private AlertDialog.Builder clearHistoryDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initDB();
        readHistoryFromDB();

        binding.getRoot().post(() -> {
            initUI();
            bindListeners();
        });
    }

    @Override
    protected void initUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.history_activity_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        scanSheet = new ScanResultBottomSheet(getString(R.string.scan_sheet_title));

        adapter = new HistoryAdapter(items, this);
        final LinearLayoutManager layoutMgr = new LinearLayoutManager(this);

        binding.historyRecyclerView.setAdapter(adapter);
        binding.historyRecyclerView.setLayoutManager(layoutMgr);

        clearHistoryDialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.history_clear_dialog_msg))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.dialog_yes), (dialog, which) -> {
                    delAllHistoryInDB();
                    Toasty.info(this, getString(R.string.done_msg), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.dialog_no), null);
    }

    @Override
    protected void bindListeners() {
        adapter.setOnItemClickListener(view -> {
            final int idx = binding.historyRecyclerView.getChildAdapterPosition(view);
            scanSheet.setResultText(items.get(idx).getResultText());
            scanSheet.show(getSupportFragmentManager());
        });

        scanSheet.setOnBtnClickListener((v, btn) -> {
            if (btn == ScanResultBottomSheet.ButtonId.OPEN_BTN) {
                scanSheet.dismiss();
            }
        });
    }

    // 数据库
    private HistoryItemDao historyItemDao;

    @Override
    protected void initDB() {
        if (!sharedHelper.contains("db_session")) {
             System.exit(0);
            // 开发用！
            // 一般情况下直接system.exit得了
//            mOpenHelper helper = new mOpenHelper(this, "vqrscan-db");
//            Database db = helper.getWritableDb();
//
//            DaoSession session = new DaoMaster(db).newSession();
//            historyItemDao = session.getHistoryItemDao();
//
//            sharedHelper.putData("db_session", session);
        } else {
            historyItemDao = ((DaoSession) sharedHelper.getData("db_session")).getHistoryItemDao();
        }
    }

    private void readHistoryFromDB() {
        Query<HistoryItem> historyItemQuery = historyItemDao
                .queryBuilder()
                .orderDesc(HistoryItemDao.Properties.CreateTime)
                .build();
        items = historyItemQuery.list();
    }

    private void delAllHistoryInDB() {
        final int itemsLen = items.size();
        historyItemDao.deleteAll();
        items.clear();
        adapter.notifyItemRangeRemoved(0, itemsLen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuItem historyMenuItem = menu
                .add(Menu.NONE, Const.CLEAR_HISTORY_MENU_ID,
                        Menu.NONE, getString(R.string.menu_item_history));
        historyMenuItem
                .setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_delete_24))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            historyMenuItem.setIconTintList(ContextCompat.getColorStateList(this, R.color.white));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == Const.CLEAR_HISTORY_MENU_ID) {
            if (items.isEmpty()) {
                Toasty.info(this, getString(R.string.empty_history_msg), Toast.LENGTH_SHORT).show();
            } else {
                clearHistoryDialog.show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}