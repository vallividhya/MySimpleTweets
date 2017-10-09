package com.codepath.apps.restclienttemplate.util;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class to hold utility methods for parsing and generating time strings.
 *
 * @author Valli Vidhya Venkatesan
 */
public class TimeUtil {

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            long elapsed = System.currentTimeMillis() - dateMillis;
            if (elapsed < 60000) {
                return (elapsed / 1000) + "s";
            } else if (elapsed < 3600000) {
                return (elapsed / (1000 * 60)) + "m";
            } else if (elapsed < 86400000) {
                return (elapsed / (1000 * 60 * 60)) + "h";
            } else if (elapsed < 604800000) {
                return (elapsed / (1000 * 60 * 60 * 24)) + "d";
            } else if (elapsed < 2419200000L) {
                return (elapsed / (1000 * 60 * 60 * 24 * 7)) + "w";
            }
        } catch (ParseException e) {
            Log.e("ERROR", "Failed to parse the date string: " + rawJsonDate, e);
        }

        return relativeDate;
    }

    public static String getCurrentTime(long timeInMillis) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        long dateMillis = timeInMillis;
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        return relativeDate;
    }

    public static String getTwitterFormatForCurrentTime() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat);
        String formattedDate = sf.format(new Date());
        return formattedDate;
    }
}
