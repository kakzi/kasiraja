package com.bmtnuinstitute.pointofsales.page.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.inyongtisto.myhelper.extension.showErrorDialog
import com.bmtnuinstitute.pointofsales.R
import com.bmtnuinstitute.pointofsales.model.Menu
import com.bmtnuinstitute.pointofsales.page.HistoryActivity
import com.bmtnuinstitute.pointofsales.page.categories.CategoriesActivity
import com.bmtnuinstitute.pointofsales.page.home.HomeActivity
import com.bmtnuinstitute.pointofsales.page.inventory.ProductActivity
import com.bmtnuinstitute.pointofsales.page.login.LoginActivity
import com.bmtnuinstitute.pointofsales.page.report.*
import com.bmtnuinstitute.pointofsales.preferences.PrefManager
import com.bmtnuinstitute.pointofsales.retrofit.response.dashboard.OmsetResponse
import com.bmtnuinstitute.pointofsales.retrofit.response.transaksi.Transaction
import com.bmtnuinstitute.pointofsales.retrofit.response.transaksi.TransactionResponse
import com.bmtnuinstitute.pointofsales.retrofit.service.ApiService
import com.bmtnuinstitute.pointofsales.util.Helper
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.activity_report.list_transaction
import kotlinx.android.synthetic.main.activity_report.progress_transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {

    private lateinit var rvMenu: RecyclerView
    private val list = ArrayList<Menu>()

    private val pref by lazy { PrefManager(this) }
    private val api by lazy { ApiService.owner }
    private val apiCashier by lazy { ApiService.cashier }

    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        rvMenu = findViewById(R.id.rvMenu)
        rvMenu.setHasFixedSize(true)

        list.addAll(listMenu)
        showRecyclerList()
        listTransaction()
        setupRecyclerView()
        omsetOneMonth()

        logout.setOnClickListener {
            pref.clear()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btn_laporan.setOnClickListener{
            startActivity(Intent(this, ReportActivity::class.java))
        }

        btn_history.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        tvSemuaHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        titleStore.text = pref.getString("pref_user_namatoko")!!

    }

    private fun omsetOneMonth() {
        apiCashier.getOmset(pref.getString("pref_user_username")!! )
            .enqueue(object : Callback<OmsetResponse> {
                override fun onFailure(call: Call<OmsetResponse>, t: Throwable) {
//                    showErrorDialog("Error :" , t.message.toString())
                }
                override fun onResponse(
                    call: Call<OmsetResponse>,
                    response: Response<OmsetResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            tv_omset.text = "Rp."+Helper.idrFormat( it.omset )
                        }
                    }
                }
            })
    }

    private fun setupRecyclerView(){
        transactionAdapter =
            TransactionAdapter(
                arrayListOf(),
                object :
                    TransactionAdapter.OnAdapterListener {
                    override fun onClick(transaction: Transaction) {
//                        Toast.makeText(this@MenuActivity, "Anda memilih" + transaction.nama_pelanggan, Toast.LENGTH_SHORT).show()
                        val bundle = Bundle()
                        bundle.putSerializable("arg_transaction", transaction)
                        val transactionMenu = TransactionDetailFragmentMenu()
                        transactionMenu.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container_report, transactionMenu)
                            .addToBackStack(null)
                            .commit()
                    }
                })
        list_transaction.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = transactionAdapter
        }
    }


    private val listMenu: ArrayList<Menu>
        get() {
            val dataName = resources.getStringArray(R.array.data_title)
            val dataPhoto = resources.obtainTypedArray(R.array.data_image)
            val listHero = ArrayList<Menu>()
            for (i in dataName.indices) {
                val hero = Menu(dataName[i], dataPhoto.getResourceId(i, -1))
                listHero.add(hero)
            }
            return listHero
        }
    private fun showRecyclerList() {
        rvMenu.layoutManager = GridLayoutManager(this, 4)
        val listHeroAdapter = MenuAdapter(list)
        listHeroAdapter.setOnItemClickCallback(object : MenuAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Menu) {
                showSelectedHero(data)
            }
        })
        rvMenu.adapter = listHeroAdapter
    }

    private fun showSelectedHero(data: Menu) {
//        Toast.makeText(this, "Anda memilih "+ data.title , Toast.LENGTH_SHORT).show()
        when (data.title.toLowerCase()) {
            "penjualan" -> startActivity(Intent(this, HomeActivity::class.java))
            "pengeluaran" -> Toast.makeText(this@MenuActivity, "Maaf, fitur ini belum tersedia!",Toast.LENGTH_SHORT ).show()
            "produk" -> startActivity(Intent(this, ProductActivity::class.java))
            "kategori" -> startActivity(Intent(this, CategoriesActivity::class.java))
        }
    }


    private fun listTransaction(){
        val username = pref.getString("pref_user_username").toString()
        var call : Call<TransactionResponse>? = null
        call = api.transaksiLast(username)
//        when ( transactionBy ) {
//            transactionByCashier -> {
//            }
//        }
        call.let {
            loadingTransaction(true)
            it.enqueue(object : Callback<TransactionResponse> {
                override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                    loadingTransaction(false)
                }

                override fun onResponse(
                    call: Call<TransactionResponse>,
                    response: Response<TransactionResponse>
                ) {
                    loadingTransaction(false)
                    if (response.isSuccessful) {
                        transactionResponse( response.body()!! )
                    }
                }
            })
        }
    }

    private fun loadingTransaction(loading: Boolean) {
        when(loading) {
            true -> {
                progress_transaction.visibility = View.VISIBLE
            }
            false -> {
                progress_transaction.visibility = View.GONE
            }
        }
    }

    private fun transactionResponse(transactionResponse: TransactionResponse) {
        transactionAdapter.setData( transactionResponse.data )
    }
}