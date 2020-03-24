package com.emika.app.data.db.callback.calendar;

import com.emika.app.data.db.entity.MemberEntity;

import java.util.List;

public interface MemberDbCallback {
    void onMembersLoaded(List<MemberEntity> memberEntityList);
}
