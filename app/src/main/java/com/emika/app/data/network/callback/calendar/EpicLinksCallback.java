package com.emika.app.data.network.callback.calendar;

import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;

import java.util.List;

public interface EpicLinksCallback {
    void onEpicLinksDownloaded(List<PayloadEpicLinks> epicLinks);
    void onEpicLinkCreated(PayloadEpicLinks epicLink);
}
