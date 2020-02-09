package com.example.homemaker.ProductFragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homemaker.R
import com.example.homemaker.Objects.Employee
import com.squareup.picasso.Picasso


class HmProductViewAdapter internal constructor (private val employees: ArrayList<Employee>, val context: Context, val clickListener: (Employee, Int) -> Unit) :
        RecyclerView.Adapter<HmProductViewAdapter.HmProductViewHolder>() {

    val picasso = Picasso.get()
    var height = context.resources.getDimension(R.dimen.hm_product_card_image_height).toInt()
    var width = context.resources.getDimension(R.dimen.hm_product_card_width).toInt()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HmProductViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.hm_product_card, parent, false)
        return HmProductViewHolder(layoutView)
    }

    override fun getItemCount() = employees.size

    override fun onBindViewHolder(holder: HmProductViewHolder, position: Int) {
        if (position < employees.size) {
            val product = employees[position]
            holder.productTitle.text = product.first_name
            holder.productPrice.text = product.last_name
            picasso.load(product.avatar)
                    .resize( width, height)
                    .into(holder.productImage)
//            ImageRequester.setImageFromUrl(holder.productImage, product.avatar)
            holder.productImage.setOnClickListener { clickListener(product, position) }
        }
    }

    class HmProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var productImage: ImageView = itemView.findViewById(R.id.hm_product_card_image)
        var productTitle: TextView = itemView.findViewById(R.id.hm_product_card_title)
        var productPrice: TextView = itemView.findViewById(R.id.hm_product_card_price)
    }

}

