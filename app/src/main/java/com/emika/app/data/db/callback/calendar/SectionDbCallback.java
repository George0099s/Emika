package com.emika.app.data.db.callback.calendar;

import com.emika.app.data.db.entity.SectionEntity;

import java.util.List;

public interface SectionDbCallback {
    void onSectionLoaded(List<SectionEntity> sections);
}
