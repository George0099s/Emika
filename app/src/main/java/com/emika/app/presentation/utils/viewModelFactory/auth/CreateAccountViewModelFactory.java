package com.emika.app.presentation.utils.viewModelFactory.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.emika.app.presentation.viewmodel.auth.CreateAccountViewModel;

public class CreateAccountViewModelFactory extends ViewModelProvider.NewInstanceFactory  {

    private String token;

    public CreateAccountViewModelFactory(String token) {
        this.token = token;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == CreateAccountViewModel.class) {
            return (T) new CreateAccountViewModel(token);
        }
        return null;
    }
}
