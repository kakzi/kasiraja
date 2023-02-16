package com.bmtnuinstitute.pointofsales.retrofit.response.keranjang

import java.io.Serializable

data class Cart(
    val created_at: String,
    val image: String,
//    val harga: String,
    val harga: Int,
    val id_keranjang: String,
    val id_produk: String,
    val nama_produk: String,
//    val jumlah: String,
//    val total: String,
    var jumlah: Int,
    var total: Int,
    val updated_at: String,
    val username: String
): Serializable