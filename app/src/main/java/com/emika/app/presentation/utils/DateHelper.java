package com.emika.app.presentation.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static String getDate(String date) {
        if (date != null && !date.equals("null")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatted = new SimpleDateFormat("d MMM");

            Calendar c = Calendar.getInstance();
            int today = c.getTime().getDay();
            int newDay = 0;
            try {
                c.setTime(sdf.parse(date));
                newDay = c.getTime().getDay();

//                Log.d("1234", "getDate: " + newDay + " " + today);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // number of days to add
            if (date != null) {
//                if (today == newDay)
//                    date = "Today";
//                else if (today < newDay && newDay - today == 1)
//                    date = "Tomorrow";
//                else if (today > newDay && today - newDay == 1)
//                    date = "Yesterday";
//                else
                date = formatted.format(c.getTime());
            }
        }
        return date;
    }

    public static String getDatePicker(String date) {
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

    public static String getLoggedTimDayInfo(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d hh:mm:ss");
        SimpleDateFormat formatted = new SimpleDateFormat("hh:mm");
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

    public static String getDatOfWeek(int s) {
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
