package com.comp90018.assignment2.app;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

/**
 * Singleton, Represents the whole app
 * When the application starts, this part runs first
 * Can configure the whole app, and configure some global variables if needed.
 *
 * @author xiaotian
 */
public class App extends MultiDexApplication {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    /** Getter to get global context */
    public static Context getContext() {
        return mContext;
    }
}
