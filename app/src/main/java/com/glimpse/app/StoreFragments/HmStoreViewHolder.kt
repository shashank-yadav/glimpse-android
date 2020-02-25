package com.glimpse.app.StoreFragments

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.glimpse.app.Objects.GlideApp
import com.glimpse.app.Objects.Store
import com.glimpse.app.R
import com.google.android.material.card.MaterialCardView
import com.google.firebase.storage.FirebaseStorage

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