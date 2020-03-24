package com.emika.app.data.network.api;

import com.emika.app.data.network.pojo.company.Model;
import com.emika.app.data.network.pojo.epiclinks.ModelEpicLinks;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EpicLinksApi {
    @GET("public_api/projects/epic_links/fetch")
    Call<ModelEpicLinks> getAllEpicLinks(@Query("token") String token);
}
