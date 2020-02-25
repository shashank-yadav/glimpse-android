package com.glimpse.app.ProductFragments

//class HmProductCategoryFragment : Fragment(){
//    lateinit var productTypes:String
//    lateinit var company:String
//    private var mCallback: ActivityCallback? = null
//    private lateinit var viewModel: StateViewModel
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//
//        viewModel = ViewModelProviders.of(this).get(StateViewModel::class.java)
//
//        val view = inflater.inflate(R.layout.hm_product_fragment_layout, container, false)
//        val recycler_view = view.findViewById<RecyclerView>(R.id.hm_bottomsheet_recyclerview_allitems)
//
////        recycler_view.setHasFixedSize(true)
//
//        val gridLayoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
//        recycler_view.layoutManager = gridLayoutManager
//
////        val listener = { employee: Employee, id: Int -> Toast.makeText(this.context,employee.avatar, Toast.LENGTH_SHORT).show()}
//        val parent = this.parentFragment!!.parentFragment as HmFragmentManager
//        val listener = { employee: Employee, id: Int ->
//            mCallback!!.setArFragmentUri(employee.avatar);
//            parent.changeView()
//        }
//
//        val adapter = HmProductViewAdapter(ProductEntryEmployee.initProductEntryList(resources), this.context!!, listener)
//        recycler_view.adapter = adapter
//
//        val largePadding = resources.getDimensionPixelSize(R.dimen.shr_staggered_product_grid_spacing_large)
//        val smallPadding = resources.getDimensionPixelSize(R.dimen.shr_staggered_product_grid_spacing_small)
//        recycler_view.addItemDecoration(ProductGridItemDecoration(largePadding, smallPadding))
//
//        return view
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mCallback = context as ActivityCallback
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        mCallback = null
//    }
//
//}
