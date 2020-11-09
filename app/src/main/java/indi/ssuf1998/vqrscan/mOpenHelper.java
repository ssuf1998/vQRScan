package indi.ssuf1998.vqrscan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

import indi.ssuf1998.vqrscan.greendao.DaoMaster;

public class mOpenHelper extends DaoMaster.OpenHelper {
    public mOpenHelper(Context context, String name) {
        super(context, name);
    }

    public mOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onCreate(Database db) {
        super.onCreate(db);
    }
}
