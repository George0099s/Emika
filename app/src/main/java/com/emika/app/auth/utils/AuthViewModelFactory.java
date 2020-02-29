package com.emika.app.auth.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.emika.app.auth.data.AuthRepository;
import com.emika.app.auth.viewmodel.AuthViewModel;

public class AuthViewModelFactory extends ViewModelProvider.NewInstanceFactory {
     private AuthRepository authRepository;

        public AuthViewModelFactory(AuthRepository authRepository) {
            this.authRepository = authRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == AuthViewModel.class) {
                return (T) new AuthViewModel(authRepository);
            }
            return null;
        }

    }
