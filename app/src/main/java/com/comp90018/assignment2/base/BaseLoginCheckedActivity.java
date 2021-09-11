package com.comp90018.assignment2.base;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.assignment2.App;
import com.comp90018.assignment2.modules.users.authentication.activity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

/**
 * 
 * base activity for all activities that need user to login
 * 
 * @author xiaotian li
 */
public abstract class BaseLoginCheckedActivity extends AppCompatActivity {


    /**
     * The method will be called called in the main thread and can be used to refresh the UI.
     *
     * Check login status change.
     *
     * @param event Jmessage events
     */
    public void onEventMainThread(LoginStateChangeEvent event) {

        // check login state change
        LoginStateChangeEvent.Reason reason = event.getReason();

        if (reason == LoginStateChangeEvent.Reason.user_logout) {
            Toast.makeText(App.getContext(), "The login is expired. Please Log in again", Toast.LENGTH_SHORT).show();
            goToLoginActivity();
        } else if (reason == LoginStateChangeEvent.Reason.user_password_change) {
            Toast.makeText(App.getContext(), "Password changed. Please Log in again", Toast.LENGTH_SHORT).show();
            goToLoginActivity();
        }
    }

    /**
     * go to login activity and exit current activity.
     */
    private void goToLoginActivity() {
        if (!isFinishing()) {

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            if (firebaseAuth.getCurrentUser() != null) {

                firebaseAuth.signOut();
            }
            JMessageClient.logout();
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);


            finish();
        }
    }


}
