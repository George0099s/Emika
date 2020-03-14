package com.emika.app.presentation.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

    public static String compareDate(int s) {
        Date currentDate = new Date();  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatted = new SimpleDateFormat("MMM dd");
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -15);
        Calendar b = c;
        b.add(Calendar.DATE, s);// number of days to add
        String startDate = sdf.format(b.getTime());
        return startDate;
    }

    public static String getDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatted = new SimpleDateFormat("MMM dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
         // number of days to add
        date = formatted.format(c.getTime());
        return date;
    }
}
