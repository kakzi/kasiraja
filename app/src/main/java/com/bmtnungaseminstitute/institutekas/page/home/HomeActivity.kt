package com.bmtnungaseminstitute.institutekas.page.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.bmtnungaseminstitute.institutekas.R
import com.bmtnungaseminstitute.institutekas.page.cart.CartActivity
import com.bmtnungaseminstitute.institutekas.page.login.LoginActivity
import com.bmtnungaseminstitute.institutekas.preferences.PrefManager
import com.bmtnungaseminstitute.institutekas.retrofit.service.ApiService
import com.bmtnungaseminstitute.institutekas.retrofit.response.SubmitResponse
import com.bmtnungaseminstitute.institutekas.retrofit.response.kategori.Category
import com.bmtnungaseminstitute.institutekas.retrofit.response.kategori.CategoryResponse
import com.bmtnungaseminstitute.institutekas.retrofit.response.produk.Product
import com.bmtnungaseminstitute.institutekas.retrofit.response.produk.ProductResponse
import com.bmtnungaseminstitute.institutekas.retrofit.service.URL_IMAGE
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val TAG = "HomeActivity"

class HomeActivity : AppCompatActivity() {

    private val api by lazy { ApiService.cashier }
    private val pref by lazy { PrefManager(this) }

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapter: ProductAdapter
    private var categoryId: Int? = null

    private lateinit var menuItemCount: MenuItem
    private var itemCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = pref.getString("pref_user_namatoko").toString()

        setupListener()
        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        listProduct("", null)
        listCategory()

    }

    private fun setupListener (){
        search_product.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                listProduct(search_product.text.toString(), categoryId)
                true
            } else {
                false
            }
        }
        txtsemua.setOnClickListener {
            categoryId = null
            listProduct(search_product.text.toString(), categoryId)
        }
    }

    private fun setupRecyclerView(){
        categoryAdapter =
            CategoryAdapter(
                arrayListOf(),
                object :
                    CategoryAdapter.OnAdapterListener {
                    override fun onClick(category: Category) {
                        categoryId = category.id_kategori.toInt()
                        listProduct(search_product.text.toString(), categoryId)
                    }
                })
        list_category.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
            adapter = categoryAdapter
        }
        productAdapter =
            ProductAdapter(
                arrayListOf(),
                object :
                    ProductAdapter.OnAdapterListener {
                    override fun onClick(product: Product) {
                        cartDialog(product)
                    }
                })
        list_product.apply {
            layoutManager = GridLayoutManager(applicationContext, 3)
            adapter = productAdapter
        }
    }

    private fun cartDialog(product: Product){
        val cartView = layoutInflater.inflate(R.layout.dialog_cart, null)
        val dialog = BottomSheetDialog(this)
        dialog.apply {
            setContentView(cartView)
            setTitle("")
            setCancelable(false)
        }
        Picasso.get()
            .load(URL_IMAGE+ product.image)
            .error(R.drawable.ic_no_image)
            .into( cartView.findViewById<ImageView>(R.id.image_product) )
        cartView.findViewById<TextView>(R.id.text_title).text = product.nama_produk
        cartView.findViewById<ImageView>(R.id.icon_close).setOnClickListener {
            dialog.dismiss()
        }
        val editQty = cartView.findViewById<TextView>(R.id.text_qty)
        cartView.findViewById<Button>(R.id.button_plus).setOnClickListener {
            var qty = editQty.text.toString().toInt()
            qty += 1
            editQty.text = qty.toString()
        }
        cartView.findViewById<Button>(R.id.button_min).setOnClickListener {
            var qty = editQty.text.toString().toInt()
            if (qty > 0) qty -= 1
            editQty.text = qty.toString()
        }
        cartView.findViewById<Button>(R.id.button_add_cart).setOnClickListener {
            itemCount += 1
            menuItemCount.title = itemCount.toString()
            Toast.makeText(this, "${product.nama_produk} ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            addToCart(product, editQty.text.toString().toInt())
        }
        dialog.show()
    }

    private fun addToCart(product: Product, qty: Int) {
        api.keranjang( pref.getString("pref_user_username")!!, product.id_produk.toInt(), qty )
            .enqueue(object : Callback<SubmitResponse> {
                override fun onFailure(call: Call<SubmitResponse>, t: Throwable) {

                }
                override fun onResponse(
                    call: Call<SubmitResponse>,
                    response: Response<SubmitResponse>
                ) {

                }
            })
    }

    private fun listCategory(){

        api.kategori(pref.getString("pref_user_username")!! )
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
                            categoryResponse( it )
                        }
                    }
                }
            })
    }

    private fun categoryResponse(categoryResponse: CategoryResponse) {
        categoryAdapter.setData( categoryResponse )
    }

    private fun listProduct(name: String, category: Int?){
        loadingProduct(true)
        api.listProduk(name, category, pref.getString("pref_user_username")!! )
            .enqueue(object : Callback<ProductResponse> {
                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    loadingProduct(false)
                }
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>
                ) {
                    loadingProduct(false)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            productResponse( it )
                        }
                    }
                }

            })
    }

    private fun loadingProduct(loading: Boolean) {
        when(loading) {
            true -> {
                progress_product.visibility = View.VISIBLE
            }
            false -> {
                progress_product.visibility = View.GONE
            }
        }
    }

    private fun productResponse(productResponse: ProductResponse) {
        productAdapter.setData( productResponse )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menuItemCount = menu.findItem(R.id.action_count)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                pref.clear()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            R.id.action_cart -> {
                startActivity(Intent(this, CartActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
