package com.example.homemaker.LoginFragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.homemaker.ActivityCallback
import com.example.homemaker.R
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.hm_phone_verification.view.*
import java.util.concurrent.TimeUnit

class PhoneVerificationFragment: Fragment(){

    lateinit var mCallbacks_phone: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var mAuth: FirebaseAuth
    private lateinit var vericationId: String
    private lateinit var phoneNumber: String


    private var mCallback: ActivityCallback? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mAuth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.hm_phone_verification, container, false)
        val editTextVerificationCode = view.login_phone_otp;
        phoneNumber = mCallback!!.getPhoneNumber()

        mCallbacks_phone = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // verification completed
                val code = credential.smsCode.toString()
                verifyCode(code)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG ).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                vericationId = p0;
            }
        }

        sendVerifcationCode(phoneNumber)

        view.login_phone_otp_button.setOnClickListener {
            val code = editTextVerificationCode.text.toString().trim()
            if( code.isEmpty() || code.length<6){
                editTextVerificationCode.setError("Enter a valid otp")
                editTextVerificationCode.requestFocus()
            }else{
                verifyCode(code)
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

    fun verifyCode(code: String){
        val credential = PhoneAuthProvider.getCredential(vericationId, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
                mCallback!!.openChat()
            }else{
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun sendVerifcationCode(mobileNumber: String){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91$mobileNumber",
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks_phone);
    }
}