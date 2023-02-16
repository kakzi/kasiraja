package com.bmtnuinstitute.pointofsales.page.print

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bmtnuinstitute.pointofsales.R
import com.bmtnuinstitute.pointofsales.model.Menu
import com.bmtnuinstitute.pointofsales.retrofit.response.keranjang.Cart
import com.bmtnuinstitute.pointofsales.retrofit.response.transaksidetail.TransactionDetail
import kotlinx.android.synthetic.main.item_print_product.view.*

class PrintHistoryAdapter (private val listProduct: ArrayList<TransactionDetail>) :
    RecyclerView.Adapter<PrintHistoryAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_print_product,
                parent, false
            )
        )

    override fun getItemCount() = listProduct.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cart = listProduct[position]
        holder.view.name_product.text = cart.nama_produk +" @"+cart.harga.toString() + "x" + cart.jumlah + ":" + cart.total

    }
    class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

    fun setData(data: ArrayList<TransactionDetail>) {
        listProduct.clear()
        listProduct.addAll(data)
        notifyDataSetChanged()
    }


}