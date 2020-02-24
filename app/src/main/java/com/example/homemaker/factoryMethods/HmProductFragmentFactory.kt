package com.example.homemaker.factoryMethods

import android.os.Bundle
import com.example.homemaker.Objects.Store
import com.example.homemaker.ProductFragments.HmProductFragment

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