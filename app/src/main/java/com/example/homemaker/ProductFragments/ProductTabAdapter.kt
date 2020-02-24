package com.example.homemaker.ProductFragments

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.homemaker.Helpers.AnalyticsHelper

class ProductTabAdapter(supportFragmentManager: FragmentManager, context: Context): FragmentStatePagerAdapter(supportFragmentManager) {

    private val mFragmentList = ArrayList<HmProductCategoryFirebaseFragment>()
    private val mFragmentTitles = ArrayList<String>()
    val analyticsHelper = AnalyticsHelper(context)

    override fun getItem(position: Int): Fragment {
        analyticsHelper.logSelectContent("selectedProductCategoryTab", mFragmentTitles[position], "Tab")
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
