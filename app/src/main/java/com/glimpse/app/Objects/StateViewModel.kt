package com.glimpse.app.Objects

import androidx.lifecycle.ViewModel

class StateViewModel : ViewModel(){
    var state: State = State()

    val cities : List<String> = listOf("Bangalore")
    val defaultStore = "wurfel_kuche"
    val defaultCity = "Bangalore"
}