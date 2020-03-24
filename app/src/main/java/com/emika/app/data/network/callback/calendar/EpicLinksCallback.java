package com.emika.app.data.network.callback.calendar;

import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;

import java.util.List;

public interface EpicLinksCallback {
    void onEpicLinksLoaded(List<PayloadEpicLinks> epicLinks);
}
