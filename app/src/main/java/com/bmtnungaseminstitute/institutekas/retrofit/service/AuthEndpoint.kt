package com.bmtnungaseminstitute.institutekas.retrofit.service

import com.bmtnungaseminstitute.institutekas.retrofit.response.login.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface AuthEndpoint {

    @FormUrlEncoded
    @POST("login/login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>
}