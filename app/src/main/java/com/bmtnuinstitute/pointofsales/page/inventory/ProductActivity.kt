package com.bmtnuinstitute.pointofsales.page.inventory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
//import com.inyongtisto.myhelper.extension.showSuccessDialog
import com.bmtnuinstitute.pointofsales.R
import com.bmtnuinstitute.pointofsales.page.cart.CartActivity
import com.bmtnuinstitute.pointofsales.page.home.CategoryAdapter
import com.bmtnuinstitute.pointofsales.page.home.ProductAdapter
import com.bmtnuinstitute.pointofsales.page.login.LoginActivity
import com.bmtnuinstitute.pointofsales.page.print.PrintHistoryActivity
import com.bmtnuinstitute.pointofsales.preferences.PrefManager
import com.bmtnuinstitute.pointofsales.retrofit.response.SubmitResponse
import com.bmtnuinstitute.pointofsales.retrofit.response.kategori.Category
import com.bmtnuinstitute.pointofsales.retrofit.response.kategori.CategoryResponse
import com.bmtnuinstitute.pointofsales.retrofit.response.produk.Product
import com.bmtnuinstitute.pointofsales.retrofit.response.produk.ProductResponse
import com.bmtnuinstitute.pointofsales.retrofit.service.ApiService
import com.bmtnuinstitute.pointofsales.retrofit.service.URL_IMAGE
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.toolbar
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.content_home.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductActivity : AppCompatActivity() {

    private val api by lazy { ApiService.cashier }
    private val pref by lazy { PrefManager(this) }

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapter: ProductAdapter
    private var categoryId: Int? = null

    private lateinit var menuItemCount: MenuItem
    private var itemCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = pref.getString("pref_user_namatoko").toString()

        setupListener()
        setupRecyclerView()

        addProduct.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }
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
                        dialogInventory(product)
                    }
                })
        list_product.apply {
            layoutManager = GridLayoutManager(applicationContext, 3)
            adapter = productAdapter
        }
    }

    private fun dialogInventory(product: Product){
        val cartView = layoutInflater.inflate(R.layout.dialog_inventory, null)
        val dialog = BottomSheetDialog(this)
        dialog.apply {
            setContentView(cartView)
            setTitle("")
            setCancelable(false)
        }

        cartView.findViewById<TextView>(R.id.label_cart).text = product.nama_produk
        cartView.findViewById<ImageView>(R.id.icon_close).setOnClickListener {
            dialog.dismiss()
        }
        cartView.findViewById<TextView>(R.id.btn_edit).setOnClickListener {
            Toast.makeText(applicationContext, "Id :"+ product.id_produk, Toast.LENGTH_SHORT).show()
            val intent = Intent(this@ProductActivity, EditProductActivity::class.java)
            startActivity(
                intent
                    .putExtra("NAME_PRODUCT", product.nama_produk)
                    .putExtra("SKU", product.barcode)
                    .putExtra("CAT_ID", product.id_kategori)
                    .putExtra("ID_PRODUCT", product.id_produk)
                    .putExtra("PRICE", product.harga)
                    .putExtra("IMAGE", product.image)
            )
        }
        cartView.findViewById<TextView>(R.id.btn_delete).setOnClickListener {
            Toast.makeText(applicationContext, "Id :"+ product.id_produk, Toast.LENGTH_SHORT).show()
            api.deleteProduk(product.id_produk).enqueue(object : Callback<SubmitResponse>{
                override fun onResponse(
                    call: Call<SubmitResponse>,
                    response: Response<SubmitResponse>
                ) {
//                    showSuccessDialog("Sukses", "Product berhasil di hapus")
                }

                override fun onFailure(call: Call<SubmitResponse>, t: Throwable) {

                }

            })
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