package com.emika.app.presentation.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

    public static String getDate(int s) {
        String dt = "2020-03-01";  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatted = new SimpleDateFormat("MMM dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, s);  // number of days to add
        dt = sdf.format(c.getTime());
        return dt;
    }

    public static String compareDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatted = new SimpleDateFormat("MMM dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
         // number of days to add
        date = sdf.format(c.getTime());
        return date;
    }
}
