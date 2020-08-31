package com.bikash.bikashBackend.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class DataUtils {
    public DataUtils() {
    }

    public static Integer getYearFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static String getStringDate(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }


    public static Date getExpirationTime(Long expireHours) {
        Date now = new Date();
        Long expireInMilis = TimeUnit.HOURS.toMillis(expireHours);
        return new Date(expireInMilis + now.getTime());
    }

    //Timestamp systemDate = new Timestamp(System.currentTimeMillis());
    public static boolean isSameDay(String databaseDate, String systemDate) {
        String databasedate = databaseDate.substring(0, 10);
        String currentDayDate = systemDate.substring(0, 10);
        if (databasedate.equals(currentDayDate)) {
            return true;
        }
        return false;
    }
}
