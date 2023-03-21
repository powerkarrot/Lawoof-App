package com.example.karrot.lawoof.ui.googlesignin

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.karrot.lawoof.R
import com.example.karrot.lawoof.Util.HTTPS.LawoofApiRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class GoogleSignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN: Int = 100
    private lateinit var googleSignInButton: Button
    lateinit var parentActivity: Activity

    companion object {
        fun newInstance() = GoogleSignInFragment()
    }

    private lateinit var viewModel: GoogleSignInViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.google_sign_in_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GoogleSignInViewModel::class.java)
        // TODO: Use the ViewModel

        viewModel.view = view!!
        viewModel.txtEmail = viewModel.view.findViewById(R.id.txt_sign_in_email)
        viewModel.txtPwd = viewModel.view.findViewById(R.id.txt_sign_in_pwd)
        viewModel.btnLogin = viewModel.view.findViewById(R.id.btn_sign_in_login)
        if(!viewModel.activityInitialized())
            viewModel.parentActivity = parentActivity

        viewModel.btnLogin .setOnClickListener {
            val password = viewModel.txtPwd.text.toString()
            val email = viewModel.txtEmail.text.toString()
            login(email, password)
        }

        val txtSignUp = viewModel.view.findViewById<TextView>(R.id.txt_sign_in_sign_up_now)
        txtSignUp.setOnClickListener {
            register(null)
        }

        // Sign in with Google

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(viewModel.parentActivity, gso)

        auth = FirebaseAuth.getInstance()

        googleSignInButton = viewModel.view.findViewById<Button>(R.id.btn_sign_in_google)
        googleSignInButton.setOnClickListener {
            if(googleSignInButton.text == "Sign out")
            {
                googleSignOut()
            } else {
                googleSignIn()
            }
        }
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun googleSignOut(){
        auth.signOut()

        googleSignInClient.signOut().addOnCompleteListener {
            UpdateUI(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        UpdateUI(currentUser)
    }

    private fun login(email: String, password: String) {
        //Todo: Check for valid login. If not valid give user information via material design toast.

        val apiRequest = LawoofApiRequest()
        val onSuccessListener = OnLoginSuccessfull(this)
        apiRequest.login(email, password, onSuccessListener)

        //printErrorSnackbar("TestError", Snackbar.LENGTH_LONG)

    }

    private fun register(bundle: Bundle?){
        val fragment = RegisterFragment.newInstance()
        val fm = fragmentManager
        val transaction = fm!!.beginTransaction()
        if(bundle != null)
        {
            fragment.arguments = bundle
        }
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
                //Todo: Check if user exist in our db. If yes check for sign in credentials otherwise register new user
            } catch (e : ApiException){
                /// Todo: Google Sign In failed update UI!

            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        println("Firebase auth with Google: " + acct.id)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(viewModel.parentActivity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        println("signInWithCredential:success")
                        val user = auth.currentUser
                        val username = acct.displayName.toString()
                        val name = acct.givenName.toString() + " " + acct.familyName.toString()
                        val email = acct.email.toString()
                        val authCode = acct.serverAuthCode.toString()
                        val phone = user!!.phoneNumber.toString()

                        println("GoogleSignIn: $username $name $email $phone with authCode: $authCode")
                        UpdateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        //println("signInWithCredential:failure")
                        //Snackbar.make(findViewById(R.id.cl_testActivity), "Authentication Failed.", Snackbar.LENGTH_LONG).show()
                        UpdateUI(null)
                    }

                    // ...

                }
    }

    private fun UpdateUI(user: FirebaseUser?) {
        //hideProgressDialog()
        if (user != null) {
            val text = getString(R.string.google_status_fmt, user.email) + ", " + getString(R.string.firebase_status_fmt, user.uid)
            printSnackbar(text, Snackbar.LENGTH_LONG)

            viewModel.txtEmail.text = user.email
            googleSignInButton.text = "Sign out"

            // Todo: Check if user is in our db if not register him


            //googleSignInButton.visibility = View.GONE
            //signOutAndDisconnect.visibility = View.VISIBLE
        } else {
            printSnackbar(getString(R.string.signed_out), Snackbar.LENGTH_LONG)

            googleSignInButton.text = "Google"
            //googleSignInButton.visibility = View.VISIBLE
            //signOutAndDisconnect.visibility = View.GONE
        }
    }

    private fun printSnackbar(text: String, duration: Int){
        val snackbar = Snackbar.make(viewModel.view, text, duration)
        snackbar.setActionTextColor(resources.getColor(R.color.GlassBlackBG, resources.newTheme()))
        snackbar.view.setBackgroundColor(resources.getColor(R.color.button_color_material_design_alpha_90, resources.newTheme()))
        snackbar.show()
    }

    private fun printErrorSnackbar(text: String, duration: Int){
        val snackbar = Snackbar.make(viewModel.view, text, duration)
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.view.setBackgroundColor(resources.getColor(R.color.button_color_material_design_alt_alpha_90, resources.newTheme()))
        snackbar.show()
    }


    class OnLoginSuccessfull(val signInFragment: GoogleSignInFragment) : JsonHttpResponseHandler() {
        override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
            super.onSuccess(statusCode, headers, response)
            val status = response!!["status"]
            if(status == "Error")
                signInFragment.printErrorSnackbar(response.toString(), Snackbar.LENGTH_LONG)
            else
                signInFragment.printSnackbar(response.toString(), Snackbar.LENGTH_LONG)
        }
    }
}
