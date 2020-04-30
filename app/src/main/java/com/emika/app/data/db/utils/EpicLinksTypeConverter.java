package com.emika.app.data.db.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EpicLinksTypeConverter {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @TypeConverter
    public String fromArrayEpicLinks(List<String> epicLinks) {
        return epicLinks.stream().collect(Collectors.joining(","));
    }

    @TypeConverter
    public List<String> toStringEpicLinks(String data) {
        if (data !=null)
        return Arrays.asList(data.split(","));
        else return new ArrayList<>();
    }
}
