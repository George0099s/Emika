package com.emika.app.presentation.utils.viewModelFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.emika.app.presentation.viewmodel.CreateAccountViewModel;

public class CreateAccountViewModelFactory extends ViewModelProvider.NewInstanceFactory  {

    private String token, firstName, lastName, jobTitle, bio;

    public CreateAccountViewModelFactory(String token, String firstName, String lastName, String jobTitle, String bio) {
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.bio = bio;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == CreateAccountViewModel.class) {
            return (T) new CreateAccountViewModel(token, firstName, lastName, jobTitle, bio);
        }
        return null;
    }
}
