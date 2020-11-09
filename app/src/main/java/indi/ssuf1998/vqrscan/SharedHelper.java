package indi.ssuf1998.vqrscan;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SharedHelper {
    private static SharedHelper instance;
    private final HashMap<String, Object> dataMap;

    private SharedHelper() {
        dataMap = new HashMap<>();
    }

    public static SharedHelper getInstance() {
        if (instance == null) {
            synchronized (SharedHelper.class) {
                if (instance == null) {
                    instance = new SharedHelper();
                }
            }
        }
        return instance;
    }

    public Object getDataThenSweep(String key, Object defaultValue) {
        final Object v = getData(key, defaultValue);
        removeData(key);
        return v;
    }

    public Object getDataThenSweep(String key) {
        return getDataThenSweep(key, null);
    }

    public Object getData(String key, Object defaultValue) {
        return Optional
                .ofNullable(dataMap.get(key))
                .orElse(defaultValue);
    }

    public Object getData(String key) {
        return getData(key, null);
    }

    public void putData(String key, Object value) {
        dataMap.put(key, value);
    }

    public void removeData(String key) {
        dataMap.remove(key);
    }

    public boolean contains(String key) {
        return dataMap.containsKey(key);
    }
}
