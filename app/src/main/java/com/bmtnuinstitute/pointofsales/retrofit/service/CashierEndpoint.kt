package com.bmtnuinstitute.pointofsales.retrofit.service

import com.bmtnuinstitute.pointofsales.retrofit.response.SubmitResponse
import com.bmtnuinstitute.pointofsales.retrofit.response.dashboard.OmsetResponse
import com.bmtnuinstitute.pointofsales.retrofit.response.kategori.CategoryResponse
import com.bmtnuinstitute.pointofsales.retrofit.response.keranjang.CartResponse
import com.bmtnuinstitute.pointofsales.retrofit.response.produk.AddProductResponse
import com.bmtnuinstitute.pointofsales.retrofit.response.produk.ProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CashierEndpoint {

    @FormUrlEncoded
    @POST("keranjang")
    fun keranjang(
        @Field("username") username: String,
        @Field("id_produk") id_produk: Int,
        @Field("jumlah") jumlah: Int
    ): Call<SubmitResponse>

    @FormUrlEncoded
    @POST("produk/delete_product")
    fun deleteProduk(
        @Field("id") id: String,
    ): Call<SubmitResponse>

    @GET("omset")
    fun getOmset(
        @Query("username") username: String
    ): Call<OmsetResponse>

    @GET("keranjang")
    fun keranjang(
        @Query("username") username: String
    ): Call<CartResponse>

    @DELETE("keranjang/{id}")
    fun hapusKeranjang(
        @Path("id") id: String
    ): Call<SubmitResponse>

    @FormUrlEncoded
    @PUT("keranjang/{id}")
    fun updateKeranjang(
        @Path("id") id: String,
        @Field("jumlah") jumlah: Int
    ): Call<SubmitResponse>

    @GET("kategori")
    fun kategori(
        @Query("make_by") make_by:String
    ): Call<CategoryResponse>


    @FormUrlEncoded
    @POST("kategori/add_category")
    fun addCategories(
        @Field("nama_kategori") nama_kategori:String,
        @Field("make_by") make_by:String
    ): Call<SubmitResponse>

    @GET("produk")
    fun listProduk(
        @Query("nama") nama: String,
        @Query("id_kategori") id_kategori: Int?,
        @Query("make_by") make_by: String?
    ): Call<ProductResponse>

    @FormUrlEncoded
    @POST("checkout")
    fun checkout(
        @Field("username") username: String,
        @Field("nama_pelanggan") nama_pelanggan: String,
        @Field("total") total: String,
        @Field("no_meja") no_meja: String,
        @Field("catatan") catatan: String
    ): Call<SubmitResponse>

    @Multipart
    @POST("produk/add_product")
    fun addProduct(
        @Part("id_kategori") id_kategori: RequestBody,
        @Part("nama_produk") nama_produk: RequestBody,
        @Part("make_by") make_by: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("barcode") barcode: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<AddProductResponse>

    @Multipart
    @POST("produk/edit_product")
    fun editProduct(
        @Part("id") id: RequestBody,
        @Part("id_kategori") id_kategori: RequestBody,
        @Part("nama_produk") nama_produk: RequestBody,
        @Part("make_by") make_by: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("barcode") barcode: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<AddProductResponse>

    @Multipart
    @POST("produk/edit_product")
    fun editProductWithoutImage(
        @Part("id") id: RequestBody,
        @Part("id_kategori") id_kategori: RequestBody,
        @Part("nama_produk") nama_produk: RequestBody,
        @Part("make_by") make_by: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("barcode") barcode: RequestBody
    ): Call<AddProductResponse>
}