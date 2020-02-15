package com.example.homemaker.StoreFragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homemaker.HmFragmentManager
import com.example.homemaker.MainActivity
import com.example.homemaker.Objects.Product
import com.example.homemaker.Objects.StateViewModel
import com.example.homemaker.Objects.Store
import com.example.homemaker.ProductFragments.HmProductFragment
import com.example.homemaker.ProductFragments.HmProductViewHolder
import com.example.homemaker.R
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.hm_product_fragment_layout2.*
import kotlinx.android.synthetic.main.hm_stores_fragment_layout.view.*
import kotlinx.android.synthetic.main.profile_dialog_layout.*

class HmStoresViewFragment: Fragment(){

    private lateinit var mAdapter: FirestorePagingAdapter<Store, HmStoreViewHolder>
    private lateinit var recyclerView: RecyclerView
    private lateinit var parent: HmFragmentManager

    private val mFirestore = FirebaseFirestore.getInstance()
    private val mObjectsCollection = mFirestore.collection("store")
    private val mQuery = mObjectsCollection.orderBy("name", Query.Direction.ASCENDING)

    private lateinit var viewModel: StateViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.hm_stores_fragment_layout, container, false)
        view.swipeRefreshLayout.setOnRefreshListener {
            mAdapter.refresh()
        }
        recyclerView = view.findViewById(R.id.hm_stores_recyclerview)

        viewModel = ViewModelProviders.of(this.activity!!).get(StateViewModel::class.java)

        val gridLayoutManager = GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager


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
            showDialog()
        }

//        parent = this.parentFragment!!.parentFragment as HmFragmentManager
        setupAdapter()
        return view

//        return super.onCreateView(inflater, container, savedInstanceState)

    }

    private fun showDialog() {
        val dialog = Dialog(this.context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
        dialog.setContentView(R.layout.profile_dialog_layout)

        val firstNameTextView = dialog.profile_firstName
        val lastNameTextView = dialog.profile_lastName
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
                firstNameTextView.text = firstName

                val lastName = data["lastName"]!!.toString()
                lastNameTextView.text = lastName
            } else {
                Log.d("store", "Current data: null")
            }
        }

        dialog.profile_logout.setOnClickListener {
//            dialog.dismiss()
            val parentActivity = this.context as MainActivity
            FirebaseAuth.getInstance().signOut()
            parentActivity.openPhoneLogin()
            dialog.dismiss()
        }

        dialog.profile_close.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupAdapter(){
        // Init Paging Configuration
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(10)
                .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<Store>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, Store::class.java)
                .build()

        // Instantiate Paging Adapter
        mAdapter = object : FirestorePagingAdapter<Store, HmStoreViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HmStoreViewHolder {
                val view = layoutInflater.inflate(R.layout.hm_store_card, parent, false)
                return HmStoreViewHolder(view)
            }

            override fun onBindViewHolder(viewHolder: HmStoreViewHolder, position: Int, store: Store) {
                // Bind to ViewHolder
                viewHolder.bind(store)
                viewHolder.card.setOnClickListener {
//                    viewModel.state.productSelected = product
//                    parent.changeView()
                    // go to new fragment here
//                    val parent = parentFragment
//                    parentFragment!!.childFragmentManager.beginTransaction().replace(R.id.container_fragment_framelayout, HmProductFragment(store)).commit()
                    fragmentManager!!.beginTransaction().replace(R.id.container_fragment_framelayout, HmProductFragment(store)).addToBackStack("").commit()
                    viewModel.state.storeSelected = store
//                    Toast.makeText(viewHolder.card
//                            .context, "inside viewholder position = ", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(e: Exception) {
                super.onError(e)
                Log.e("Product Adapter", e.message)
            }

            override fun onLoadingStateChanged(state: LoadingState) {

                Log.e("state", state.toString())
                when (state) {
                    LoadingState.LOADING_INITIAL -> {
                        swipeRefreshLayout.isRefreshing = true
                    }


                    LoadingState.LOADING_MORE -> {
                        swipeRefreshLayout.isRefreshing = true
                    }

                    LoadingState.LOADED -> {
                        swipeRefreshLayout.isRefreshing = false
                    }

                    LoadingState.ERROR -> {
                        Toast.makeText(
                                context,
                                "Error Occurred!",
                                Toast.LENGTH_SHORT
                        ).show()
                        swipeRefreshLayout.isRefreshing = false
                    }

                    LoadingState.FINISHED -> {
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            }

        }

        // Finally Set the Adapter to RecyclerView
        recyclerView.adapter = mAdapter

    }

}