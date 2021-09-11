package com.comp90018.assignment2;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.comp90018.assignment2.utils.SharedPreferencesHelper;

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
    private static SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        // attach shared preference helper
        sharedPreferencesHelper = new SharedPreferencesHelper(this);

        /*
         * The SDK initializes and specifies whether message logging roaming is enabled.
         * After message roaming is enabled, when the user logs in between devices,
         * the SDK will automatically synchronize the history messages of the current
         * user to the local PC. After the synchronization is complete, the SDK will
         * send a ConversationRefreshEvent event to notify the upper layer to refresh.
         */
        JMessageClient.init(this, true);

        /*
         * The application layer can register event reception in any class,
         * and the SDK holds strong references to that class.
         *
         * The upper layer needs to be careful to unbind event reception in appropriate places.
         *
         * To implement global event listening, or need to listen for events throughout
         * the life of the application, it is recommended to put it in the Application class,
         * not in components like Activity, fragmemt, or Service.
         */
        JMessageClient.registerEventReceiver(this);
    }



    @Override
    public void onTerminate() {
        super.onTerminate();

        /*
         * A message receiving class object that will not
         * receive any events after it is unbound.
         */
        JMessageClient.unRegisterEventReceiver(this);
    }

    /** Getter to get global context */
    public static Context getContext() {
        return mContext;
    }
}
