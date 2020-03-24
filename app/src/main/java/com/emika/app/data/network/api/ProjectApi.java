package com.emika.app.data.network.api;

import com.emika.app.data.network.pojo.project.ModelProject;
import com.emika.app.data.network.pojo.project.ModelSection;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProjectApi {
    @GET("public_api/projects/fetch")
    Call<ModelProject> getAllProjects(@Query("token") String token);
    @GET("public_api/projects/sections/fetch")
    Call<ModelSection> getAllSections(@Query("token") String token);
}
