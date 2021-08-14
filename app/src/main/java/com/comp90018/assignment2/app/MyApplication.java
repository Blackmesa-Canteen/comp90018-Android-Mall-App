package com.comp90018.assignment2.app;

import android.app.Application;
import android.content.Context;

/**
 * Represents the whole app
 * When the application starts, this part runs first
 */
public class MyApplication extends Application {
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
