package com.example.homemaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.homemaker.StoreFragments.HmStoresViewFragment
import kotlinx.android.synthetic.main.hm_container_fragment_layout.view.*

class HmContainerFragment: Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.hm_container_fragment_layout, container, false)
        childFragmentManager.beginTransaction().replace(R.id.container_fragment_framelayout, HmStoresViewFragment()).commit()
        return view
    }
}