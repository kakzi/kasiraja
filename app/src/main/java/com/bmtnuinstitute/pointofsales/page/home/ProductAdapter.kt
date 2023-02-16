package com.bmtnuinstitute.pointofsales.page.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bmtnuinstitute.pointofsales.R
import com.bmtnuinstitute.pointofsales.retrofit.response.produk.Product
import com.bmtnuinstitute.pointofsales.retrofit.service.URL_IMAGE
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_product.view.*

class ProductAdapter(var products: ArrayList<Product>, var listener: OnAdapterListener) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_product,
                parent, false
            )
        )

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.view.text_title.text = product.nama_produk
//        holder.view.text_price.text = "Rp ${Helper.idrFormat(inventory.harga.toInt())}"
        Picasso.get()
            .load(URL_IMAGE + product.image)
            .placeholder(R.drawable.ic_no_image)
            .placeholder(R.drawable.ic_no_image)
            .into(holder.view.image_product)
        holder.view.image_product.setOnClickListener {
            listener.onClick(product)
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    fun setData(newTrailer: ArrayList<Product>) {
        products.clear()
        products.addAll(newTrailer)
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun onClick(product: Product)
    }

}