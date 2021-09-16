package com.comp90018.assignment2.utils;

import android.content.Context;

import com.comp90018.assignment2.R;

import java.sql.Date;
import java.text.SimpleDateFormat;

import cn.jiguang.api.JCoreInterface;

/**
 * date & time formatter
 * mainly used for chat message displaying
 *
 * @author Jmessage project authority https://docs.jiguang.cn/jmessage/guideline/jmessage_guide/
 * @author xiaotian li
 */
public class TimeFormater {

    private long mTimeStamp;
    private Context mContext;

    public TimeFormater(Context context, long timeStamp) {
        this.mContext = context;

        // When the last message was created
        this.mTimeStamp = timeStamp;
    }

    /**
     * Conversation list time display:
     * The time of the last message of the session prevails
     *
     * The time of the current day message is displayed, e.g. 18:09
     * yesterday and the day before yesterday
     * Mon. Tue. ... in a week
     * 4-22 in a year
     * 2015-4-22 in other years
     */
    public String getTime() {

        //yyyy-MM-dd HH:mm:ss
        Date date = new Date(mTimeStamp);
        String dateStr = format(date, mContext.getString(R.string.jmui_time_format_accuracy));
        String oldYear = dateStr.substring(0, 4);
        int oldMonth = Integer.parseInt(dateStr.substring(5, 7));
        int oldDay = Integer.parseInt(dateStr.substring(8, 10));
        String oldHour = dateStr.substring(11, 13);
        String oldMinute = dateStr.substring(14, 16);

        //current time
        long today = JCoreInterface.getReportTime();
        Date now = new Date(today * 1000);
        String nowStr = format(now, mContext.getString(R.string.jmui_time_format_accuracy));

        String newYear = nowStr.substring(0, 4);
        int newMonth = Integer.parseInt(nowStr.substring(5, 7));
        int newDay = Integer.parseInt(nowStr.substring(8, 10));
        String newHour = nowStr.substring(11, 13);
        String newMinute = nowStr.substring(14, 16);
        String result = "";
        long l = today * 1000 - mTimeStamp;
        long days = l / (24 * 60 * 60 * 1000);
        long hours = (l / (60 * 60 * 1000) - days * 24);
        long min = ((l / (60 * 1000)) - days * 24 * 60 - hours * 60);
        long s = (l / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - min * 60);

        if (!oldYear.equals(newYear)) {
            // not this year
            result = oldYear + "-" + oldMonth + "-" + oldDay;
        } else {
            // this year
            // in the same month
            if (oldMonth == newMonth) {
                // in the same day
                if (oldDay == newDay) {
                    result = oldHour + ":" + oldMinute;
                } else {
                    // other days
                    int day = newDay - oldDay;
                    if (day == 1) {
                        result = "yesterday";
                    } else if (day == 2) {
                        result = "the day before yesterday";
                    } else if (day > 2 && day < 8) {
                        int week = date.getDay();
                        if (week == 1) {
                            result = mContext.getString(R.string.jmui_monday);
                        } else if (week == 2) {
                            result = mContext.getString(R.string.jmui_tuesday);
                        } else if (week == 3) {
                            result = mContext.getString(R.string.jmui_wednesday);
                        } else if (week == 4) {
                            result = mContext.getString(R.string.jmui_thursday);
                        } else if (week == 5) {
                            result = mContext.getString(R.string.jmui_friday);
                        } else if (week == 6) {
                            result = mContext.getString(R.string.jmui_saturday);
                        } else {
                            result = mContext.getString(R.string.jmui_sunday);
                        }
                    } else {
                        result = oldMonth + "-" + oldDay;
                    }
                }
            } else {
                if (oldMonth == 1 || oldMonth == 3 || oldMonth == 5 || oldMonth == 7 || oldMonth == 8 || oldMonth == 10 || oldMonth == 12) {
                    if (newDay == 1 && oldDay == 30) {
                        result = "the day before yesterday";
                    } else if (newDay == 1 && oldDay == 31) {
                        result = "yesterday";
                    } else if (newDay == 2 && oldDay == 31) {
                        result = "the day before yesterday";
                    } else {
                        result = oldMonth + "-" + oldDay;
                    }
                } else if (oldMonth == 2) {
                    if (newDay == 1 && oldDay == 27 || newDay == 2 && oldDay == 28) {
                        result = "the day before yesterday";
                    } else if (newDay == 1 && oldDay == 28) {
                        result = "yesterday";
                    } else {
                        result = oldMonth + "-" + oldDay;
                    }
                } else if (oldMonth == 4 || oldMonth == 6 || oldMonth == 9 || oldMonth == 11) {
                    if (newDay == 1 && oldDay == 29) {
                        result = "the day before yesterday";
                    } else if (newDay == 1 && oldDay == 30) {
                        result = "yesterday";
                    } else if (newDay == 2 && oldDay == 30) {
                        result = "the day before yesterday";
                    } else {
                        result = oldMonth + "-" + oldDay;
                    }
                }
            }
        }
        return result;
    }


