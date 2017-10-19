package com.administrator.eventplanner.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AppUtil {

    public static long getMilliDate(String date){
        long milliDtate = 0;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        try{
            milliDtate = format.parse(date).getTime();
        }catch (Exception e){

        }
        return milliDtate;
    }

    public static boolean isEntityString(String s) {
        return s == null || s.equals("null") || s.equals("") || s.trim().length() <= 0 ? false : true;
    }

    public static String getTotalDays(long startMillis,long endMillis){
        long totalTime = endMillis - startMillis;
        try{
            int day = (int)Math.floor(totalTime/(24*60*60*1000));
            if (totalTime%(24*60*60*1000) == 0){
                return day+"일";
            }
            int hour = (int)Math.floor(totalTime/(60*60*1000)%24);
            if (totalTime%(60*60*1000) == 0){
                return day+"일"+hour+"시간";
            }
            int minite = (int)Math.floor((totalTime/(60*1000))%60);
            return day+"일"+hour+"시간"+minite+"분";
        }catch (Exception e){
            Log.e("SubPlan-EXP",e.getMessage());
        }

        return null;
    }
}
