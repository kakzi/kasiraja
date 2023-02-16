package com.bmtnuinstitute.pointofsales.retrofit.response.login

data class LoginResponse(
    val data: Login,
    val message: String,
    val error: Boolean,
    val status: Int
)