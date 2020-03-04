package com.emika.app.presentation.utils.viewModelFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.emika.app.presentation.viewmodel.AuthViewModel;

public class AuthViewModelFactory extends ViewModelProvider.NewInstanceFactory {
     private String token;

        public AuthViewModelFactory(String token) {
            this.token = token;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == AuthViewModel.class) {
                return (T) new AuthViewModel(token);
            }
            return null;
        }

    }
