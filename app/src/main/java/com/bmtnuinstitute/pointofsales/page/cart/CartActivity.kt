package com.bmtnuinstitute.pointofsales.page.cart

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bmtnuinstitute.pointofsales.R
import com.bmtnuinstitute.pointofsales.page.print.PrintActivity
import com.bmtnuinstitute.pointofsales.preferences.PrefManager
import com.bmtnuinstitute.pointofsales.retrofit.service.ApiService
import com.bmtnuinstitute.pointofsales.retrofit.response.SubmitResponse
import com.bmtnuinstitute.pointofsales.retrofit.response.keranjang.Cart
import com.bmtnuinstitute.pointofsales.retrofit.response.keranjang.CartResponse
import com.bmtnuinstitute.pointofsales.util.Helper
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_cart.list_product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val TAG = "CartActivity"

class CartActivity : AppCompatActivity() {

    private val api by lazy { ApiService.cashier }
    private val pref by lazy { PrefManager(this) }
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        setupView()
        setupRecyclerView()
        setupListener()
    }

    override fun onStart() {
        super.onStart()
        listCart()
    }

    private fun setupView(){
        supportActionBar!!.apply {
            title = "Pesanan"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupRecyclerView(){
        cartAdapter = CartAdapter(this, arrayListOf(), object : CartAdapter.OnAdapterListener{
            override fun onUpdate(cart: Cart) {
                updateCart(cart.id_keranjang, cart.jumlah)
            }

            override fun onDelete(cart: Cart, position: Int) {
                val alertDialog = AlertDialog.Builder(this@CartActivity)
                alertDialog.apply {
                    setTitle("Konfirmasi hapus")
                    setMessage("Yakin hapus ${cart.nama_produk} dari keranjang?")
                    setNegativeButton("Batal") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Hapus") { dialog, _ ->
                        cartAdapter.remove(position)
                        deleteCart(cart.id_keranjang)
                        dialog.dismiss()
                    }
                }
                alertDialog.show()
            }

        })
        list_product.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = cartAdapter
        }
    }

    private fun setupListener(){
        button_checkout.setOnClickListener {
            if ( cartAdapter.carts.size > 0 && edit_customer_name.text.isNotEmpty() &&
                    edit_customer_no.text.isNotEmpty() ) {
                checkout()
            } else {
                Toast.makeText(applicationContext, "Isi data dengan benar",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun listCart(){
        loadingCart(true)
        api.keranjang( pref.getString("pref_user_username")!! )
            .enqueue(object : Callback<CartResponse> {
                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    loadingCart(false)
                }
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>
                ) {
                    loadingCart(false)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            cartResponse( it )
                        }
                    }
                }

            })
    }

    private fun deleteCart(id: String){
        api.hapusKeranjang( id )
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

    private fun updateCart(id: String, amount: Int){
        api.updateKeranjang( id, amount )
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

    private fun loadingCart(loading: Boolean) {
        when(loading) {
            true -> {
                progress_cart.visibility = View.VISIBLE
            }
            false -> {
                progress_cart.visibility = View.GONE
            }
        }
    }

    private fun cartResponse(cartResponse: CartResponse) {
        cartAdapter.setData( cartResponse )
    }

    fun liveTotal(){
        var total = 0
        for (cart in cartAdapter.carts) total += cart.total
        Log.d("TAG", "liveTotal: $total")
        text_total_live.text = "Total: Rp ${Helper.idrFormat(total)}"
    }

    private fun checkout(){
        var totalNominal = 0
        for (cart in cartAdapter.carts) totalNominal += cart.total
        loadingCheckout(true)
        api.checkout(
            pref.getString("pref_user_username")!!,
            edit_customer_name.text.toString(),
            totalNominal.toString(),
            edit_customer_no.text.toString(),
            edit_customer_note.text.toString()
        )
            .enqueue(object : Callback<SubmitResponse> {
                override fun onFailure(call: Call<SubmitResponse>, t: Throwable) {
                    loadingCheckout(false)
                }
                override fun onResponse(
                    call: Call<SubmitResponse>,
                    response: Response<SubmitResponse>
                ) {
                    loadingCheckout(false)
                    if (response.isSuccessful) {
                        checkoutResponse(response.body()!!)
                    }
                }

            })
    }

    private fun loadingCheckout(loading: Boolean){
        when(loading) {
            true -> {
                button_checkout.isEnabled = false
                button_checkout.text = "Menyimpan..."
            }
            false -> {
                button_checkout.isEnabled = true
                button_checkout.text = "Bayar"
            }
        }
    }

    private fun checkoutResponse(submitResponse: SubmitResponse) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("${submitResponse.message}!")
            setMessage("Cetak bukti pembayaran?")
            setNegativeButton("Tutup", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                finish()
            })
            setPositiveButton("Cetak", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                val intent = Intent(this@CartActivity, PrintActivity::class.java)
                startActivity(
                    intent
                        .putExtra("intent_table", edit_customer_no.text.toString())
                        .putExtra("intent_name", edit_customer_name.text.toString())
                        .putExtra("intent_total", text_total_live.text.toString())
                        .putExtra("intent_cart", cartAdapter.carts)
                )
                finish()
            })
        }
        alertDialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
