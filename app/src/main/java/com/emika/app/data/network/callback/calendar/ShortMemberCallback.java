package com.emika.app.data.network.callback.calendar;

import com.emika.app.data.network.pojo.member.PayloadShortMember;

import java.util.List;

public interface ShortMemberCallback {
    void allMembers(List<PayloadShortMember> shortMembers);
}
