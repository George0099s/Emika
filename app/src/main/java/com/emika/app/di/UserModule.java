package com.emika.app.di;

import com.emika.app.presentation.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UserModule {
    @Provides
    @Singleton
    static User getUserInstance() {
        return new User();
    }
}
