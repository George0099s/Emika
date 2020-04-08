package com.emika.app.presentation.utils;

import android.graphics.drawable.Drawable;

import com.emika.app.R;

import java.util.HashMap;

public final class Constants {

    public static final String BASIC_URL = "https://api.emika.ai/";
    public static final String MY_PREFERENCES = "emika.app";
    public static HashMap<Integer, String> dateColumnMap = new HashMap<>();
    public static HashMap<String, String> priority = new HashMap<String, String>(){{
        put("normal", "Normal");
        put("low", "Low");
        put("high", "High");
        put("urgent", "Urgent");
    }};

//    public static HashMap<String, Drawable> contactImg = new HashMap<String, Drawable>(){{
//        put("email", R.drawable.);
//        put("telegram", "Low");
//        put("whatsapp", "High");
//        put("instagram", "Urgent");
//        put("facebook", "Normal");
//        put("linkedin", "Low");
//        put("wechat", "High");
//        put("vk", "Urgent");
//    }};


}
