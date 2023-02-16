package com.bmtnuinstitute.pointofsales.page.categories

import androidx.appcompat.app.AppCompatActivity
import com.bmtnuinstitute.pointofsales.R
import android.os.Bundle
import android.widget.Toast
//import com.inyongtisto.myhelper.extension.showErrorDialog
//import com.inyongtisto.myhelper.extension.showSuccessDialog
import com.bmtnuinstitute.pointofsales.preferences.PrefManager
import com.bmtnuinstitute.pointofsales.retrofit.response.SubmitResponse
import com.bmtnuinstitute.pointofsales.retrofit.service.ApiService
import kotlinx.android.synthetic.main.activity_categories.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesActivity : AppCompatActivity() {

    private val api by lazy { ApiService.cashier }
    private val pref by lazy { PrefManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        val nameCategories = nameCategory.text.toString()
        val make_by = pref.getString("pref_user_username")!!.toString()

        buttonSave.setOnClickListener {
            api.addCategories(nameCategory.text.toString(),make_by).enqueue(object : Callback<SubmitResponse> {
                override fun onResponse(
                    call: Call<SubmitResponse>,
                    response: Response<SubmitResponse>
                ) {
                    if (response.isSuccessful){
//                        showSuccessDialog("Sukses", "Kategori berhasil di tambahkan!")
                        finish()
                    }
                }

                override fun onFailure(call: Call<SubmitResponse>, t: Throwable) {
//                    Toast.makeText(this@CategoriesActivity, "Error: ", t.message.toString(), Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
}