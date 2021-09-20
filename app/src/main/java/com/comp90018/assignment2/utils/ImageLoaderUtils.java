package com.comp90018.assignment2.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

/**
 * ImageLoaderUtils
 * @author xiaotian li
 * @author 罗孟伟 https://github.com/LuckSiege/PictureSelector
 */
public class ImageLoaderUtils {
    public static boolean assertValidRequest(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return !isDestroy(activity);
        } else if (context instanceof ContextWrapper){
            ContextWrapper contextWrapper = (ContextWrapper) context;
            if (contextWrapper.getBaseContext() instanceof Activity){
                Activity activity = (Activity) contextWrapper.getBaseContext();
                return !isDestroy(activity);
            }
        }
        return true;
    }

    private static boolean isDestroy(Activity activity) {
        if (activity == null) {
            return true;
        }
        return activity.isFinishing() || activity.isDestroyed();
    }
}
