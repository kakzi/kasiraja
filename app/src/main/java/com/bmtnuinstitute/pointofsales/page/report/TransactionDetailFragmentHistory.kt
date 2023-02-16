package com.bmtnuinstitute.pointofsales.page.report

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bmtnuinstitute.pointofsales.R
import com.bmtnuinstitute.pointofsales.page.HistoryActivity
import com.bmtnuinstitute.pointofsales.page.print.PrintActivity
import com.bmtnuinstitute.pointofsales.page.print.PrintHistoryActivity
import com.bmtnuinstitute.pointofsales.retrofit.response.transaksi.Transaction
import com.bmtnuinstitute.pointofsales.retrofit.response.transaksidetail.TransactionDetailResponse
import com.bmtnuinstitute.pointofsales.retrofit.service.ApiService
import com.bmtnuinstitute.pointofsales.util.Helper
import kotlinx.android.synthetic.main.fragment_transaction_detail.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionDetailFragmentHistory : Fragment() {

    private lateinit var v: View
    private val api by lazy { ApiService.owner }
    private val actionBar by lazy { (requireActivity() as HistoryActivity).supportActionBar!! }
    private lateinit var transactionDetailAdapter: TransactionDetailAdapter
    private var transactionId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_transaction_detail, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        transaction()
        detailTransaction()
    }

    private fun setupView(){
        actionBar.title = "Detail Transaksi"
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

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
        v.btnPrintDetail.setOnClickListener {
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
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.title = "Laporan"
    }
}