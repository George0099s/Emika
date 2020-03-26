package com.emika.app.data.db.callback.calendar;

import com.emika.app.data.db.entity.EpicLinksEntity;

import java.util.List;

public interface EpicLinksDbCallback {
    void onEpicLinksLoaded(List<EpicLinksEntity> epicLinksEntities);
}
