package com.emika.app.presentation.utils.viewModelFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.emika.app.presentation.viewmodel.AuthViewModel;
import com.emika.app.presentation.viewmodel.JoinCompanyViewModel;

public class JoinCompanyViewModelFactory extends ViewModelProvider.NewInstanceFactory {
   private String token;

    public JoinCompanyViewModelFactory(String token) {
        this.token = token;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == JoinCompanyViewModel.class) {
            return (T) new JoinCompanyViewModel(token);
        }
        return null;
    }

}
