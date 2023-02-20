package com.bmtnungaseminstitute.institutekas.page.print

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bmtnungaseminstitute.institutekas.R
import com.bmtnungaseminstitute.institutekas.preferences.PrefManager
import com.bmtnungaseminstitute.institutekas.retrofit.response.keranjang.Cart
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
import com.bmtnungaseminstitute.institutekas.model.Menu
import com.bmtnungaseminstitute.institutekas.page.cart.CartAdapter
import com.bmtnungaseminstitute.institutekas.page.home.ProductAdapter
import com.bmtnungaseminstitute.institutekas.retrofit.response.keranjang.CartResponse
import com.bmtnungaseminstitute.institutekas.retrofit.response.produk.Product
import com.bmtnungaseminstitute.institutekas.retrofit.response.transaksidetail.TransactionDetail
import com.bmtnungaseminstitute.institutekas.retrofit.service.ApiService
import com.bmtnungaseminstitute.institutekas.util.Helper
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
        totalYes.text = "Total : Rp." + Helper.idrFormat(total.toInt())

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
        printerAdapter = PrintHistoryAdapter(product)
        rvPrintProduct.apply {
            layoutManager = LinearLayoutManager(this@PrintHistoryActivity)
            adapter = printerAdapter
        }
    }


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
                    .setText("${it.nama_produk} @${Helper.idrFormat(it.harga.toInt())}  x ${it.jumlah} : ${Helper.idrFormat(it.total.toInt())} \n")
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
                .setText("Terima kasih sudah belanja\n" +
                        "di Toko kami!\n" +
                        "BMT NU Ngasem Institute\n" +
                        "Bantu Bisnis Naik Kelas!")
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