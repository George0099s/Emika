package com.emika.app.presentation.utils;

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


}
