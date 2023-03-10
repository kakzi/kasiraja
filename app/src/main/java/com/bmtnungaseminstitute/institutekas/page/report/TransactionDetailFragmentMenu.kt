package com.bmtnungaseminstitute.institutekas.page.report

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bmtnungaseminstitute.institutekas.R
import com.bmtnungaseminstitute.institutekas.page.HistoryActivity
import com.bmtnungaseminstitute.institutekas.page.menu.MenuActivity
import com.bmtnungaseminstitute.institutekas.page.print.PrintActivity
import com.bmtnungaseminstitute.institutekas.page.print.PrintHistoryActivity
import com.bmtnungaseminstitute.institutekas.retrofit.response.transaksi.Transaction
import com.bmtnungaseminstitute.institutekas.retrofit.response.transaksidetail.TransactionDetail
import com.bmtnungaseminstitute.institutekas.retrofit.response.transaksidetail.TransactionDetailResponse
import com.bmtnungaseminstitute.institutekas.retrofit.service.ApiService
import com.bmtnungaseminstitute.institutekas.util.Helper
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.fragment_history_details.view.*
import kotlinx.android.synthetic.main.fragment_transaction_detail.view.*
import kotlinx.android.synthetic.main.fragment_transaction_detail.view.list_product
import kotlinx.android.synthetic.main.fragment_transaction_detail.view.text_name
import kotlinx.android.synthetic.main.fragment_transaction_detail.view.text_no
import kotlinx.android.synthetic.main.fragment_transaction_detail.view.text_note
import kotlinx.android.synthetic.main.fragment_transaction_detail.view.text_table
import kotlinx.android.synthetic.main.fragment_transaction_detail.view.totalSale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionDetailFragmentMenu : Fragment() {

    private lateinit var v: View
    private val api by lazy { ApiService.owner }
                                                                                                                                                                                                                                                private val actionBar by lazy { (requireActivity() as MenuActivity).supportActionBar!! }
    private lateinit var transactionDetailAdapter: TransactionDetailAdapter
    private var transactionId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_history_details, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setupView()
        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        transaction()
        detailTransaction()
    }

//    private fun setupView(){
//        actionBar.title = "Detail Transaksi"
//        actionBar.setDisplayHomeAsUpEnabled(true)
//    }

    private fun setupRecyclerView() {
        transactionDetailAdapter = TransactionDetailAdapter(arrayListOf())
        v.list_product.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionDetailAdapter
        }
    }

    private fun transaction(){
        val transaction = requireArguments().getSerializable("arg_transaction") as Transaction
        transactionId = transaction.id_transaksi
        v.text_no.text = transaction.no_transaksi
        v.text_table.text = "No Meja : ${transaction.no_meja}"
        v.text_name.text = "Nama Pembeli : " + transaction.nama_pelanggan
        v.text_note.text = "Catatan : " + transaction.catatan
        v.totalSale.text = "Total : " + Helper.idrFormat(transaction.total.toInt())
        v.btnCetak.setOnClickListener {
            val intent = Intent(context, PrintHistoryActivity::class.java)
            startActivity(
                intent
                    .putExtra("intent_table", transaction.no_meja)
                    .putExtra("intent_name", transaction.nama_pelanggan)
                    .putExtra("intent_total", transaction.total)
                    .putExtra("intent_cart", transactionDetailAdapter.products)
            )
        }
    }

    private fun detailTransaction(){
        transactionId?.let {
            api.transaksiDetail(it)
                .enqueue(object : Callback<TransactionDetailResponse> {
                    override fun onFailure(call: Call<TransactionDetailResponse>, t: Throwable) {

                    }
                    override fun onResponse(
                        call: Call<TransactionDetailResponse>,
                        response: Response<TransactionDetailResponse>
                    ) {
                        if (response.isSuccessful) {
                            transactionDetailResponse( response.body()!! )
                        }
                    }
                })
        }
    }

    private fun transactionDetailResponse(transactionDetailResponse: TransactionDetailResponse) {
        transactionDetailAdapter.setData( transactionDetailResponse.data )
    }

    override fun onDestroy() {
        super.onDestroy()
//        actionBar.setDisplayHomeAsUpEnabled(false)
//        actionBar.title = "Laporan"
    }
}