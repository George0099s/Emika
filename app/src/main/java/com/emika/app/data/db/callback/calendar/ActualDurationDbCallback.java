package com.emika.app.data.db.callback.calendar;

import com.emika.app.data.db.entity.ActualDurationEntity;

import java.util.List;

public interface ActualDurationDbCallback {
    void onActualDurationLoaded(List<ActualDurationEntity> actualDurationEntities);
}
