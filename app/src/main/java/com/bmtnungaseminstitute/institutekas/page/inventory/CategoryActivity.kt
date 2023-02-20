package com.bmtnungaseminstitute.institutekas.page.inventory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bmtnungaseminstitute.institutekas.R
import com.bmtnungaseminstitute.institutekas.preferences.PrefManager
import com.bmtnungaseminstitute.institutekas.retrofit.response.kategori.Category
import com.bmtnungaseminstitute.institutekas.retrofit.response.kategori.CategoryResponse
import com.bmtnungaseminstitute.institutekas.retrofit.service.ApiService
import kotlinx.android.synthetic.main.activity_category.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CategoryActivity : AppCompatActivity() {


    private val api by lazy { ApiService.cashier }
    private val pref by lazy { PrefManager(this) }

    private lateinit var categoryListAdapter: CategoryListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        listCategory()
        setupListCategory()
    }

    private fun setupListCategory() {
        categoryListAdapter =
            CategoryListAdapter(
                arrayListOf(),
                object :
                    CategoryListAdapter.OnAdapterListener {
                    override fun onClick(category: Category) {
//                        Toast.makeText(this@CategoryActivity, "Anda memilih kategori " + category.nama_kategori, Toast.LENGTH_SHORT).show()
                        val value = category.id_kategori
                        Log.d("Kategori ID :", value)
                        val i = Intent(this@CategoryActivity, AddProductActivity::class.java)
                        i.putExtra("CAT_ID", value)
                        startActivity(i)
                    }
                })
        list_category.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL
            adapter = categoryListAdapter
        }
    }

    private fun listCategory() {
        api.kategori(pref.getString("pref_user_username")!!)
            .enqueue(object : Callback<CategoryResponse> {
                override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

                override fun onResponse(
                    call: Call<CategoryResponse>,
                    response: Response<CategoryResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            categoryResponse(it)
                        }
                    }
                }
            })
    }

    private fun categoryResponse(categoryResponse: CategoryResponse) {
        categoryListAdapter.setData(categoryResponse)
    }
}