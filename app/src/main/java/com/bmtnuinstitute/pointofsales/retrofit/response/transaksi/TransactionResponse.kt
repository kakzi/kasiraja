package com.bmtnuinstitute.pointofsales.retrofit.response.transaksi

data class TransactionResponse(
    val `data`: List<Transaction>,
    val error: Boolean
)