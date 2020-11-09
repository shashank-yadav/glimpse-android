package com.glimpse.app.StoreFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glimpse.app.Helpers.AnalyticsHelper
import com.glimpse.app.Objects.StateViewModel
import com.glimpse.app.Objects.Store
import com.glimpse.app.R
import com.glimpse.app.factoryMethods.HmProductFragmentFactory
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.hm_product_fragment_layout.*
import kotlinx.android.synthetic.main.hm_stores_fragment_layout.view.*

class HmStoresViewFragment: Fragment(){

    private lateinit var mAdapter: FirestorePagingAdapter<Store, HmStoreViewHolder>
    private lateinit var recyclerView: RecyclerView
    private lateinit var analyticsHelper: AnalyticsHelper

    private val mFirestore = FirebaseFirestore.getInstance()
    private val mObjectsCollection = mFirestore.collection("store")
    private val mQuery = mObjectsCollection.orderBy("name", Query.Direction.ASCENDING)

    private lateinit var viewModel: StateViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.hm_stores_fragment_layout, container, false)
        analyticsHelper = AnalyticsHelper(this.context!!)

        view.swipeRefreshLayout.setOnRefreshListener {
            mAdapter.refresh()
        }

        recyclerView = view.findViewById(R.id.hm_stores_recyclerview)
        viewModel = ViewModelProviders.of(this.activity!!).get(StateViewModel::class.java)

        val gridLayoutManager = GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager

//        parent = this.parentFragment!!.parentFragment as HmFragmentManager
        setupAdapter()
        return view

//        return super.onCreateView(inflater, container, savedInstanceState)

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
//                    throw RuntimeException("Test Crash")
                    analyticsHelper.logSelectContent("selectStore", store.id!!, "Card")
                    fragmentManager!!.beginTransaction().replace(R.id.container_fragment_framelayout, HmProductFragmentFactory.newInstance(store)).addToBackStack("").commit()
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