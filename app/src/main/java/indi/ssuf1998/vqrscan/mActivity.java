package indi.ssuf1998.vqrscan;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public abstract class mActivity extends AppCompatActivity {
    protected final SharedHelper sharedHelper = SharedHelper.getInstance();
    protected SharedPreferences preferences;

    protected abstract void initDB();

    protected abstract void initUI();

    protected abstract void bindListeners();

}
