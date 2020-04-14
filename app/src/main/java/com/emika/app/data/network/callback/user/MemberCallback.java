package com.emika.app.data.network.callback.user;

import com.emika.app.data.network.pojo.member.PayloadMember;

public interface MemberCallback {
    void onMemberInfoLoaded(PayloadMember member);
}
