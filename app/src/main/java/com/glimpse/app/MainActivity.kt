package com.glimpse.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.glimpse.app.LoginFragments.PhoneLoginFragment
import com.glimpse.app.LoginFragments.PhoneVerificationFragment
import com.glimpse.app.LoginFragments.UsernameFragment
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), ActivityCallback{

    lateinit var userPhoneNumber: String
    lateinit var arUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hm_main_activity)

        val mauth = FirebaseAuth.getInstance();

        if (mauth.currentUser != null) {
            openChat()
        }else{
//            openChat()
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, PhoneLoginFragment())
                    .commit()
        }


//        arFragment = ArFragment()

        /////This
//        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment


//        var loginFragment = LoginFragment()
//        var loginFragment = MainFragment()
//        var prodGrid = ProductGridFragment()
//        var scrollFragment = ProductTypeFragment()


//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.add(R.id.login_container, arFragment)
//        fragmentTransaction.commit()

//        replaceFragment(scrollFragment)
//        replaceFragment1(prodGrid)
//        val fragment = MyArFragment.newInstance()
//        replaceFragment(arFragment)

//        if (savedInstanceState == null) {
//
//            var arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment)
//            Log.d("myTag", arFragment.toString())
//            supportFragmentManager
//                    .beginTransaction()
//                    .add(R.id.My_Container_1_ID, ProductGridFragment())
////                    .add(R.id.My_Container_2_ID, supportFragmentManager.findFragmentById(R.id.sceneform_fragment) )
//                    .commit()
//        }


        //////////// and this
//        arFragment.arSceneView.scene.addOnUpdateListener { frameTime ->
//            arFragment.onUpdate(frameTime)
//            onUpdate()
//        }




//        arFragment.setOnTapArPlaneListener { hitResult:HitResult, plane:Plane, _ ->
//            //create a new anchor
//            val anchor = hitResult.createAnchor()
//            placeObject(arFragment,anchor,Uri.parse("https://poly.googleusercontent.com/downloads/0BnDT3T1wTE/85QOHCZOvov/Mesh_Beagle.gltf"))
//        }

        // Set the onclick lister for our button
        // Change this string to point to the .sfb file of your choice :)

        //////////////// and these
//        floatingActionButton.setOnClickListener { addObject(Uri.parse("https://poly.googleusercontent.com/downloads/0BnDT3T1wTE/85QOHCZOvov/Mesh_Beagle.gltf")) }
//        showFab(false)
    }


    public override fun openChat(){
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        replaceFragmentWithoutStack(HmFragmentManager())
//        replaceFragment(HmStoresViewFragment())
    }

    override fun openUsername(){
        replaceFragment(UsernameFragment())
    }


    public override fun openCreateAccount(){
//        replaceFragment(ProductFragment())
    }

    public override fun logout(){
        replaceFragment(LoginFragment())
    }

    public override fun setPhoneNumber(phoneNumber: String) {
        userPhoneNumber = phoneNumber
    }

    public override fun getPhoneNumber(): String {
        return userPhoneNumber
    }

    public override fun openPhoneLogin() {
        replaceFragment(PhoneLoginFragment())
    }

    public override fun openPhoneVerification() {
        replaceFragment(PhoneVerificationFragment())
    }

    override fun setArFragmentUri(uri:String) {
        arUri = uri
    }

    override fun getArFragmentUri(): String {
        return arUri
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
    }

    private fun replaceFragmentWithoutStack(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
    }


//    private fun replaceFragment(fragment: Fragment){
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.container_product_type, fragment)
//        fragmentTransaction.commit()
//    }
//
//    private fun replaceFragment1(fragment: Fragment){
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.container_product_grid1, fragment)
//        fragmentTransaction.commit()
//    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
//    override fun navigateTo(fragment: Fragment, addToBackstack: Boolean) {
//        val transaction = supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.container, fragment)
//
//        if (addToBackstack) {
//            transaction.addToBackStack(null)
//        }
//
//        transaction.commit()
//    }

}
