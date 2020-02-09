package com.example.homemaker.Objects

data class Store(
        var id: String? = "",
        var name:String? = "",
        var desc:String? = "",
        var products: List<String> = listOf<String>(),
        var location: List<String> = listOf<String>()
)