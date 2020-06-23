package com.emika.app.di;

import com.emika.app.domain.repository.auth.CreateUserRepository;
import com.emika.app.presentation.adapter.calendar.CommentAdapter;
import com.emika.app.presentation.adapter.calendar.DayInfoTaskAdapter;
import com.emika.app.presentation.adapter.calendar.EpicLinksAdapter;
import com.emika.app.presentation.adapter.calendar.ItemAdapter;
import com.emika.app.presentation.adapter.calendar.ProjectAdapter;
import com.emika.app.presentation.adapter.calendar.SectionAdapter;
import com.emika.app.presentation.adapter.calendar.SelectCurrentUserAdapter;
import com.emika.app.deprecated.ChatAdapterOld;
import com.emika.app.presentation.ui.calendar.BottomSheetEditSections;
import com.emika.app.presentation.ui.calendar.BottomSheetProjectAddMember;
import com.emika.app.presentation.ui.calendar.Cal;
import com.emika.app.presentation.ui.chat.ChatActivity;
import com.emika.app.presentation.adapter.chat.QuickAnswerAdapter;
import com.emika.app.presentation.adapter.profile.AllMembersAdapter;
import com.emika.app.presentation.ui.MainActivity;
import com.emika.app.presentation.ui.auth.CreateAccountFragment;
import com.emika.app.presentation.ui.calendar.AddTask;
import com.emika.app.presentation.ui.calendar.AddTaskActivity;
import com.emika.app.presentation.ui.calendar.BoardFragment;
import com.emika.app.presentation.ui.calendar.BottomSheetAddTaskSelectProject;
import com.emika.app.presentation.ui.calendar.BottomSheetCalendarSelectUser;
import com.emika.app.presentation.ui.calendar.BottomSheetDayInfo;
import com.emika.app.presentation.ui.calendar.BottomSheetSelectEpicLinks;
import com.emika.app.presentation.ui.calendar.Inbox;
import com.emika.app.presentation.ui.calendar.TaskInfo;
import com.emika.app.presentation.ui.calendar.TaskInfoActivity;
import com.emika.app.presentation.ui.chat.ChatFragment;
import com.emika.app.presentation.ui.profile.CreateContactDialogFragment;
import com.emika.app.presentation.ui.profile.EditProfileActivity;
import com.emika.app.presentation.ui.profile.MemberActivity;
import com.emika.app.presentation.ui.profile.ProfileActivity;
import com.emika.app.presentation.ui.profile.ProfileFragment;
import com.emika.app.presentation.viewmodel.StartActivityViewModel;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;
import com.emika.app.presentation.viewmodel.calendar.BottomSheetAddTaskSelectProjectViewModel;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel;
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {UserModule.class})
public interface UserComponent {
    void inject(CalendarViewModel calendarViewModel);
    void inject(MainActivity mainActivity);
    void inject(ProfileFragment profileFragment);
    void inject(BoardFragment boardFragment);
    void inject(BottomSheetCalendarSelectUser bottomSheetCalendarSelectUser);
    void inject(SelectCurrentUserAdapter adapter);
    void inject(AddTaskListViewModel addTaskListViewModel);
    void inject(ProjectAdapter adapter);
    void inject(SectionAdapter adapter);
    void inject(BottomSheetAddTaskSelectProjectViewModel bottomSheetAddTaskSelectProjectViewModel);
    void inject(BottomSheetAddTaskSelectProject bottomSheetAddTaskSelectProject);
    void inject(TaskInfoViewModel taskInfoViewModel);
    void inject(EpicLinksAdapter adapter);
    void inject(BottomSheetSelectEpicLinks bottomSheetSelectEpicLinks);
    void inject(ChatAdapterOld chatAdapter);
    void inject(ChatFragment chatFragment);
    void inject(ItemAdapter adapter);
    void inject(StartActivityViewModel startActivityViewModel);
    void inject(QuickAnswerAdapter quickAnswerAdapter);
    void inject(MemberActivity memberActivity);
    void inject(EditProfileActivity editProfileActivity);
    void inject(CreateUserRepository repository);
    void inject(CreateAccountFragment createAccountFragment);
    void inject(AllMembersAdapter allMembersAdapter);
    void inject(BottomSheetDayInfo bottomSheetDayInfo);
    void inject(CreateContactDialogFragment createContactDialogFragment);
    void inject(ProfileViewModel profileViewModel);
    void inject(AddTask addTaskFragment);
    void inject(DayInfoTaskAdapter dayInfoTaskAdapter);
    void inject(TaskInfo taskInfo);
    void inject(Inbox inbox);
    void inject(AddTaskActivity addTask);
    void inject(TaskInfoActivity addTask);
    void inject(ChatActivity chatActivity);
    void inject(ProfileActivity profile);
    void inject(Cal calendar);
    void inject(CommentAdapter adapter);
    void inject(BottomSheetProjectAddMember bottomSheet);
    void inject(BottomSheetEditSections bottomSheet);
}
