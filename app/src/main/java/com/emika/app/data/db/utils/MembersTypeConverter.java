package com.emika.app.data.db.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MembersTypeConverter {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @TypeConverter
    public String fromArrayMembers(List<String> members) {
        return members.stream().collect(Collectors.joining(","));
    }

    @TypeConverter
    public List<String> toStringMembers(String data) {
        if (data !=null)
            return Arrays.asList(data.split(","));
        else return new ArrayList<>();
    }
}
