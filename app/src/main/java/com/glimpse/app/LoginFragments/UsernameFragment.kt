package com.glimpse.app.LoginFragments
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.glimpse.app.ActivityCallback
import com.glimpse.app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.hm_username.view.*

class UsernameFragment : Fragment(){

    private var mCallback: ActivityCallback? = null
    private val mFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.hm_username, container, false)
        val firstNameTextView = view.login_firstname
        val lastNameTextView = view.login_lastname

        view.username_continue_button.setOnClickListener {
            val firstName = firstNameTextView.text!!.trim().toString()
            val lastName = lastNameTextView.text!!.trim().toString()

            var bothCorrect = true

            if( firstName.isEmpty() || firstName.length > 15 ){
                firstNameTextView.error = "Enter a valid Phone number"
                firstNameTextView.requestFocus()
                bothCorrect = false
            }

            if( lastName.isEmpty() || lastName.length > 15 ){
                lastNameTextView.error = "Enter a valid Phone number"
                lastNameTextView.requestFocus()
                bothCorrect = false
            }

            if (bothCorrect){

                val user = FirebaseAuth.getInstance().currentUser
//                val profileUpdates = UserProfileChangeRequest.Builder()
//                        .setDisplayName(firstName+" "+)
//                        .build()

                val userData = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName
                )
                mFirestore.collection("users").document(user!!.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Log.d("userData", "User Data successfully written!")
                        }
                        .addOnFailureListener { e -> Log.w("userData", "Error writing document userData", e) }

                mCallback!!.openChat()
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