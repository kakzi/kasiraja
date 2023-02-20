package com.bmtnungaseminstitute.institutekas.page.print

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bmtnungaseminstitute.institutekas.R
import com.bmtnungaseminstitute.institutekas.preferences.PrefManager
import com.bmtnungaseminstitute.institutekas.retrofit.response.keranjang.Cart
import com.bmtnungaseminstitute.institutekas.retrofit.service.ApiService
import com.bmtnungaseminstitute.institutekas.util.Helper
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import kotlinx.android.synthetic.main.activity_print.*


private const val TAG = "PrintActivity"

class PrintActivity : AppCompatActivity() {

    private val noTable by lazy { intent.getStringExtra("intent_table") }
    private val name by lazy { intent.getStringExtra("intent_name") }
    private val total by lazy { intent.getStringExtra("intent_total") }
    private val carts: ArrayList<Cart> by lazy { intent.getSerializableExtra("intent_cart") as ArrayList<Cart> }
    private val pref by lazy { PrefManager(this) }
    private val api by lazy { ApiService.cashier }
    private lateinit var printerAdapter: PrintAdapter
//    private val list = ArrayList<Cart>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print)
        supportActionBar!!.hide()

        Log.e(TAG, carts.toString())

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

    private fun setupRecyclerView(){
        printerAdapter = PrintAdapter(carts)
        rvPrintProduct.apply {
            layoutManager = LinearLayoutManager(this@PrintActivity)
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
        carts.forEach {
            printables.add(
                TextPrintable.Builder()
                    .setText("${it.nama_produk} @${Helper.idrFormat(it.harga.toInt())}  x ${it.jumlah} : ${Helper.idrFormat(it.total.toInt())} \n")
                    .build()
            )
        }

        printables.add(
            TextPrintable.Builder()
                .setText(total.toString())
                .setNewLinesAfter(1)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .build()
        )

        printables.add(
            TextPrintable.Builder()
                .setText("Terima kasih sudah belanja\ndi Toko kami!\nBMT NU Ngasem Institute\nBantu Bisnis Naik Kelas!")
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