package com.bmtnuinstitute.pointofsales.retrofit.response.transaksi

import java.io.Serializable

data class Transaction(
    val catatan: String,
    val created_at: String,
    val id_transaksi: String,
    val nama_pelanggan: String,
    val no_meja: String,
    val no_transaksi: String,
    val status_pembayaran: String,
    val total: String,
    val updated_at: String,
    val username: String
) : Serializable