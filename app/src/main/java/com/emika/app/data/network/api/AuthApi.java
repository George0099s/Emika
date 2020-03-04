package com.emika.app.data.network.api;

import com.emika.app.data.network.pojo.ModelToken;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.data.network.pojo.ModelEmail;
import com.emika.app.data.network.pojo.singUp.ModelSignUp;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {

    @POST("public_api/auth/create_token")
    Call<ModelToken> createToken();

    @POST("public_api/auth/delete_token")
    Call<ModelToken> deleteToken(@Query("token")String token);

    @GET("public_api/auth/validate_token")
    Call<ModelToken> validateToken(@Query("token") String token);

    @GET("public_api/auth/check_email")
    Call<ModelEmail> checkEmail(@Query("token") String token,
                                @Query("email") String email);

    @POST("public_api/auth/log_out")
    Call<ModelAuth> logOut(@Query("token") String token);

    @FormUrlEncoded
    @POST("public_api/auth/sign_in")
    Call<ModelAuth> signIn(@Query("token") String token,
                           @Field("email") String email,
                           @Field("password") String password);

    @FormUrlEncoded
    @POST("public_api/auth/sign_up")
    Call<ModelSignUp> signUp(@Query("token") String token,
                             @Field("email") String email,
                             @Field("password") String password);


}
