package com.emika.app.data.network.callback;

import com.emika.app.data.network.pojo.ModelToken;
import com.emika.app.data.network.pojo.singIn.ModelAuth;

public interface TokenCallback {
    void getToken(String token);
}
