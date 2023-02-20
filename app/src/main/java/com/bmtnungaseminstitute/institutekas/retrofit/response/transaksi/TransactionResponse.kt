package com.bmtnungaseminstitute.institutekas.retrofit.response.transaksi

data class TransactionResponse(
    val `data`: List<Transaction>,
    val error: Boolean
)