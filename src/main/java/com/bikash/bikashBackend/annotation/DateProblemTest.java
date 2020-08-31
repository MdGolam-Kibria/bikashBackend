package com.bikash.bikashBackend.annotation;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Timestamp;
import java.util.Date;

public class DateProblemTest {
    public static String databasedate = "2020-08-31 01:58:17";

    public static void main(String[] args) {
        //String datbaseDate = timeDate.substring(0, 10);
        //System.out.println(datbaseDate);
        //system date
        //System.out.println(String.valueOf(timestamp).substring(0, 10));
        Timestamp systemDate = new Timestamp(System.currentTimeMillis());
        if (isSameDay(databasedate, String.valueOf(systemDate))) {
            System.out.println("Congrats");
        } else{
            System.out.println("null");
    }

}

    public static boolean isSameDay(String databaseDate, String systemDate) {
        String databasedate = databaseDate.substring(0, 10);
        String currentDayDate = systemDate.substring(0, 10);
        if (databasedate.equals(currentDayDate)) {
            return true;
        }
        return false;
    }
}
