package com.comp90018.assignment2.modules.location.utils;

import com.comp90018.assignment2.modules.location.bean.LocationBean;

/**
 * callback when get live location
 *
 * @author xiaotian li
 */
public interface OnGotLocationCallback {
    void gotLocationCallback(LocationBean locationBean);
}
