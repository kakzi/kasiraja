package com.bmtnuinstitute.pointofsales.retrofit.response.login

data class Login(
    val created_at: String,
    val is_aktif: String,
    val level: String,
    val nama_toko: String,
    val email: String,
    val pin_secure: String,
    val phone: String,
    val apikey: String?,
    val nama: String,
    val password: String,
    val address: String,
    val updated_at: String,
    val username: String
)