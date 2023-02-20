package com.bmtnungaseminstitute.institutekas.page.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bmtnungaseminstitute.institutekas.R
import com.bmtnungaseminstitute.institutekas.model.Menu

class MenuAdapter(private val listMenu: ArrayList<Menu>): RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.image_menu)
        var tvName: TextView = itemView.findViewById(R.id.titleMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val (title, image) = listMenu[position]
        holder.tvName.text = title
        holder.imgPhoto.setImageResource(image)

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listMenu[holder.adapterPosition]) }

    }

    override fun getItemCount(): Int = listMenu.size

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Menu)
    }

}

