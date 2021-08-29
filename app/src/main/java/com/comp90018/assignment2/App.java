package com.comp90018.assignment2;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import cn.jpush.im.android.api.JMessageClient;

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

        /*
         * The SDK initializes and specifies whether message logging roaming is enabled.
         * After message roaming is enabled, when the user logs in between devices,
         * the SDK will automatically synchronize the history messages of the current
         * user to the local PC. After the synchronization is complete, the SDK will
         * send a ConversationRefreshEvent event to notify the upper layer to refresh. 
         */
        JMessageClient.init(this, true);
    }

    /** Getter to get global context */
    public static Context getContext() {
        return mContext;
    }
}
