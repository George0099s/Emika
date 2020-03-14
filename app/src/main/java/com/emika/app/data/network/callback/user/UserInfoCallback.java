package com.emika.app.data.network.callback.user;

import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;
import com.emika.app.data.network.pojo.user.Payload;

public interface UserInfoCallback {
    void updateInfo(UpdateUserModel model);
    void getUserInfo(Payload userModel);
}
