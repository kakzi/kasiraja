package com.bmtnungaseminstitute.institutekas.retrofit.service

import com.bmtnungaseminstitute.institutekas.retrofit.response.chart.ChartResponse
import com.bmtnungaseminstitute.institutekas.retrofit.response.export.ExportResponse
import com.bmtnungaseminstitute.institutekas.retrofit.response.kasir.CashierResponse
import com.bmtnungaseminstitute.institutekas.retrofit.response.transaksi.TransactionResponse
import com.bmtnungaseminstitute.institutekas.retrofit.response.transaksidetail.TransactionDetailResponse
import retrofit2.Call
import retrofit2.http.*

interface OwnerEndpoint {

    @GET("kasir")
    fun kasir(): Call<CashierResponse>

    @FormUrlEncoded
    @POST("transaksi-date")
    fun transaksiDate(
        @Field("tgl_awal") tgl_awal: String,
        @Field("tgl_akhir") tgl_akhir: String,
        @Field("no_transaksi") no_transaksi: String,
        @Field("username") username: String
    ): Call<TransactionResponse>

    @FormUrlEncoded
    @POST("transaksi-kasir")
    fun transaksiKasir(
        @Field("username") username: String,
        @Field("no_transaksi") no_transaksi: String
    ): Call<TransactionResponse>

    @FormUrlEncoded
    @POST("transaksi_terakhir")
    fun transaksiLast(
        @Field("username") username: String
    ): Call<TransactionResponse>

    @FormUrlEncoded
    @POST("history")
    fun history(
        @Field("username") username: String
    ): Call<TransactionResponse>


    @FormUrlEncoded
    @POST("transaksi/detail")
    fun transaksiDetail(
        @Field("id_transaksi") id_transaksi: String
    ): Call<TransactionDetailResponse>

    @FormUrlEncoded
    @POST("chart")
    fun chart(
        @Field("tahun") tahun: String,
        @Field("username") username: String
    ): Call<ChartResponse>

    @GET("export-excel")
    fun exportExcel(
        @Query("tgl_awal") tgl_awal: String,
        @Query("tgl_akhir") tgl_akhir: String
    ): Call<ExportResponse>

    @GET("export-pdf")
    fun exportPdf(
        @Query("tgl_awal") tgl_awal: String,
        @Query("tgl_akhir") tgl_akhir: String
    ): Call<ExportResponse>
}