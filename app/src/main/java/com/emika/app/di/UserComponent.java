package com.emika.app.di;

import android.app.TaskInfo;

import com.emika.app.presentation.ui.MainActivity;
import com.emika.app.presentation.ui.calendar.AddTaskActivity;
import com.emika.app.presentation.ui.calendar.BoardFragment;
import com.emika.app.presentation.ui.calendar.TaskInfoActivity;
import com.emika.app.presentation.ui.profile.ProfileFragment;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Subcomponent;

@Singleton
@Component(modules = {UserModule.class})
public interface UserComponent {
    void inject(MainActivity mainActivity);
    void inject(ProfileFragment profileFragment);
    void inject(BoardFragment boardFragment);
    void inject(AddTaskActivity addTaskActivity);
    void inject(TaskInfoActivity taskInfoActivity);
}
