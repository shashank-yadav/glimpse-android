package com.glimpse.app.ProductFragments

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.glimpse.app.Objects.GlideApp
import com.glimpse.app.R
import com.glimpse.app.Objects.Product
import com.google.android.material.card.MaterialCardView
import com.google.firebase.storage.FirebaseStorage
import java.text.NumberFormat
import java.util.*


class HmProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//        var image: ImageView = itemView.findViewById(R.id.hm_product_card_image)
    var name: TextView = itemView.findViewById(R.id.hm_product_card_title)
    var desc: TextView = itemView.findViewById(R.id.hm_product_card_desc)
    var price: TextView = itemView.findViewById(R.id.hm_product_card_price)
    val image: ImageView = itemView.findViewById(R.id.hm_product_card_image)
    val card: MaterialCardView = itemView.findViewById(R.id.hm_product_card)

    fun bind(product: Product){
        name.text = product.name
        desc.text = product.desc
        val num = product.price.toInt()
        val formattedNum = NumberFormat.getNumberInstance(Locale.ENGLISH).format(num)
        price.text = itemView.context.resources.getString(R.string.Rs) + " "+ formattedNum


        // Reference to an image file in Cloud Storage
        val storageReference = FirebaseStorage.getInstance().reference.child(product.getImgUrl())
        Log.e("ImageUrl", product.getImgUrl())

//        val storageReference = FirebaseStorage.getInstance().reference.child("images/wurfel_kuche/sofa/00001.jpg")


//        val url = "https://firebasestorage.googleapis.com/v0/b/homemaker-d95e8.appspot.com/o/images%2Fwurfel_kuche%2Fsofa%2F00001.jpg?alt=media&token=448e2d9d-fc7d-4871-bf6e-930330e762e8"
//        Glide.

//        Glide.with(image.context)
//                .load("https://moodle.htwchur.ch/pluginfile.php/124614/mod_page/content/4/example.jpg")
//                .into(image);

        // ImageView in your Activity

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)

        GlideApp.with(image.context /* context */)
                .load(storageReference)
                .into(image)

//        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter()
//        storageReference.downloadUrl.addOnSuccessListener {
//            // Got the download URL for 'users/me/profile.png'
//            Log.e("Url", it.toString())
//            Glide.with(image.context)
//                .load(it.toString()).apply(requestOptions)
//                .into(image);
//        }.addOnFailureListener {
//            // Handle any errors
//            Log.e("Url", it.toString())
//        }
    }
}