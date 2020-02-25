package com.glimpse.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.glimpse.app.ArFragments.HmArFragment
import com.glimpse.app.ProductFragments.MyPagerAdapter
import com.glimpse.app.ViewPagerAnimations.PopTransformation
import kotlinx.android.synthetic.main.hm_mainlayout.view.*

class HmFragmentManager : Fragment(){
    private var mCallback: ActivityCallback? = null



    private lateinit var arFragment: HmArFragment
    private lateinit var childFragment: HmContainerFragment
    private lateinit var fragmentStateHelper: FragmentStateHelper
    private lateinit var vpager:ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,

                              savedInstanceState: Bundle?): View? {



        val view = inflater.inflate(R.layout.hm_mainlayout, container, false)
        arFragment = HmArFragment()
        childFragment = HmContainerFragment()
        fragmentStateHelper = FragmentStateHelper(childFragmentManager)

        // setup viewpager for AR and product screen
        vpager = view.hm_vpager
        val adapter = MyPagerAdapter(childFragmentManager)
        adapter.addFragment(childFragment, "childFragment")
        adapter.addFragment(arFragment, "arFragment")
        vpager.adapter = adapter
        vpager.setPageTransformer(true, PopTransformation())

        // setup fab
//        val floatingActionButton = view.findViewById(R.id.floating_action_button) as FloatingActionButton
//        floatingActionButton.setOnClickListener(
//                fun(view: View?) { // Handle the click.
//                    vpager.currentItem =  (vpager.currentItem + 1)%2
//                    Log.e("Debug", vpager.currentItem.toString())
//                }
//        )

        return view
    }

    fun changeView(){
        vpager.currentItem =  (vpager.currentItem + 1)%2
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mCallback = context as ActivityCallback
    }

    override fun onDetach() {
        super.onDetach()
        mCallback = null
    }

}
