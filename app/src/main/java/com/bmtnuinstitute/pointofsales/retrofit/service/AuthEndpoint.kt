package com.bmtnuinstitute.pointofsales.retrofit.service

import com.bmtnuinstitute.pointofsales.retrofit.response.login.LoginResponse
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