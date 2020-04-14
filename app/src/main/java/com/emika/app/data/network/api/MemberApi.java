package com.emika.app.data.network.api;

import com.emika.app.data.network.pojo.member.ModelMember;
import com.emika.app.data.network.pojo.member.ModelShortMember;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MemberApi {

    @GET("public_api/members/fetch")
    Call<ModelShortMember> getAllMembers(@Query("token") String token);

    @GET("public_api/members/{member_id}/info")
    Call<ModelMember> getMember(@Path("member_id") String memberId,
                                @Query("token") String token);

}
