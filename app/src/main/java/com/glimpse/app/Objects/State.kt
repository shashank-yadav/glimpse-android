package com.glimpse.app.Objects

class State (){
    lateinit var stores: MutableList<Store>
    lateinit var storeSelected: Store
    lateinit var citySelected: String
    var productSelected: Product? = null

    companion object{
        val DEFAULT_CITY="Bangalore"
        val DEFAULT_STORE_ID="wurfel_kuche"
    }
}