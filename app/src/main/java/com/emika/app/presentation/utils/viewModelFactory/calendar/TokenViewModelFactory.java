package com.emika.app.presentation.utils.viewModelFactory.calendar;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.emika.app.presentation.viewmodel.StartActivityViewModel;
import com.emika.app.presentation.viewmodel.calendar.AddTaskListViewModel;
import com.emika.app.presentation.viewmodel.calendar.BottomSheetAddTaskSelectProjectViewModel;
import com.emika.app.presentation.viewmodel.calendar.BottomSheetSelectEpicLinksViewModel;
import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel;
import com.emika.app.presentation.viewmodel.chat.ChatViewModel;
import com.emika.app.presentation.viewmodel.profile.AllMembersViewModel;
import com.emika.app.presentation.viewmodel.profile.EditProfileViewModel;
import com.emika.app.presentation.viewmodel.profile.ManageInviteViewModel;
import com.emika.app.presentation.viewmodel.profile.MemberViewModel;
import com.emika.app.presentation.viewmodel.profile.ProfileViewModel;

public class TokenViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private String token;

    public TokenViewModelFactory(String token) {
        this.token = token;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == CalendarViewModel.class) {
            return (T) new CalendarViewModel(token);
        }

        else if (modelClass == TaskInfoViewModel.class){
            return (T) new TaskInfoViewModel(token);
        }

        else if (modelClass == ProfileViewModel.class){
            return (T) new ProfileViewModel(token);
        }
        else if (modelClass == AddTaskListViewModel.class){
            return (T) new AddTaskListViewModel(token);
        }
        else if (modelClass == BottomSheetAddTaskSelectProjectViewModel.class){
            return (T) new BottomSheetAddTaskSelectProjectViewModel(token);
        }
        else if (modelClass == StartActivityViewModel.class){
            return (T) new StartActivityViewModel(token);
        }
        else if (modelClass == BottomSheetSelectEpicLinksViewModel.class){
            return (T) new BottomSheetSelectEpicLinksViewModel(token);
        }
        else if (modelClass == ChatViewModel.class){
            return (T) new ChatViewModel(token);
        }
        else if (modelClass == AllMembersViewModel.class){
            return (T) new AllMembersViewModel(token);
        }
        else if (modelClass == MemberViewModel.class){
            return (T) new MemberViewModel(token);
        }
        else if (modelClass == EditProfileViewModel.class){
            return (T) new EditProfileViewModel(token);
        }
        else if (modelClass == ManageInviteViewModel.class){
            return (T) new ManageInviteViewModel(token);
        }
        return null;
    }

}
