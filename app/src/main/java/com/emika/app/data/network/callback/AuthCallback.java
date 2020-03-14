package com.emika.app.data.network.callback;

import com.emika.app.data.network.pojo.PayloadEmail;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.data.network.pojo.singUp.ModelSignUp;

public interface AuthCallback {
    void callbackCheckedEmail(PayloadEmail payloadEmail);
    void callbackModelAuthSignIn(ModelAuth modelAuth);
    void callbackModelAuthSignUp(ModelSignUp modelSignUp);
    void callbackRestorePassword(ModelAuth modelAuth);
}
