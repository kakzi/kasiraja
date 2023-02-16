package com.bmtnuinstitute.pointofsales.page.report

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bmtnuinstitute.pointofsales.R
import com.bmtnuinstitute.pointofsales.page.chart.ChartActivity
import com.bmtnuinstitute.pointofsales.page.login.LoginActivity
import com.bmtnuinstitute.pointofsales.preferences.PrefManager
import com.bmtnuinstitute.pointofsales.retrofit.response.SubmitResponse
import com.bmtnuinstitute.pointofsales.retrofit.response.export.ExportResponse
import com.bmtnuinstitute.pointofsales.retrofit.response.kasir.Cashier
import com.bmtnuinstitute.pointofsales.retrofit.response.kasir.CashierResponse
import com.bmtnuinstitute.pointofsales.retrofit.response.transaksi.Transaction
import com.bmtnuinstitute.pointofsales.retrofit.response.transaksi.TransactionResponse
import com.bmtnuinstitute.pointofsales.retrofit.service.ApiService
import com.bmtnuinstitute.pointofsales.util.CalenderUtil
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.content_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val TAG = "ReportActivity"
const val transactionByDate = 0
const val transactionByCashier = 1

class ReportActivity : AppCompatActivity() {

    private val pref by lazy { PrefManager(this) }
    private val api by lazy { ApiService.owner }

    private var dateStart: String = ""
    private var dateEnd: String = ""
    private var usernameCashier: String = ""
    private var currentTransactionBy: Int = 0

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var cashierAdapter: CashierAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        setupView()
        setupListener()
        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        loadingTransaction(false)
        listCashier()
    }

    private fun setupView(){
        supportActionBar!!.title = "Laporan"
        viewTransactionBy(transactionByDate)
    }

    private fun viewTransactionBy(transactionBy: Int){
        currentTransactionBy = transactionBy
        when (transactionBy) {
            transactionByDate -> {
                radio_date.isChecked = true
                edit_date_start.visibility = View.VISIBLE
                edit_date_end.visibility = View.VISIBLE
                list_cashier.visibility = View.GONE
            }
//            transactionByCashier -> {
//                radio_cashier.isChecked = true
//                edit_date_start.visibility = View.GONE
//                edit_date_end.visibility = View.GONE
//                list_cashier.visibility = View.VISIBLE
//            }
        }
    }

    private fun setupListener(){
        radio_filter_by.setOnCheckedChangeListener { group, checkedId ->
            val filter: RadioButton = group.findViewById(checkedId)
            when (filter.id) {
                R.id.radio_date -> {
                    viewTransactionBy(transactionByDate)
                }
//                R.id.radio_cashier -> {
//                    viewTransactionBy(transactionByCashier)
//                }
            }
        }
        edit_no_transaction.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                listTransaction( currentTransactionBy )
                true
            }
            false
        }
        edit_date_start.setOnClickListener {
            val calender = CalenderUtil
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener {
                    view, year, monthOfYear, dayOfMonth ->
                dateStart = "$year-${(monthOfYear+1)}-$dayOfMonth"
                edit_date_start.setText( dateStart )
                listTransaction(transactionByDate)
            }, calender.year, calender.month, calender.day)
            datePickerDialog.show()
        }
        edit_date_end.setOnClickListener {
            val calender = CalenderUtil
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener {
                    view, year, monthOfYear, dayOfMonth ->
                dateEnd = "$year-${(monthOfYear+1)}-$dayOfMonth"
                edit_date_end.setText( dateEnd )
                listTransaction(transactionByDate)
            }, calender.year, calender.month, calender.day)
            datePickerDialog.show()
        }
    }

    private fun setupRecyclerView(){
        cashierAdapter =
            CashierAdapter(
                arrayListOf(),
                object :
                    CashierAdapter.OnAdapterListener {
                    override fun onClick(cashier: Cashier) {
                        usernameCashier = cashier.username
                        listTransaction(transactionByCashier)
                    }
                })
        list_cashier.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = cashierAdapter
        }
        transactionAdapter =
            TransactionAdapter(
                arrayListOf(),
                object :
                    TransactionAdapter.OnAdapterListener {
                    override fun onClick(transaction: Transaction) {
                        val bundle = Bundle()
                        bundle.putSerializable("arg_transaction", transaction)
                        val transactionDetailFragment = TransactionDetailFragment()
                        transactionDetailFragment.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container_report, transactionDetailFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                })
        list_transaction.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = transactionAdapter
        }
    }

    private fun listCashier(){
        api.kasir().enqueue(object : Callback<CashierResponse> {
            override fun onFailure(call: Call<CashierResponse>, t: Throwable) {

            }
            override fun onResponse(
                call: Call<CashierResponse>,
                response: Response<CashierResponse>
            ) {
                if (response.isSuccessful) {
                    cashierResponse( response.body()!! )
                }
            }
        })
    }

    private fun cashierResponse(cashierResponse: CashierResponse) {
        cashierAdapter.setData( cashierResponse.data )
    }

    private fun listTransaction(transactionBy: Int){
        var call : Call<TransactionResponse>? = null
        when ( transactionBy ) {
            transactionByDate -> {
                if (dateStart.isNotEmpty() && dateEnd.isNotEmpty()) {
                    call = api.transaksiDate( dateStart, dateEnd, edit_no_transaction.text.toString(), pref.getString("pref_user_username")!! )
                } else {
                    Toast.makeText(applicationContext, "Lengkapi tanggal pencarian",
                        Toast.LENGTH_SHORT).show()
                }
            }
//            transactionByCashier -> {
//                call = api.transaksiKasir( usernameCashier, edit_no_transaction.text.toString() )
//            }
        }
        call?.let {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_report, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_chart -> {
                startActivity(Intent(this, ChartActivity::class.java))
                true
            }
            R.id.action_export_excel -> {
                viewTransactionBy(transactionByDate)
                if (dateStart.isNotEmpty() && dateEnd.isNotEmpty()) {
                    export("excel")
                } else {
                    Toast.makeText(applicationContext, "Lengkapi tanggal",
                        Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.action_export_pdf -> {
                viewTransactionBy(transactionByDate)
                if (dateStart.isNotEmpty() && dateEnd.isNotEmpty()) {
                    export("pdf")
                } else {
                    Toast.makeText(applicationContext, "Lengkapi tanggal pencarian",
                        Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.action_logout -> {
                pref.clear()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun export(exportType: String){
        var call: Call<ExportResponse>? = null
        when (exportType) {
            "excel" -> call = api.exportExcel(dateStart, dateEnd)
            "pdf" -> call = api.exportPdf(dateStart, dateEnd)
        }
        call?.let {
            Toast.makeText(applicationContext, "Mohon tunggu", Toast.LENGTH_SHORT).show()
            call.enqueue(object : Callback<ExportResponse> {
                override fun onResponse(
                    call: Call<ExportResponse>,
                    response: Response<ExportResponse>
                ) {
                    if (response.isSuccessful) {
                        exportResponse( response.body()!! )
                    }
                }
                override fun onFailure(call: Call<ExportResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Export Gagal", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun exportResponse(exportResponse: ExportResponse){
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse( exportResponse.data )
        startActivity(openURL)
    }
}