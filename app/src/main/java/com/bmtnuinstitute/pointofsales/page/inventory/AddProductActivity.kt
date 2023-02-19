package com.bmtnuinstitute.pointofsales.page.inventory

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
//import com.inyongtisto.myhelper.extension.showErrorDialog
//import com.inyongtisto.myhelper.extension.showSuccessDialog
//import com.inyongtisto.myhelper.extension.toMultipartBody
import com.bmtnuinstitute.pointofsales.R
import com.bmtnuinstitute.pointofsales.preferences.PrefManager
import com.bmtnuinstitute.pointofsales.retrofit.response.produk.AddProductResponse
import com.bmtnuinstitute.pointofsales.retrofit.service.ApiService
import kotlinx.android.synthetic.main.activity_add_product.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddProductActivity : AppCompatActivity() {

    companion object {
        const val CAT_ID = "CAT_ID"
        const val SKU = "SKU"
    }

    private val api by lazy { ApiService.cashier }
    private val pref by lazy { PrefManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        add_photo.setOnClickListener {
            imagePicker()
        }

        btnScan.setOnClickListener {
            val i = Intent(this@AddProductActivity, ScanQrCodeActivity::class.java)
            val id_kategori = intent.getStringExtra(CAT_ID).toString()
            i.putExtra("CAT_ID", id_kategori)
            startActivity(i)
            finish()
        }

//        val sku = intent.getStringExtra(SKU).toString()!!
//        textSku.setText(sku)
//
    }

    private fun imagePicker() {
        ImagePicker.with(this)
            .crop()
            .maxResultSize(512, 512)//Crop image and let user choose aspect ratio.
            .createIntentFromDialog { launcher.launch(it) }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                // Use the uri to load the image
                Log.d("Image", uri.toString())
                val fileUri: Uri = uri
                viewImage(File(fileUri.path!!))
            }
        }

    private fun viewImage(file: File) {
        Glide.with(this).load(file).into(add_photo)
        btnProduk.setOnClickListener {
            uploadProduct(file)
        }
    }

    private fun uploadProduct(file: File) {

//        val fileName = file.toMultipartBody()
        val requestBody = RequestBody.create("multipart/from-file".toMediaTypeOrNull(), file)
        val partImage = MultipartBody.Part.createFormData("image", file.name, requestBody)
        val id_kategori = intent.getStringExtra(CAT_ID).toString()
        val nameProduct = edt_nama_produuct.text.toString()
        val make_by = pref.getString("pref_user_username")!!.toString()
        Log.d("username", make_by.toString())

        api.addProduct(
            RequestBody.create("text/plain".toMediaTypeOrNull(), id_kategori),
            RequestBody.create("text/plain".toMediaTypeOrNull(), nameProduct),
            RequestBody.create("text/plain".toMediaTypeOrNull(), make_by),
            RequestBody.create("text/plain".toMediaTypeOrNull(), edt_price.text.toString()),
            RequestBody.create("text/plain".toMediaTypeOrNull(), textSku.text.toString()),
            partImage
        ).enqueue(object :
            Callback<AddProductResponse> {
            override fun onResponse(
                call: Call<AddProductResponse>,
                response: Response<AddProductResponse>
            ) {
                if (response.isSuccessful) {
//                    showSuccessDialog("Berhasil", "Upload Produk berhasil!")
                    finish()
//                    startActivity(Intent(applicationContext, ProductActivity::class.java))
                }
            }

            override fun onFailure(call: Call<AddProductResponse>, t: Throwable) {
//                showErrorDialog("Gagal", t.message.toString())
            }

        })


    }

}

