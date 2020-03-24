package com.emika.app.data.db.callback.calendar;

import com.emika.app.data.db.entity.UserEntity;

public interface UserDbCallback {
    void onUserLoaded(UserEntity user);
}
