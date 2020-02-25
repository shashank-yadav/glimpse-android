package com.glimpse.app

//import com.google.codelabs.mdc.kotlin.shrine.R

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.glimpse.app.Utils.closeKeyboard
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


/**
 * Fragment representing the login screen for Shrine.
 */
class LoginFragment_backup : Fragment() {

//    lateinit var mEmail: View
//    lateinit var mPassword: View
//    lateinit var signInButton: View

    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mProgressView: View? = null
    private var mLoginFormView: View? = null

    /** Activity callback  */
    private var mCallback: ActivityCallback? = null

    /** Firebase objects  */
    private var mAuth: FirebaseAuth? = null

//    private var RC_SIGN_IN = 777
////    private lateinit var mAuth: FirebaseAuth
////    private lateinit var mGoogleApiClient: GoogleApiClient
//    private lateinit var mGoogleSignInClient: GoogleSignInClient

    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleButton: SignInButton


    /**
     * Create a instance of this fragment
     *
     * @return fragment instance
     */
    fun newInstance(): LoginFragment_backup? {
        return LoginFragment_backup()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login_material, container, false)

        // Choose authentication providers
//        val providers = arrayListOf(
//                AuthUI.IdpConfig.EmailBuilder().build(),
//                AuthUI.IdpConfig.GoogleBuilder().build())
//
//        // Create and launch sign-in intent
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(providers)
//                        .build(),
//                RC_SIGN_IN)



        mEmail = view.findViewById<EditText>(R.id.username_m)
        mPassword = view.findViewById<EditText>(R.id.password_m)
        var signInButton = view.findViewById<View>(R.id.sign_in_button_m)
        googleButton = view.findViewById<SignInButton>(R.id.googleButton)
//

//        googleButton.setOnClickListener {
//            signIn()
//        }

        configureGoogleSignIn()
        setupUI()
        firebaseAuth = FirebaseAuth.getInstance()
//

        signInButton.setOnClickListener{
            context?.let { it1 -> Utils.closeKeyboard(it1, signInButton) }
            attemptLogin()
        }


        val createAccount = view.findViewById(R.id.create_account_button_m) as Button
        createAccount.setOnClickListener {
            closeKeyboard(context!!, createAccount)
            mCallback!!.openCreateAccount()
        }

//        mLoginFormView = view.findViewById(R.id.login_form)
//        mProgressView = view.findViewById(R.id.login_progress)

        mAuth = FirebaseAuth.getInstance()
        closeKeyboard(context!!, mEmail!!)




        // Set an error if the password is less than 8 characters.
//        view.next_button.setOnClickListener {
//            if (!isPasswordValid(password_edit_text.text)) {
//                password_text_input.error = getString(R.string.shr_error_password)
//            } else {
//                password_text_input.error = null // Clear the error
//                (activity as NavigationHost).navigateTo(ProductGridFragment(), false) // Navigate to the next Fragment
//            }
//        }

        // Clear the error once more than 8 characters are typed.
//        view.password_edit_text.setOnKeyListener { _, _, _ ->
//            if (isPasswordValid(password_edit_text.text)) {
//                password_text_input.error = null //Clear the error
//            }
//            false
//        }
        return view
    }


    //// On start check if user already logged in, if yes, then take him to the app screen
    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            mCallback!!.openCreateAccount()
//            startActivity(HomeActivity.getLaunchIntent(this))
//            finish()
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


    /*
        In reality, this will have more complex logic including, but not limited to, actual
        authentication of the username and password.
     */
    private fun isPasswordValid(text: Editable?): Boolean {
        return text != null && text.length >= 8
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mCallback = context as ActivityCallback
    }

    override fun onDetach() {
        super.onDetach()
        mCallback = null
    }

    /// Private methods

    /// Private methods
    private fun attemptLogin() { // Reset errors.
        mEmail?.setError(null)
        mPassword?.setError(null)
        // Store values at the time of the login attempt.
        val username: String = mEmail?.getText().toString()
        val password: String = mPassword?.getText().toString()
        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mEmail?.setError(getString(R.string.error_empty))
            mEmail?.requestFocus()
            return
        }
        if (TextUtils.isEmpty(password)) {
            mPassword?.setError(getString(R.string.error_password))
            mPassword?.requestFocus()
            return
        }
        login()
    }

    private fun login() {
        showProgress(true)
        val email: String = mEmail?.getText().toString()
        val password: String = mPassword?.getText().toString()
        mAuth?.signInWithEmailAndPassword(email, password)?.addOnSuccessListener(OnSuccessListener<AuthResult> { authResult ->
            if (mCallback != null) {
//                saveLocalUser(context!!, Constants.DEFAULT_USER,
//                        mEmail.getText().toString(),
//                        authResult.user!!.uid)
//                mCallback.openChat()
            }
        })?.addOnFailureListener(OnFailureListener { e ->
            showProgress(false)
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
        })
    }

    private fun showProgress(show: Boolean) {
        mProgressView?.setVisibility(if (show) View.VISIBLE else View.GONE)
        mLoginFormView?.setVisibility(if (show) View.GONE else View.VISIBLE)
    }
}
