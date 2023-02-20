package com.bmtnungaseminstitute.institutekas.page.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bmtnungaseminstitute.institutekas.R
import com.bmtnungaseminstitute.institutekas.retrofit.response.kategori.Category
import kotlinx.android.synthetic.main.adapter_category.view.*

class CategoryListAdapter (var categories: ArrayList<Category>, var listener: OnAdapterListener):
    RecyclerView.Adapter<CategoryListAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_category,
                parent, false
            )
        )

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.view.title.text = category.nama_kategori
        holder.view.title.setOnClickListener {
            listener.onClick(category)
        }

    }

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view){
    }

    fun setData(data: List<Category>) {
        categories.clear()
        categories.addAll(data)
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun onClick(category: Category)
    }

}