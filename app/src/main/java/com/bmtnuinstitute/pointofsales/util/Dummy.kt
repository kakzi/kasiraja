package com.bmtnuinstitute.pointofsales.util

import com.bmtnuinstitute.pointofsales.R
import com.bmtnuinstitute.pointofsales.model.CartModel
import com.bmtnuinstitute.pointofsales.model.CategoryModel
import com.bmtnuinstitute.pointofsales.model.ProductModel

class Dummy {

    companion object {

        fun getProduct(): ArrayList<ProductModel> {

            val list = arrayOf(
                "Rawon",
                "Tempe Penyet",
                "Bebek Goreng",
                "Cumi-Cumi",
                "Soto",
                "Nasi Goreng"
            )

            val products = ArrayList<ProductModel>()
            for (food in list) products.add(
                ProductModel(
                    food
                )
            )

            return products

        }

        fun getCategory(): ArrayList<CategoryModel> {

            val list = arrayOf(
                "Semua",
                "Makanan",
                "Makanan Ringan",
                "Minuman"
            )

            val categories = ArrayList<CategoryModel>()
            for (category in list) categories.add(
                CategoryModel(
                    category
                )
            )

            return categories

        }

        fun getCart(): ArrayList<CartModel> {

            val titles = arrayOf(
                "Nasi Goreng",
                "Soto"
            )

            val pricing = intArrayOf(
                12000 ,
                10000
            )

            val images = intArrayOf(
                R.drawable.nasgor,
                R.drawable.soto
            )

            val products = ArrayList<CartModel>()
            for (i in titles.indices) products.add(
                CartModel(
                    titles[i],
                    pricing[i],
                    images[i],
                    0,
                    0
                )
            )

            return products

        }

    }



}