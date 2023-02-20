package com.bmtnungaseminstitute.institutekas.retrofit.response.login

data class LoginResponse(
    val data: Login,
    val message: String,
    val error: Boolean,
    val status: Int
)