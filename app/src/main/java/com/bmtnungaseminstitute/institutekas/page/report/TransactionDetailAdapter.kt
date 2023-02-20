package com.bmtnungaseminstitute.institutekas.page.report

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.client.result.ProductParsedResult
import com.bmtnungaseminstitute.institutekas.R
import com.bmtnungaseminstitute.institutekas.page.inventory.CategoryListAdapter
import com.bmtnungaseminstitute.institutekas.retrofit.response.kategori.Category
import com.bmtnungaseminstitute.institutekas.retrofit.response.transaksidetail.TransactionDetail
import com.bmtnungaseminstitute.institutekas.retrofit.service.URL_IMAGE
import com.bmtnungaseminstitute.institutekas.util.Helper
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_transaction_detail.view.*

class TransactionDetailAdapter (var products: ArrayList<TransactionDetail>):
    RecyclerView.Adapter<TransactionDetailAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_transaction_detail,
                parent, false
            )
        )

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.view.text_title.text = product.nama_produk
        val total = Helper.idrFormat( product.total.toInt() )
        val price = Helper.idrFormat(product.harga.toInt())
        holder.view.text_price.text = "Rp $price x ${product.jumlah} : $total"
        Picasso.get()
            .load(URL_IMAGE + product.image)
            .error(R.drawable.ic_no_image)
            .into(holder.view.image_product)
    }

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

    fun setData(data: List<TransactionDetail>) {
        products.clear()
        products.addAll(data)
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun onClick(product: TransactionDetail)
    }

}