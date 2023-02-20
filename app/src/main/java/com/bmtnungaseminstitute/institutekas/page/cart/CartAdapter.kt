package com.bmtnungaseminstitute.institutekas.page.cart

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bmtnungaseminstitute.institutekas.R
import com.bmtnungaseminstitute.institutekas.retrofit.response.keranjang.Cart
import com.bmtnungaseminstitute.institutekas.retrofit.service.URL_IMAGE
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_cart.view.*

class CartAdapter(
    val context: Context,
    var carts: ArrayList<Cart>,
    val listener: OnAdapterListener
) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_cart,
                parent, false
            )
        )

    override fun getItemCount() = carts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cart = carts[position]

        holder.view.text_title.text = cart.nama_produk
        holder.view.text_price.text = cart.harga.toString()
        Log.d("URL_IMAGE_CART", cart.image.toString())
        Picasso.get()
            .load(URL_IMAGE + cart.image)
            .error(R.drawable.ic_no_image)
            .into(holder.view.image_product)

        holder.view.text_qty.text = cart.jumlah.toString()
        cart.total = cart.harga * cart.jumlah
        (context as CartActivity).liveTotal()
        holder.view.text_plus.setOnClickListener {
            cart.jumlah++
            cart.total = cart.harga * cart.jumlah
            holder.view.text_qty.text = cart.jumlah.toString()
            (context as CartActivity).liveTotal()
            listener.onUpdate(cart)
        }
        holder.view.text_min.setOnClickListener {
            if (cart.jumlah > 1) {
                cart.jumlah--
                cart.total = cart.harga * cart.jumlah
                holder.view.text_qty.text = cart.jumlah.toString()
                (context as CartActivity).liveTotal()
                listener.onUpdate(cart)
            } else {
                listener.onDelete(cart, position)
            }
        }

    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    fun setData(data: ArrayList<Cart>) {
        carts.clear()
        carts.addAll(data)
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun onUpdate(cart: Cart)
        fun onDelete(cart: Cart, position: Int)
    }

    fun remove(position: Int) {
        carts.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, carts.size)
        (context as CartActivity).liveTotal()
    }

}