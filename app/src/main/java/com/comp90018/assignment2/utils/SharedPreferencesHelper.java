package com.comp90018.assignment2.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * For configuration holding.
 * <p>
 * Can also used for notification.
 *
 * @author xiaotian
 */
public class SharedPreferencesHelper {
    private final SharedPreferences sharedPreferences;

    private final SharedPreferences.Editor editor;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("GoodOG",
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * store with key
     */
    public void put(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * get record with key
     * @param key string key
     * @param defaultObject can be ""
     * @return string
     */
    public Object getSharedPreference(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }
    }

    /**
     * remove a record with key
     */
    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * clear all
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * check key existence
     */
    public Boolean contain(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * get all records
     */
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }
}
