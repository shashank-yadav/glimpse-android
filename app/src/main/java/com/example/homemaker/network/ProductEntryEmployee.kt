package com.example.homemaker.network

import android.content.res.Resources
import android.net.Uri
import com.example.homemaker.Objects.Employee
import com.example.homemaker.R
//import com.google.codelabs.mdc.kotlin.shrine.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import kotlin.collections.ArrayList

/**
 * A product entry in the list of products.
 */
class ProductEntryEmployee(dynamicUrl: String) {
    val dynamicUrl: Uri = Uri.parse(dynamicUrl)

    companion object {
        /**
         * Loads a raw JSON at R.raw.products and converts it into a list of ProductEntry objects
         */
        fun initProductEntryList(resources: Resources): ArrayList<Employee> {
            val inputStream = resources.openRawResource(R.raw.employees)
            val jsonProductsString = inputStream.bufferedReader().use(BufferedReader::readText)
            val gson = Gson()
            val productListType = object : TypeToken<ArrayList<Employee>>() {}.type
            return gson.fromJson<ArrayList<Employee>>(jsonProductsString, productListType)
        }
    }
}