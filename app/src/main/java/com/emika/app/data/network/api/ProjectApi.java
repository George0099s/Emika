package com.emika.app.data.network.api;

import com.emika.app.data.network.pojo.project.ModelProject;
import com.emika.app.data.network.pojo.project.ModelSection;
import com.emika.app.data.network.pojo.project.ProjectCreation;
import com.emika.app.data.network.pojo.project.SectionCreation;
import com.google.gson.JsonArray;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProjectApi {
    @GET("public_api/projects/fetch")
    Call<ModelProject> getAllProjects(@Query("token") String token);
    @GET("public_api/projects/sections/fetch")
    Call<ModelSection> getAllSections(@Query("token") String token);



    @FormUrlEncoded
    @POST("/public_api/projects/create")
    Call<ProjectCreation> createProject(@Query("token") String token,
                                        @Field("members") JSONArray members,
                                        @Field("name") String projectName,
                                        @Field("description") String description,
                                        @Field("active_fields") JSONArray activeFields
    );

    @FormUrlEncoded
    @POST("/public_api/projects/sections/create")
    Call<SectionCreation> createSection(@Query("token") String token,
                                        @Field("name") String sectionName,
                                        @Field("status") String status,
                                        @Field("order") String order,
                                        @Field("project_id") String projectId

    );

    @FormUrlEncoded
    @POST("/public_api/projects/{project_id}/update")
    Call<ProjectCreation> updateProject(
            @Path("project_id") String projectId,
            @Query("token") String token,
            @Field("description") String description,
            @Field("name") String name,
            @Field("status") String status,
            @Field("active_fields") JSONArray activeFields,
            @Field("members") JSONArray members,
            @Field("color") String color);

    @FormUrlEncoded
    @POST("/public_api/projects/sections/{section_id}/update")
    Call<SectionCreation> updateSection(
            @Path("section_id") String sectionId,
            @Query("token") String token,
            @Field("name") String name,
            @Field("status") String status,
            @Field("order") String order,
            @Field("project_id") String projectId);

    @FormUrlEncoded
    @POST("/public_api/projects/sections/update_order")
    Call<SectionCreation> updateSectionsOrder(
            @Query("token") String token,
            @Field("sections") JSONArray orders);

}
