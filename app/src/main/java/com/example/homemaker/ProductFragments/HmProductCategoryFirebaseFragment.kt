package com.example.homemaker.ProductFragments

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
import com.example.homemaker.*
import com.example.homemaker.Objects.Employee
import com.example.homemaker.Objects.Product
import com.example.homemaker.Objects.StateViewModel
import com.example.homemaker.Objects.Store
import com.example.homemaker.network.ProductEntryEmployee
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.hm_product_fragment_layout2.*
import kotlinx.android.synthetic.main.hm_stores_fragment_layout.view.*

class HmProductCategoryFirebaseFragment(store: Store, productCategory: String) : Fragment(){
    private lateinit var mAdapter: FirestorePagingAdapter<Product, HmProductViewHolder>
    private lateinit var recyclerView: RecyclerView
    private lateinit var parent: HmFragmentManager

    private val mFirestore = FirebaseFirestore.getInstance()
    private val mObjectsCollection = mFirestore.collection("items").document(store.id.toString()).collection(productCategory)
    private val mQuery = mObjectsCollection.orderBy("price", Query.Direction.ASCENDING)



    private var mCallback: ActivityCallback? = null
    private lateinit var viewModel: StateViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.hm_product_fragment_layout2, container, false)
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
