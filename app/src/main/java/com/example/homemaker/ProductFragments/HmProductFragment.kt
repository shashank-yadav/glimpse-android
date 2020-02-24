package com.example.homemaker.ProductFragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.homemaker.ActivityCallback
import com.example.homemaker.Helpers.AnalyticsHelper
import com.example.homemaker.MainActivity
import com.example.homemaker.Objects.StateViewModel
import com.example.homemaker.R
import com.example.homemaker.Objects.Store
import com.example.homemaker.factoryMethods.HmProductCategoryFirebaseFragmentFactory
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.hm_bottomsheet_content.view.hm_bottomsheet_tabs
import kotlinx.android.synthetic.main.hm_bottomsheet_content.view.hm_viewPager
import kotlinx.android.synthetic.main.hm_productview_layout.view.*
import kotlinx.android.synthetic.main.profile_dialog_layout.*

class HmProductFragment : Fragment(){

    private lateinit var store: Store
    private var mCallback: ActivityCallback? = null
    private lateinit var analyticsHelper: AnalyticsHelper
    private lateinit var bottomTabs: TabLayout
    private lateinit var product_viewpager: ViewPager
    private lateinit var tab_adapter: ProductTabAdapter

    private lateinit var viewModel: StateViewModel
    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val view = inflater.inflate(R.layout.hm_productview_layout, container, false)
        analyticsHelper = AnalyticsHelper(this.context!!)
        rootView = view
        viewModel = ViewModelProviders.of(this.activity!!).get(StateViewModel::class.java)
//        initSelectCitiesDropdown()
//        initStores()
        setupTabs()
        rootView.hm_store_back_button.setOnClickListener{
            fragmentManager!!.popBackStackImmediate()
        }

        view.hm_store_card_title.text = store.name
//        view.hm_store_card_desc.text = store.locations[0]

