package com.glimpse.app.LoginFragments

import `in`.aabhasjindal.otptextview.OtpTextView
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.glimpse.app.ActivityCallback
import com.glimpse.app.R
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.hm_phone_verification.view.*
import java.util.concurrent.TimeUnit

class PhoneVerificationFragment: Fragment(){

    lateinit var mCallbacks_phone: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var mAuth: FirebaseAuth
    private lateinit var verificationId: String
    private lateinit var phoneNumber: String
    private lateinit var otpView: OtpTextView


    private var mCallback: ActivityCallback? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mAuth = FirebaseAuth.getInstance()
        val view = inflater.inflate(R.layout.hm_phone_verification, container, false)
        val editTextVerificationCode = view.login_phone_otp
        phoneNumber = mCallback!!.getPhoneNumber()

        val otpMessageTextView = view.otp_message_line1
        otpMessageTextView.text = "We've sent an otp to +91-$phoneNumber"

        mCallbacks_phone = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // verification completed
//                val code = credential.smsCode.toString()
//                verifyCode(code)
                signInWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG ).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationId = p0
            }
        }

        sendVerificationCode(phoneNumber)
//        Toast.makeText(context, phoneNumber.toString(), Toast.LENGTH_LONG ).show()

        otpView = view.login_phone_otp

        view.login_phone_otp_button.setOnClickListener {
//            val code = editTextVerificationCode.text.toString().trim()
            val code = otpView.otp
            if(code != null && code.length == 6){
                verifyCode(code)
            }else{
                otpView.showError()
                Toast.makeText(context, "Enter Valid OTP", Toast.LENGTH_LONG).show()
            }
//            if( code.isEmpty() || code.length<6){
//                editTextVerificationCode.error = "Enter a valid otp"
//                editTextVerificationCode.requestFocus()
//            }else{
//                verifyCode(code)
//            }
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
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                otpView.showSuccess()
                Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
//                mCallback!!.openUsername()

                val user = FirebaseAuth.getInstance().currentUser
                FirebaseFirestore.getInstance().collection("users").document(user!!.uid)
                        .get()
                        .addOnSuccessListener {
                            Log.d("userData", "User Data successfully written!")
                            mCallback!!.openChat()
                        }
                        .addOnFailureListener { e ->
                            Log.w("userData", "Error writing document userData", e)
                            mCallback!!.openUsername()
                        }

            }else{
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91$phoneNumber",
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks_phone)
    }
}