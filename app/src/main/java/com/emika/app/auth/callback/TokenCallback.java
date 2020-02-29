package com.emika.app.auth.callback;

import com.emika.app.auth.data.pojo.singIn.ModelAuth;
import com.emika.app.auth.data.pojo.PayloadEmail;
import com.emika.app.auth.data.pojo.singUp.ModelSignUp;

public interface TokenCallback {
    void callbackCheckedEmail(PayloadEmail payloadEmail);
    void callbackModelAuthSignIn(ModelAuth modelAuth);
    void callbackModelAuthSignUp(ModelSignUp modelSignUp);
}