        val user = FirebaseAuth.getInstance().currentUser
        val docRef= FirebaseFirestore.getInstance().collection("users").document(user!!.uid)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("store", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("store", "Current data: ${snapshot.data}")
                val firstName = snapshot.data!!["firstName"]!!.toString()
                view.store_username.text = "$firstName"
            } else {
                Log.d("store", "Current data: null")
            }
        }

        view.store_account_icon.setOnClickListener {
            showProfileDialog()
        }


        return view
    }

    private fun showProfileDialog() {

        analyticsHelper.logSelectContent("selectProfile", "", "Button")
        val dialog = Dialog(this.context!!, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
        dialog.setContentView(R.layout.profile_dialog_layout)

        val nameTextView = dialog.profile_name
        val phoneNumberTextView = dialog.profile_phone

        val user = FirebaseAuth.getInstance().currentUser
        phoneNumberTextView.text = user!!.phoneNumber

        val docRef= FirebaseFirestore.getInstance().collection("users").document(user!!.uid)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("store", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("store", "Current data: ${snapshot.data}")

                val data = snapshot.data!!
                val firstName = data["firstName"]!!.toString()
                val lastName = data["lastName"]!!.toString()
                nameTextView.text = "$firstName $lastName"
            } else {
                Log.d("store", "Current data: null")
            }
        }

        dialog.profile_sign_out_button.setOnClickListener {
            analyticsHelper.logSelectContent("signOut", "", "Button")
            val parentActivity = this.context as MainActivity
            FirebaseAuth.getInstance().signOut()
            parentActivity.openPhoneLogin()
            dialog.dismiss()
        }

        dialog.profile_explore_public_stores_view.setOnClickListener{
            dialog.dismiss()
            rootView.hm_store_back_button.performClick()
//            this.fragmentManager!!.popBackStackImmediate()
        }

        dialog.profile_contact_us_email_view.setOnClickListener{
            val emailIntent = Intent(Intent.ACTION_SEND, Uri.fromParts(
                    "mailto","help@glimpsereality.co", null)
            )
            emailIntent.type = "plain/text"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("help@glimpsereality.co"))
            context!!.startActivity(Intent.createChooser(emailIntent, "Send mail..."))
            true
        }

        dialog.profile_close.setOnClickListener {
            analyticsHelper.logSelectContent("closeProfile", "", "Button")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupTabs(){
        bottomTabs = rootView.hm_bottomsheet_tabs
        product_viewpager = rootView.hm_viewPager
        tab_adapter = ProductTabAdapter(childFragmentManager, this.context!!)
        product_viewpager.adapter = tab_adapter
        bottomTabs.setupWithViewPager(product_viewpager)

//        tab_adapter.notifyDataSetChanged()
        for( item in store.products){
            val productCategoryFirebaseFragment = HmProductCategoryFirebaseFragmentFactory.newInstance(store, item)
//            tab_adapter.addFragment(HmProductCategoryFirebaseFragment(store, item), item)
            tab_adapter.addFragment(productCategoryFirebaseFragment, item)
            tab_adapter.notifyDataSetChanged()
        }

//        for(i in 0..4){
//            val tab = (bottomTabs.getChildAt(0) as ViewGroup).getChildAt(i)
//            val params = tab.layoutParams as ViewGroup.MarginLayoutParams
//            params.setMargins(50,0,50,0)
//            tab.requestLayout()
//        }
    }


//    private fun initSelectCitiesDropdown(){
//        val cities = viewModel.cities
//        val newadapter: ArrayAdapter<String?> = ArrayAdapter(
//                this.context!!,
//                R.layout.dropdown_menu_popup_item,
//                cities)
//
//
//        val editTextFilledExposedDropdown: AutoCompleteTextView = rootView.hm_bottomsheet_select_city_dropdown
//        editTextFilledExposedDropdown.setAdapter(newadapter)
//
//        editTextFilledExposedDropdown.setText(editTextFilledExposedDropdown.getAdapter().getItem(0).toString(), false)
//
//        editTextFilledExposedDropdown.setOnItemClickListener { parent, view, position, id ->
//            viewModel.state.citySelected = cities[position]
//            Toast.makeText(this.context, "Clicked item :"+ " "+ position+cities[position].toString() ,Toast.LENGTH_SHORT).show()
//        }
//    }


//    private fun initSelectStoreDropdown(){
//        var hashMap : HashMap<String?, Store> = HashMap()
//        for(store in stores){
//            hashMap[store.name] = store
//        }
//
//        val newadapter: ArrayAdapter<String?> = ArrayAdapter<String?>(
//                context,
//                R.layout.dropdown_menu_popup_item,
//                ArrayList(hashMap.keys))
//
//        val editTextFilledExposedDropdown: AutoCompleteTextView = rootView.hm_bottomsheet_select_store_dropdown
//        editTextFilledExposedDropdown.setAdapter(newadapter)
//        editTextFilledExposedDropdown.setOnItemClickListener { parent, view, position, id ->
//            viewModel.state.storeSelected = stores[position]
//
//            setupTabs()
////            tab_adapter.clear()
//            tab_adapter.notifyDataSetChanged()
//
//            val store = stores[position]
//            for( item in store.products){
//                tab_adapter.addFragment(HmProductCategoryFirebaseFragment(store, item), item)
//                tab_adapter.notifyDataSetChanged()
//            }
//
////            Toast.makeText(this.context, "Clicked item :"+ " "+ position+stores[position].toString() ,Toast.LENGTH_SHORT).show()
//        }
//
//        val initialSelectionId = 0
//        editTextFilledExposedDropdown.setText( editTextFilledExposedDropdown.getAdapter().getItem(initialSelectionId).toString(), false)
//        val store = stores[initialSelectionId]
//        for( item in store.products){
//            tab_adapter.addFragment(HmProductCategoryFirebaseFragment(store, item), item)
//            tab_adapter.notifyDataSetChanged()
//        }
//
//    }

//    private fun initStores() {
//        stores.clear()
//        db.collection("store").get()
//                .addOnSuccessListener {documents ->
//                    for(document in documents){
//                        stores.add( document.toObject(Store::class.java) )
//                        Log.e("document", document.toString())
//                    }
//                    Log.e("Read data", stores.toString())
//                    viewModel.state.stores = stores
//                    initSelectStoreDropdown()
//
//                }.addOnFailureListener { exception ->
//                    Log.d("store error", "get failed with ", exception)
//                }
//
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mCallback = context as ActivityCallback
        store = arguments!!.getSerializable("store") as Store
    }

    override fun onDetach() {
        super.onDetach()
        mCallback = null
    }

}
