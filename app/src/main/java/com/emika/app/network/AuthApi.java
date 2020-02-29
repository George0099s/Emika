package com.emika.app.network;

import com.emika.app.auth.data.pojo.singIn.ModelAuth;
import com.emika.app.auth.data.pojo.ModelEmail;
import com.emika.app.auth.data.pojo.singUp.ModelSignUp;
import com.emika.app.data.pojo.ModelToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {

    @POST("public_api/auth/create_token")
    Call<ModelToken> createToken();

    @GET("public_api/auth/validate_token")
    Call<ModelToken> validateToken(@Query("token") String token);

    @GET("public_api/auth/check_email")
    Call<ModelEmail> checkEmail(@Query("token") String token,
                                @Query("email") String email);

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


//    @POST("public_api/posts/{post_id}/save")
//    Call<Token> saveDeletePost(@Path("post_id")String postId,
//                               @Query("token") String token);
//
//    @POST("public_api/people/{account_id}/start_dialog")
//    Call<DialogModel> startDia(@Path("account_id")String accountId,
//                               @Query("token") String token);
}
