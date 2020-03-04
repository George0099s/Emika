package com.emika.app.data.network.api;

import com.emika.app.data.network.pojo.company.Model;
import com.emika.app.data.network.pojo.singIn.ModelAuth;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CompanyApi {

    @FormUrlEncoded
    @POST("public_api/company/create")
    Call<ModelAuth> createCompany(@Query("token") String token,
                               @Field("name") String name,
                               @Field("size") String size);

    @POST("public_api/company/invitations/{invitation_id}/accept")
    Call<ModelAuth> acceptInvite(@Path("invitation_id") String name,
                                 @Query("token") String token);

    @GET("public_api/company/invitations/check")
    Call<Model> checkInvitation(@Query("token") String token);


}
