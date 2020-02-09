package com.example.homemaker.ProductFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.homemaker.ActivityCallback
import com.example.homemaker.Objects.StateViewModel
import com.example.homemaker.R
import com.example.homemaker.Objects.Store
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.hm_bottomsheet_content.view.*

class HmProductFragment : Fragment(){

    private var mCallback: ActivityCallback? = null

    private var db = FirebaseFirestore.getInstance()
    private var stores: MutableList<Store> = mutableListOf()
    private lateinit var bottomTabs: TabLayout
    private lateinit var product_viewpager: ViewPager
    private lateinit var tab_adapter: ProductTabAdapter

    private lateinit var viewModel: StateViewModel
    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val view = inflater.inflate(R.layout.hm_bottomsheet_content, container, false)
        rootView = view
        viewModel = ViewModelProviders.of(this.activity!!).get(StateViewModel::class.java)
        initSelectCitiesDropdown()
        initStores()
        setupTabs()
        return view
    }

    private fun setupTabs(){
        bottomTabs = rootView.hm_bottomsheet_tabs
        product_viewpager = rootView.hm_viewPager
        tab_adapter = ProductTabAdapter(childFragmentManager)
        product_viewpager.adapter = tab_adapter
        bottomTabs.setupWithViewPager(product_viewpager)
    }


    private fun initSelectCitiesDropdown(){
        val cities = viewModel.cities
        val newadapter: ArrayAdapter<String?> = ArrayAdapter(
                this.context!!,
                R.layout.dropdown_menu_popup_item,
                cities)


        val editTextFilledExposedDropdown: AutoCompleteTextView = rootView.hm_bottomsheet_select_city_dropdown
        editTextFilledExposedDropdown.setAdapter(newadapter)

        editTextFilledExposedDropdown.setText(editTextFilledExposedDropdown.getAdapter().getItem(0).toString(), false)

        editTextFilledExposedDropdown.setOnItemClickListener { parent, view, position, id ->
            viewModel.state.citySelected = cities[position]
            Toast.makeText(this.context, "Clicked item :"+ " "+ position+cities[position].toString() ,Toast.LENGTH_SHORT).show()
        }
    }


    private fun initSelectStoreDropdown(){
        var hashMap : HashMap<String?, Store> = HashMap()
        for(store in stores){
            hashMap[store.name] = store
        }

        val newadapter: ArrayAdapter<String?> = ArrayAdapter<String?>(
                context,
                R.layout.dropdown_menu_popup_item,
                ArrayList(hashMap.keys))

        val editTextFilledExposedDropdown: AutoCompleteTextView = rootView.hm_bottomsheet_select_store_dropdown
        editTextFilledExposedDropdown.setAdapter(newadapter)
        editTextFilledExposedDropdown.setOnItemClickListener { parent, view, position, id ->
            viewModel.state.storeSelected = stores[position]

            setupTabs()
//            tab_adapter.clear()
            tab_adapter.notifyDataSetChanged()

            val store = stores[position]
            for( item in store.products){
                tab_adapter.addFragment(HmProductCategoryFirebaseFragment(store, item), item)
                tab_adapter.notifyDataSetChanged()
            }

//            Toast.makeText(this.context, "Clicked item :"+ " "+ position+stores[position].toString() ,Toast.LENGTH_SHORT).show()
        }

        val initialSelectionId = 0
        editTextFilledExposedDropdown.setText( editTextFilledExposedDropdown.getAdapter().getItem(initialSelectionId).toString(), false)
        val store = stores[initialSelectionId]
        for( item in store.products){
            tab_adapter.addFragment(HmProductCategoryFirebaseFragment(store, item), item)
            tab_adapter.notifyDataSetChanged()
        }

    }

    private fun initStores() {
        stores.clear()
        db.collection("store").get()
                .addOnSuccessListener {documents ->
                    for(document in documents){
                        stores.add( document.toObject(Store::class.java) )
                        Log.e("document", document.toString())
                    }
                    Log.e("Read data", stores.toString())
                    viewModel.state.stores = stores
                    initSelectStoreDropdown()

                }.addOnFailureListener { exception ->
                    Log.d("store error", "get failed with ", exception)
                }

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
