package com.example.homemaker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.fragment.app.Fragment
import com.example.homemaker.Utils.closeKeyboard
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_login_material.*
import java.security.interfaces.RSAKey


/**
 * Fragment representing the login screen for Shrine.
 */
class LoginFragment : Fragment() {

    /** Activity callback  */
    private var mCallback: ActivityCallback? = null

    val RC_SIGN_IN: Int = 777
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleButton: SignInButton


    /**
     * Create a instance of this fragment
     *
     * @return fragment instance
     */
    fun newInstance(): LoginFragment? {
        return LoginFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login_material, container, false)
        googleButton = view.findViewById<SignInButton>(R.id.googleButton)
        configureGoogleSignIn()
        setupUI()
        firebaseAuth = FirebaseAuth.getInstance()
        return view
    }


    //// On start check if user already logged in, if yes, then take him to the app screen
    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            mCallback!!.openCreateAccount()
        }
    }

    //// Configure the gso information
    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this.context!!, mGoogleSignInOptions)
    }


    //// Setup listener for googleButton
    private fun setupUI() {
        googleButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toast.makeText(this.context, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                mCallback!!.openCreateAccount()
//                startActivity(HomeActivity.getLaunchIntent(this))
            } else {
                Toast.makeText(this.context, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
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
