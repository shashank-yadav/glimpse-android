package com.glimpse.app.Objects

class Product(
    val name: String = "",
    val price: Long = 0,
    val id: String = "",
    val category: String = "",
    val storeId: String = "",
    val desc: String = ""
){

    fun getImgUrl(): String {
        val url = "images/" + storeId + "/" + category + "/" + id +".jpg"
        return url
    }

    fun getModelUrl(): String {
        val url = "models/" + storeId + "/" + category + "/" + id +".glb"
        return url
    }

}