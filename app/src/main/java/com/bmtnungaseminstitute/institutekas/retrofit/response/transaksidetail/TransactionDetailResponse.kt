package com.bmtnungaseminstitute.institutekas.retrofit.response.transaksidetail


data class TransactionDetailResponse(
    val `data`: ArrayList<TransactionDetail>,
    val error: Boolean
)