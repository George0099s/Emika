package com.emika.app.data.network.callback.calendar;

import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual;

import java.util.List;

public interface DurationActualCallback {
    void onDurationLogDownloaded(List<PayloadDurationActual> durationActualList);
}
