package com.emika.app.di;

import com.emika.app.presentation.adapter.calendar.ProjectAdapter;
import com.emika.app.presentation.adapter.calendar.SectionAdapter;
import com.emika.app.presentation.adapter.calendar.SelectCurrentUserAdapter;
import com.emika.app.presentation.ui.MainActivity;
import com.emika.app.presentation.ui.calendar.AddTaskActivity;
import com.emika.app.presentation.ui.calendar.BoardFragment;
import com.emika.app.presentation.ui.calendar.BottomSheetAddTaskSelectProject;
import com.emika.app.presentation.ui.calendar.BottomSheetCalendarSelectUser;
import com.emika.app.presentation.ui.calendar.TaskInfoActivity;
import com.emika.app.presentation.ui.profile.ProfileFragment;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;
import com.emika.app.presentation.viewmodel.calendar.BottomSheetAddTaskSelectProjectViewModel;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {UserModule.class})
public interface UserComponent {
    void inject(CalendarViewModel calendarViewModel);
    void inject(MainActivity mainActivity);
    void inject(ProfileFragment profileFragment);
    void inject(BoardFragment boardFragment);
    void inject(AddTaskActivity addTaskActivity);
    void inject(TaskInfoActivity taskInfoActivity);
    void inject(BottomSheetCalendarSelectUser bottomSheetCalendarSelectUser);
    void inject(SelectCurrentUserAdapter adapter);
    void inject(AddTaskListViewModel addTaskListViewModel);
    void inject(ProjectAdapter adapter);
    void inject(SectionAdapter adapter);
    void inject(BottomSheetAddTaskSelectProjectViewModel bottomSheetAddTaskSelectProjectViewModel);
    void inject(BottomSheetAddTaskSelectProject bottomSheetAddTaskSelectProject);
    void inject(TaskInfoViewModel taskInfoViewModel);
}
