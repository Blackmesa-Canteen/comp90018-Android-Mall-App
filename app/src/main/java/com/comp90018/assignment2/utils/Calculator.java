package com.comp90018.assignment2.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Calc some math stuff
 *
 * @author xiaotian li
 */
public class Calculator {

    /**
     * Long time -> convert to date -> convert to String in required format
     */
    public static String fromLongToDate(String format, Long time) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date(time);
        return sdf.format(date);
    }
}
