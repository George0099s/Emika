package com.emika.app.data.network.api;

import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel;
import com.emika.app.data.network.pojo.user.Model;

import org.json.JSONArray;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {


    @FormUrlEncoded
    @POST("public_api/account/update")
    Call<UpdateUserModel> updateAccountInfo(@Query("token") String token,
                                            @Field("first_name") String firstName,
                                            @Field("last_name") String lastName,
                                            @Field("job_title") String jobTitle,
                                            @Field("bio") String bio,
                                            @Field("contacts")JSONArray contacts);


    @GET("public_api/account/info")
    Call<Model> getUserInfo(@Query("token") String token);

    @Multipart
    @POST("public_api/account/upload_picture")
    Call<UpdateUserModel> updateUserImage(
            @Query("token") String token,
            @Part MultipartBody.Part file);


}
