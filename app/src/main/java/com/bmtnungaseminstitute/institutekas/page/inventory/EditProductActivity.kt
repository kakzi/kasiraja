package com.bmtnungaseminstitute.institutekas.page.inventory

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
//import com.inyongtisto.myhelper.extension.showErrorDialog
//import com.inyongtisto.myhelper.extension.showSuccessDialog
//import com.inyongtisto.myhelper.extension.toMultipartBody
import com.bmtnungaseminstitute.institutekas.R
import com.bmtnungaseminstitute.institutekas.preferences.PrefManager
import com.bmtnungaseminstitute.institutekas.retrofit.response.produk.AddProductResponse
import com.bmtnungaseminstitute.institutekas.retrofit.service.ApiService
import com.bmtnungaseminstitute.institutekas.retrofit.service.BASE_URL
import com.bmtnungaseminstitute.institutekas.retrofit.service.URL_IMAGE
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_add_product.add_photo
import kotlinx.android.synthetic.main.activity_add_product.btnProduk
import kotlinx.android.synthetic.main.activity_add_product.btnScan
import kotlinx.android.synthetic.main.activity_add_product.edt_nama_produuct
import kotlinx.android.synthetic.main.activity_add_product.edt_price
import kotlinx.android.synthetic.main.activity_add_product.textSku
import kotlinx.android.synthetic.main.activity_edit_product.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditProductActivity : AppCompatActivity() {

    companion object {
        const val CAT_ID = "CAT_ID"
        const val SKU = "SKU"
        const val NAME_PRODUCT = "NAME_PRODUCT"
        const val ID_PRODUCT = "ID_PRODUCT"
        const val PRICE = "PRICE"
        const val IMAGE = "IMAGE"
    }

    private val api by lazy { ApiService.cashier }
    private val pref by lazy { PrefManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        add_photo.setOnClickListener {
            imagePicker()
        }

        btnScan.setOnClickListener {
            val i = Intent(this@EditProductActivity, ScanQrCodeActivity::class.java)
            val id_kategori = intent.getStringExtra(CAT_ID).toString()
            i.putExtra("CAT_ID", id_kategori)
            startActivity(i)
            finish()
        }

        val sku = intent.getStringExtra(SKU).toString()!!
        textSku.setText(sku)
        val id_kategori = intent.getStringExtra(CAT_ID).toString()
        val name = intent.getStringExtra(NAME_PRODUCT).toString()
        val price = intent.getStringExtra(PRICE).toString()
        val image = intent.getStringExtra(IMAGE).toString()
        val id_product = intent.getStringExtra(ID_PRODUCT).toString()
        edt_nama_produuct.setText(name)
        edt_price.setText(price)
        Glide.with(this).load(URL_IMAGE+image).into(add_photo)

//        btnEditProduk.setOnClickListener {
//            uploadProduct(file)
//        }

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
        btnEditProduk.setOnClickListener {
            uploadProduct(file)
        }
    }

    private fun uploadProduct(file: File) {

        val requestBody = RequestBody.create("multipart/from-file".toMediaTypeOrNull(), file)
        val partImage = MultipartBody.Part.createFormData("image", file.name, requestBody)
        val id_kategori = intent.getStringExtra(CAT_ID).toString()
        val id_product = intent.getStringExtra(ID_PRODUCT).toString()
        val nameProduct = edt_nama_produuct.text.toString()
        val make_by = pref.getString("pref_user_username")!!.toString()
        Log.d("username", make_by.toString())

        if (partImage == null){
            api.editProductWithoutImage(
                RequestBody.create("text/plain".toMediaTypeOrNull(), id_product),
                RequestBody.create("text/plain".toMediaTypeOrNull(), id_kategori),
                RequestBody.create("text/plain".toMediaTypeOrNull(), nameProduct),
                RequestBody.create("text/plain".toMediaTypeOrNull(), make_by),
                RequestBody.create("text/plain".toMediaTypeOrNull(), edt_price.text.toString()),
                RequestBody.create("text/plain".toMediaTypeOrNull(), textSku.text.toString())
            ).enqueue(object :
                Callback<AddProductResponse> {
                override fun onResponse(
                    call: Call<AddProductResponse>,
                    response: Response<AddProductResponse>
                ) {
                    if (response.isSuccessful) {
//                        showSuccessDialog("Sukses", "Ubah Produk berhasil!")
                        finish()
//                    startActivity(Intent(applicationContext, ProductActivity::class.java))
                    }
                }

                override fun onFailure(call: Call<AddProductResponse>, t: Throwable) {
//                    showErrorDialog("Gagal", t.message.toString())
                }

            })
        } else {
            api.editProduct(
                RequestBody.create("text/plain".toMediaTypeOrNull(), id_product),
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
//                        showSuccessDialog("Sukses", "Ubah Produk berhasil!")
                        finish()
//                    startActivity(Intent(applicationContext, ProductActivity::class.java))
                    }
                }

                override fun onFailure(call: Call<AddProductResponse>, t: Throwable) {
//                    showErrorDialog("Gagal", t.message.toString())
                }

            })
        }



    }

}

