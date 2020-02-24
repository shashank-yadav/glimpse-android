package com.example.homemaker.factoryMethods

import android.os.Bundle
import com.example.homemaker.Objects.Store
import com.example.homemaker.ProductFragments.HmProductCategoryFirebaseFragment

class HmProductCategoryFirebaseFragmentFactory {
    companion object{
         fun newInstance(store: Store, productCategory: String): HmProductCategoryFirebaseFragment{
            val args = Bundle()
            args.putSerializable("store", store)
            args.putSerializable("productCategory", productCategory)
            val fragment = HmProductCategoryFirebaseFragment()
            fragment.arguments = args
            return fragment
        }
    }

}