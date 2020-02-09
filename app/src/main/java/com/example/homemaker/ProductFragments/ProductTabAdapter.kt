package com.example.homemaker.ProductFragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

class ProductTabAdapter(supportFragmentManager: FragmentManager): FragmentStatePagerAdapter(supportFragmentManager) {

    private val mFragmentList = ArrayList<HmProductCategoryFirebaseFragment>()
    private val mFragmentTitles = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitles[position]
    }

    fun addFragment(fragment: HmProductCategoryFirebaseFragment, title:String) {
        mFragmentList.add(fragment)
        mFragmentTitles.add(title)
    }

    fun clear(){
        mFragmentList.clear()
        mFragmentTitles.clear()
    }
}
