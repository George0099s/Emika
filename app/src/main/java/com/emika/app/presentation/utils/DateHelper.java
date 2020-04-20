package com.emika.app.presentation.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

    public static String compareDate(int s) {
        Date currentDate = new Date();  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -15);
        Calendar b = c;
        b.add(Calendar.DATE, s);// number of days to add
        String startDate = sdf.format(b.getTime());
        return startDate;
    }

    public static String getDate(String date){
        if (date != null && !date.equals("null")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatted = new SimpleDateFormat("d MMM");

            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(date));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            // number of days to add
            if (date != null)
                date = formatted.format(c.getTime());
        }
        return date;

    }
    public static String getDatePicker(String date){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
            SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-d");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // number of days to add
        if (date != null)
            date = formatted.format(c.getTime());
            return date;
    }

    public static String getDatOfWeek(int s){
        String dayOfWeek;
        SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -15);
        Calendar b = c;
        b.add(Calendar.DATE, s);// number of days to add
        dayOfWeek = weekFormat.format(b.getTime());
    return dayOfWeek;
    }
}
