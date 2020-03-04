package com.emika.app.data.network.callback;

import com.emika.app.data.network.pojo.company.Invitation;
import com.emika.app.data.network.pojo.singIn.ModelAuth;

import java.util.List;

public interface CompanyCallback {
    void invitations(List<Invitation> invitations);
    void accepted(ModelAuth model);
}
