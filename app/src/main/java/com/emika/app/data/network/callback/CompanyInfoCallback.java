package com.emika.app.data.network.callback;

import com.emika.app.data.network.pojo.companyInfo.PayloadCompanyInfo;

public interface CompanyInfoCallback {
    void onCompanyInfoDownloaded(PayloadCompanyInfo companyInfo);
}
