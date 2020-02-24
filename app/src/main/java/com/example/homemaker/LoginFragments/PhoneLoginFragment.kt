package com.example.homemaker.LoginFragments
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.homemaker.ActivityCallback
import com.example.homemaker.R
import com.google.android.gms.measurement.module.Analytics
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.hm_phone_login.view.*

class PhoneLoginFragment : Fragment(){

    private var mCallback: ActivityCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.hm_phone_login, container, false)

        val editTextPhone = view.login_phone_number;
        view.login_phone_button.setOnClickListener {
            val mobileNum = editTextPhone.text.toString().trim()
            if( mobileNum.isEmpty() || mobileNum.length<10){
                editTextPhone.setError("Enter a valid Phone number")
                editTextPhone.requestFocus()
            }else{
//                mCallback.
                mCallback!!.setPhoneNumber(mobileNum)
                mCallback!!.openPhoneVerification()
            }
        }
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
}