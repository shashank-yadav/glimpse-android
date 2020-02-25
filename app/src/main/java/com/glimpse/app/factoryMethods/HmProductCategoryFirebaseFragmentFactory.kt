package com.glimpse.app.factoryMethods

import android.os.Bundle
import com.glimpse.app.Objects.Store
import com.glimpse.app.ProductFragments.HmProductCategoryFirebaseFragment

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