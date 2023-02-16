package com.bmtnuinstitute.pointofsales.retrofit.response.transaksidetail


data class TransactionDetailResponse(
    val `data`: ArrayList<TransactionDetail>,
    val error: Boolean
)