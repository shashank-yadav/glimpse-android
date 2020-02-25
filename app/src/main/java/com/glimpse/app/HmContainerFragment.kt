package com.glimpse.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.glimpse.app.StoreFragments.HmStoresViewFragment

class HmContainerFragment: Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.hm_container_fragment_layout, container, false)
        childFragmentManager.beginTransaction().replace(R.id.container_fragment_framelayout, HmStoresViewFragment()).commit()
        return view
    }
}