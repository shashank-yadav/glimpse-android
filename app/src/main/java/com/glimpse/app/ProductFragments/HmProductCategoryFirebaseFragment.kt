package com.glimpse.app.ProductFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glimpse.app.*
import com.glimpse.app.Helpers.AnalyticsHelper
import com.glimpse.app.Objects.Product
import com.glimpse.app.Objects.StateViewModel
import com.glimpse.app.Objects.Store
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.hm_product_fragment_layout.*
import kotlinx.android.synthetic.main.hm_stores_fragment_layout.view.*

class HmProductCategoryFirebaseFragment : Fragment(){

    private lateinit var store: Store //= arguments!!.getSerializable("store") as Store
    private lateinit var productCategory: String //= arguments!!.getSerializable("productCategory") as String

    private lateinit var mAdapter: FirestorePagingAdapter<Product, HmProductViewHolder>
    private lateinit var recyclerView: RecyclerView
    private lateinit var parent: HmFragmentManager
    private lateinit var analyticsHelper: AnalyticsHelper

    private val mFirestore = FirebaseFirestore.getInstance()
//    private val mObjectsCollection = mFirestore.collection("items").document(store.id.toString()).collection(productCategory)
//    private val mQuery = mObjectsCollection.orderBy("price", Query.Direction.ASCENDING)
    private lateinit var mQuery: Query



    private var mCallback: ActivityCallback? = null
    private lateinit var viewModel: StateViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.hm_product_fragment_layout, container, false)
        analyticsHelper = AnalyticsHelper(this.context!!)

        view.swipeRefreshLayout.setOnRefreshListener {
            mAdapter.refresh()
        }
        recyclerView = view.findViewById(R.id.hm_bottomsheet_recyclerview_allitems)
        viewModel = ViewModelProviders.of(this.activity!!).get(StateViewModel::class.java)

        val gridLayoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager

//        parent = this.parentFragment!!.parentFragment as HmFragmentManager
        parent = this.parentFragment!!.parentFragment!!.parentFragment as HmFragmentManager
        setupAdapter()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mCallback = context as ActivityCallback
        store = arguments!!.getSerializable("store") as Store
        productCategory = arguments!!.getSerializable("productCategory") as String
        val mObjectsCollection = mFirestore.collection("items").document(store.id.toString()).collection(productCategory)
        mQuery = mObjectsCollection.orderBy("price", Query.Direction.ASCENDING)
    }

    override fun onDetach() {
        super.onDetach()
        mCallback = null
    }

    private fun setupAdapter(){
        // Init Paging Configuration
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(10)
                .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<Product>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, Product::class.java)
                .build()

        // Instantiate Paging Adapter
        mAdapter = object : FirestorePagingAdapter<Product, HmProductViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HmProductViewHolder {
                val view = layoutInflater.inflate(R.layout.hm_product_card, parent, false)
                return HmProductViewHolder(view)
            }

            override fun onBindViewHolder(viewHolder: HmProductViewHolder, position: Int, product: Product) {
                // Bind to ViewHolder
                viewHolder.bind(product)
                viewHolder.card.setOnClickListener {
                    analyticsHelper.logSelectContent("selectProduct", product.storeId+"_"+product.id, "Card")
                    viewModel.state.productSelected = product
                    parent.changeView()
                }
            }

            override fun onError(e: Exception) {
                super.onError(e)
                Log.e("MainActivity", e.message)
            }

            override fun onLoadingStateChanged(state: LoadingState) {
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
