package com.example.homemaker.StoreFragments

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.homemaker.Objects.GlideApp
import com.example.homemaker.Objects.Product
import com.example.homemaker.Objects.Store
import com.example.homemaker.R
import com.google.android.material.card.MaterialCardView
import com.google.firebase.storage.FirebaseStorage
import java.text.NumberFormat
import java.util.*

class HmStoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var card: MaterialCardView = itemView.findViewById(R.id.hm_store_card_layout)
    var name: TextView = itemView.findViewById(R.id.hm_store_card_title)
    var desc: TextView = itemView.findViewById(R.id.hm_store_card_desc)
    val image: ImageView = itemView.findViewById(R.id.hm_store_card_image)


    fun bind(store: Store){
        name.text = store.name
        desc.text = store.locations[0]

        val storageReference = FirebaseStorage.getInstance().reference.child(store.getImgUrl())
        GlideApp.with(image.context /* context */)
                .load(storageReference)
                .into(image)

//        itemView.setOnClickListener{
//            Toast.makeText(itemView.context, "inside viewholder position = " + adapterPosition, Toast.LENGTH_SHORT).show()
//        }
    }

}