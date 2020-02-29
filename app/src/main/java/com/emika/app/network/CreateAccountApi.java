package com.emika.app.network;

import com.emika.app.auth.data.pojo.ModelEmail;
import com.emika.app.data.pojo.ModelToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CreateAccountApi {


    @FormUrlEncoded
    @POST("public_api/account/update")
    Call<ModelToken> updateAccountInfo(@Query("token") String token,
                                       @Field("first_name") String firstName,
                                       @Field("last_name") String lastName,
                                       @Field("job_title") String jobTitle,
                                       @Field("bio") String bio);

    @GET("public_api/auth/validate_token")
    Call<ModelToken> validateToken(@Query("token") String token);

    @GET("public_api/auth/check_email")
    Call<ModelEmail> checkEmail(@Query("token") String token,
                                @Query("email") String email);

//    'first_name', 'last_name', 'patronymic_name', 'phone', 'gender', 'job_title', 'bio'
}
