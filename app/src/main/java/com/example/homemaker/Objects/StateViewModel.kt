package com.example.homemaker.Objects

import androidx.lifecycle.ViewModel
import java.lang.reflect.Array

class StateViewModel : ViewModel(){
    var state: State = State()

    val cities : List<String> = listOf("Bangalore")
    val defaultStore = "wurfel_kuche"
    val defaultCity = "Bangalore"
}