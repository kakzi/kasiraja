package com.bmtnuinstitute.pointofsales.page.print

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bmtnuinstitute.pointofsales.R
import com.bmtnuinstitute.pointofsales.preferences.PrefManager
import com.bmtnuinstitute.pointofsales.retrofit.response.keranjang.Cart
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import kotlinx.android.synthetic.main.activity_print.*
import android.R.array
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bmtnuinstitute.pointofsales.model.Menu
import com.bmtnuinstitute.pointofsales.page.cart.CartAdapter
import com.bmtnuinstitute.pointofsales.page.home.ProductAdapter
import com.bmtnuinstitute.pointofsales.retrofit.response.keranjang.CartResponse
import com.bmtnuinstitute.pointofsales.retrofit.response.produk.Product
import com.bmtnuinstitute.pointofsales.retrofit.response.transaksidetail.TransactionDetail
import com.bmtnuinstitute.pointofsales.retrofit.service.ApiService
import com.bmtnuinstitute.pointofsales.util.Helper
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_cart.list_product
import kotlinx.android.synthetic.main.content_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Array.get


private const val TAG = "PrintActivity"

class PrintHistoryActivity : AppCompatActivity() {

    private val noTable by lazy { intent.getStringExtra("intent_table") }
    private val name by lazy { intent.getStringExtra("intent_name") }
    private val total by lazy { intent.getStringExtra("intent_total") }
    private val product: ArrayList<TransactionDetail> by lazy { intent.getSerializableExtra("intent_cart") as ArrayList<TransactionDetail> }
    private val pref by lazy { PrefManager(this) }
    private val api by lazy { ApiService.cashier }
    private lateinit var printerAdapter: PrintHistoryAdapter
//    private val list = ArrayList<Cart>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print)
        supportActionBar!!.hide()

        Log.e(TAG, product.toString())

        Printooth.init(this)

        nama_toko.text = pref.getString("pref_user_namatoko")!!
        address.text = pref.getString("pref_user_address")!!
        phone.text = "Nomor Telp : " + pref.getString("pref_user_phone")!!
        nama_pelanggan.text = name
        nomor.text = noTable
        totalYes.text = "Total : " + total.toString()

        sendNotification()

        button_scan.setOnClickListener {
            startActivityForResult(Intent(this, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
        }

        button_print.setOnClickListener {
            print()
        }

        button_close.setOnClickListener {
            finish()
        }

        setupRecyclerView()

    }

    private fun sendNotification() {
        val apikey = pref.getString("pref_apikey")
        val phone = pref.getString("pref_user_phone")



    }

//    override fun onStart() {
//        super.onStart()
//        listCart()
//    }

    private fun setupRecyclerView(){
        printerAdapter = PrintHistoryAdapter(product)
        rvPrintProduct.apply {
            layoutManager = LinearLayoutManager(this@PrintHistoryActivity)
            adapter = printerAdapter
        }
    }

//    private fun listCart(){
//        loadingCart(true)
//        api.keranjang( pref.getString("pref_user_username")!! )
//            .enqueue(object : Callback<CartResponse> {
//                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
//                    loadingCart(false)
//                }
//                override fun onResponse(
//                    call: Call<CartResponse>,
//                    response: Response<CartResponse>
//                ) {
//                    loadingCart(false)
//                    if (response.isSuccessful) {
//                        response.body()?.let {
//                            cartResponse( it )
//                        }
//                    }
//                }
//
//            })
//    }

//    private fun loadingCart(loading: Boolean) {
//        when(loading) {
//            true -> {
//                progress_print.visibility = View.VISIBLE
//            }
//            false -> {
//                progress_print.visibility = View.GONE
//            }
//        }
//    }

//    private fun cartResponse(cartResponse: CartResponse) {
//        printerAdapter.setData(cartResponse)
//    }

    private fun print(){

        val printables = arrayListOf<Printable>(
            TextPrintable.Builder()
                .setText(pref.getString("pref_user_namatoko").toString() + "\n")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
                .build(),
            TextPrintable.Builder()
                .setText(pref.getString("pref_user_address").toString())
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setNewLinesAfter(1)
                .build(),
            TextPrintable.Builder()
                .setText("No. Transaksi $noTable - $name")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build()
        )
        printables.add(
            TextPrintable.Builder()
                .setText("Detail Pesanan \n")
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .build()
        )
        product.forEach {
            printables.add(
                TextPrintable.Builder()
                    .setText("${it.nama_produk} @${it.harga}  x ${it.jumlah} : ${it.total} \n")
                    .build()
            )
        }

        printables.add(
            TextPrintable.Builder()
                .setText("Total : " + total?.let { Helper.idrFormat(it.toInt()) })
                .setNewLinesAfter(1)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .build()
        )

        printables.add(
            TextPrintable.Builder()
                .setText("Terima kasih sudah belanja\ndi Toko kami!")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build()
        )

        printables.add(
            TextPrintable.Builder()
                .setText("........................................")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build()
        )

        Printooth.printer().print(printables)
    }


}