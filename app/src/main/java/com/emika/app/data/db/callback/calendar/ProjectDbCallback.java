package com.emika.app.data.db.callback.calendar;

import com.emika.app.data.db.entity.ProjectEntity;

import java.util.List;

public interface ProjectDbCallback {
    void onProjectLoaded(List<ProjectEntity> projectEntities);
}
