package com.glimpse.app.factoryMethods

import android.os.Bundle
import com.glimpse.app.Objects.Store
import com.glimpse.app.ProductFragments.HmProductFragment

class HmProductFragmentFactory {
    companion object{
        fun newInstance(store: Store): HmProductFragment{
            val args = Bundle()
            args.putSerializable("store", store)
            val fragment = HmProductFragment()
            fragment.arguments = args
            return fragment
        }
    }
}