package com.bmtnuinstitute.pointofsales.retrofit.response.produk

import java.io.Serializable

data class Product(
    val created_at: CreatedAt,
    val deleted_at: Any,
    val image: String,
    val barcode: String,
    val harga: String,
    val id_kategori: String,
    val id_produk: String,
    val nama_produk: String,
    val updated_at: UpdatedAt
): Serializable