package com.bmtnuinstitute.pointofsales.retrofit.response.transaksidetail

import java.io.Serializable

data class TransactionDetail(
    val created_at: String,
    val image: String,
    val harga: String,
    val id_produk: String,
    val id_transaksi: String,
    val id_transaksi_detail: String,
    val jumlah: String,
    val nama_produk: String,
    val total: String,
    val updated_at: String
): Serializable