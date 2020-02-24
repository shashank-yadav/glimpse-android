package com.example.homemaker.Objects

import java.io.Serializable

data class Store(
        var id: String? = "",
        var name:String? = "",
        var desc:String? = "",
        var products: List<String> = listOf<String>(),
        var locations: List<String> = listOf<String>(),
        var cities: List<String> = listOf<String>()
) : Serializable {
    fun getImgUrl(): String {
        val url = "store_logos/" + id + "/" + id +".jpg"
        return url
    }
}