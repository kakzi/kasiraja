package com.bmtnuinstitute.pointofsales.page

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bmtnuinstitute.pointofsales.R
import com.bmtnuinstitute.pointofsales.page.report.TransactionAdapter
import com.bmtnuinstitute.pointofsales.page.report.TransactionDetailFragment
import com.bmtnuinstitute.pointofsales.page.report.TransactionDetailFragmentHistory
import com.bmtnuinstitute.pointofsales.preferences.PrefManager
import com.bmtnuinstitute.pointofsales.retrofit.response.transaksi.Transaction
import com.bmtnuinstitute.pointofsales.retrofit.response.transaksi.TransactionResponse
import com.bmtnuinstitute.pointofsales.retrofit.service.ApiService
import kotlinx.android.synthetic.main.activity_report.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {


    private val pref by lazy { PrefManager(this) }
    private val api by lazy { ApiService.owner }
    private val apiCashier by lazy { ApiService.cashier }

    private lateinit var transactionAdapter: TransactionAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setupRecyclerView()
        listTransaction()
    }

    private fun setupRecyclerView(){
        transactionAdapter =
            TransactionAdapter(
                arrayListOf(),
                object :
                    TransactionAdapter.OnAdapterListener {
                    override fun onClick(transaction: Transaction) {
                        Toast.makeText(this@HistoryActivity, "Anda memilih" + transaction.nama_pelanggan, Toast.LENGTH_SHORT).show()
                        val bundle = Bundle()
                        bundle.putSerializable("arg_transaction", transaction)
                        val historyActivity = TransactionDetailFragmentHistory()
                        historyActivity.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container_report, historyActivity)
                            .addToBackStack(null)
                            .commit()
                    }
                })
        list_transaction.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = transactionAdapter
        }
    }


    private fun listTransaction() {
        val username = pref.getString("pref_user_username").toString()
        var call : Call<TransactionResponse>? = null
        call = api.history(username)
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