    /**
     * Time display in a conversation
     * similar with the one above
     *
     * Time display interval: If the interval between sending or receiving messages
     * is more than 5 minutes, the new time is displayed
     */
    public String getDetailTime() {

        //yyyy-MM-dd HH:mm:ss
        Date date = new Date(mTimeStamp);
        String dateStr = format(date, mContext.getString(R.string.jmui_time_format_accuracy));
        String oldYear = dateStr.substring(0, 4);
        int oldMonth = Integer.parseInt(dateStr.substring(5, 7));
        int oldDay = Integer.parseInt(dateStr.substring(8, 10));
        String oldHour = dateStr.substring(11, 13);
        String oldMinute = dateStr.substring(14, 16);

        long today = JCoreInterface.getReportTime();
        Date now = new Date(today * 1000);
        String nowStr = format(now, mContext.getString(R.string.jmui_time_format_accuracy));

        String newYear = nowStr.substring(0, 4);
        int newMonth = Integer.parseInt(nowStr.substring(5, 7));
        int newDay = Integer.parseInt(nowStr.substring(8, 10));
        String newHour = nowStr.substring(11, 13);
        String newMinute = nowStr.substring(14, 16);
        String result = "";
        long l = today * 1000 - mTimeStamp;
        long days = l / (24 * 60 * 60 * 1000);
        long hours = (l / (60 * 60 * 1000) - days * 24);
        long min = ((l / (60 * 1000)) - days * 24 * 60 - hours * 60);
        long s = (l / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - min * 60);

        if (!oldYear.equals(newYear)) {

            result = oldYear + "-" + oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
        } else {

            if (oldMonth == newMonth) {

                if (oldDay == newDay) {
                    result = oldHour + ":" + oldMinute;
                } else {

                    int day = newDay - oldDay;
                    if (day == 1) {
                        result = "yesterday " + oldHour + ":" + oldMinute;
                    } else if (day == 2) {
                        result = "the day before yesterday " + oldHour + ":" + oldMinute;
                    } else if (day > 2 && day < 8) {
                        int week = date.getDay();
                        if (week == 1) {
                            result = mContext.getString(R.string.jmui_monday) + " " + oldHour + ":" + oldMinute;
                        } else if (week == 2) {
                            result = mContext.getString(R.string.jmui_tuesday) + " " + oldHour + ":" + oldMinute;
                        } else if (week == 3) {
                            result = mContext.getString(R.string.jmui_wednesday) + " " + oldHour + ":" + oldMinute;
                        } else if (week == 4) {
                            result = mContext.getString(R.string.jmui_thursday) + " " + oldHour + ":" + oldMinute;
                        } else if (week == 5) {
                            result = mContext.getString(R.string.jmui_friday) + " " + oldHour + ":" + oldMinute;
                        } else if (week == 6) {
                            result = mContext.getString(R.string.jmui_saturday) + " " + oldHour + ":" + oldMinute;
                        } else {
                            result = mContext.getString(R.string.jmui_sunday) + " " + oldHour + ":" + oldMinute;
                        }
                    } else {
                        result = oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
                    }
                }
            } else {
                if (oldMonth == 1 || oldMonth == 3 || oldMonth == 5 || oldMonth == 7 || oldMonth == 8 || oldMonth == 10 || oldMonth == 12) {
                    if (newDay == 1 && oldDay == 30) {
                        result = "the day before yesterday " + oldHour + ":" + oldMinute;
                    } else if (newDay == 1 && oldDay == 31) {
                        result = "yesterday " + oldHour + ":" + oldMinute;
                    } else if (newDay == 2 && oldDay == 31) {
                        result = "the day before yesterday " + oldHour + ":" + oldMinute;
                    } else {
                        result = oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
                    }
                } else if (oldMonth == 2) {
                    if (newDay == 1 && oldDay == 27 || newDay == 2 && oldDay == 28) {
                        result = "the day before yesterday " + oldHour + ":" + oldMinute;
                    } else if (newDay == 1 && oldDay == 28) {
                        result = "yesterday " + oldHour + ":" + oldMinute;
                    } else {
                        result = oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
                    }
                } else if (oldMonth == 4 || oldMonth == 6 || oldMonth == 9 || oldMonth == 11) {
                    if (newDay == 1 && oldDay == 29) {
                        result = "the day before yesterday " + oldHour + ":" + oldMinute;
                    } else if (newDay == 1 && oldDay == 30) {
                        result = "yesterday " + oldHour + ":" + oldMinute;
                    } else if (newDay == 2 && oldDay == 30) {
                        result = "the day before yesterday " + oldHour + ":" + oldMinute;
                    } else {
                        result = oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
                    }
                }
            }
        }
        return result;
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
}
