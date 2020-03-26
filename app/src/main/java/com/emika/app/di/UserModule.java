package com.emika.app.di;

import com.emika.app.presentation.ui.MainActivity;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UserModule {
    @Provides
    @Singleton
    static User getUserInstance() {
        return new User();
    }
    @Provides
    @Singleton
    static Assignee getAssigneeInstance() {
        return new Assignee();
    }
    @Provides
    @Singleton
    static Project getProjectInstance() {
        return new Project();
    }
    @Provides
    @Singleton
    static EpicLinks getEpicLinks() {
        return new EpicLinks();
    }
}
