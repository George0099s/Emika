package com.emika.app.presentation.utils.viewModelFactory.calendar;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.emika.app.presentation.viewmodel.calendar.CalendarViewModel;
import com.emika.app.presentation.viewmodel.calendar.TaskInfoViewModel;
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
        return null;
    }

}
