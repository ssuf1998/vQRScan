package indi.ssuf1998.vqrscan;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import indi.ssuf1998.vqrscan.databinding.ActivitySettingsBinding;
import indi.ssuf1998.vqrscan.greendao.DaoSession;
import indi.ssuf1998.vqrscan.greendao.HistoryItemDao;

public class SettingsActivity extends mActivity {
    private ActivitySettingsBinding binding;

    private final List<SettingsSimpleItem<?>> items = new ArrayList<>();
    private AlertDialog.Builder aboutDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initDB();

        binding.getRoot().post(() -> {
            initUI();
            bindListeners();
        });
    }

    @Override
    protected void initDB() {
        preferences = getSharedPreferences("vqrscan_pref", MODE_PRIVATE);
    }

    @Override
    protected void initUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.settings_activity_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        items.add(
                new SettingsSwitchItem(getString(R.string.settings_directly_open),
                        preferences.getBoolean(Const.SETTINGS_DIRECTLY_OPEN, false))
                        .setItemIcon(R.drawable.ic_baseline_flash_on_24)
                        .setItemSubText(getString(R.string.settings_directly_open_sub))
        );

        items.add(
                new SettingsSwitchItem(getString(R.string.setting_toggle_history),
                        preferences.getBoolean(Const.SETTINGS_TOGGLE_HISTORY, true))
                        .setItemIcon(R.drawable.ic_baseline_history_toggle_off_24)
        );

        items.add(
                new SettingsSwitchItem(getString(R.string.settings_toggle_vibrate),
                        preferences.getBoolean(Const.SETTINGS_TOGGLE_VIBRATE, true))
                        .setItemIcon(R.drawable.ic_baseline_vibration_24)
        );

        aboutDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.settings_about)
                .setMessage(getString(R.string.about))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.dialog_yes), null);

        items.add(
                new SettingsSimpleItem<Void>(getString(R.string.settings_about), null)
                        .setItemIcon(R.drawable.ic_baseline_info_24)
                        .setOnItemClickListener(v -> aboutDialog.show())
        );

        final SettingsAdapter adapter = new SettingsAdapter(items, this);
        final LinearLayoutManager layoutMgr = new LinearLayoutManager(this);

        binding.settingsRecyclerView.setAdapter(adapter);
        binding.settingsRecyclerView.setLayoutManager(layoutMgr);
    }

    @Override
    protected void bindListeners() {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Const.SETTINGS_DIRECTLY_OPEN, ((SettingsSwitchItem) items.get(0)).getValue());
        editor.putBoolean(Const.SETTINGS_TOGGLE_HISTORY, ((SettingsSwitchItem) items.get(1)).getValue());
        editor.putBoolean(Const.SETTINGS_TOGGLE_VIBRATE, ((SettingsSwitchItem) items.get(2)).getValue());
        editor.apply();
    }
}