package com.cainam.tuchat.system;

import android.app.Application;
import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by cainam on 9/1/17.
 */

public class LastSeen extends Application{



    private static final int MILLIS = 1;
    private static final int SECOND = MILLIS * 1000;
    private static final int MINUTE = SECOND * 60;
    private static final int HOUR = MINUTE * 60;
    private static final int DAY = HOUR * 24;


    public static String checkLastTime(long timeRecorded, Context context){

        if (timeRecorded < 1000000000000L){
            timeRecorded = timeRecorded * 1000;
        }

        long currentTime = System.currentTimeMillis();

        if (timeRecorded > currentTime || timeRecorded <= 0){
            return null;
        }

        long timeDiff = currentTime - timeRecorded;

        if (timeDiff < MINUTE){
            return "Seen: just now";
        }else if (timeDiff < (2 * MINUTE)){
            return "Seen: 1 minute ago";
        }else if (timeDiff < (50 * MINUTE)){
            long time = (timeDiff / MINUTE);
            return "Seen: " + time + " minutes ago";
        }else if (timeDiff < (90 * MINUTE)){
            return "Seen: 1 hour ago";
        }else if (timeDiff < (24 * HOUR)){
            long time = (timeDiff / HOUR);
            return "Seen: " + time + " hours ago";
        }else if (timeDiff < (48 * HOUR)){
            return "Seen: yesterday";
        }else if (timeDiff < (365 * DAY)){
            long time = timeDiff / DAY;
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timeRecorded);
            SimpleDateFormat fmt = new SimpleDateFormat("EEE: MMM d, yyyy 'at' HH:mm a", Locale.US);
            fmt.format(cal.getTime());
            return fmt.format(cal.getTime());
        }
        return null;
    }
}
