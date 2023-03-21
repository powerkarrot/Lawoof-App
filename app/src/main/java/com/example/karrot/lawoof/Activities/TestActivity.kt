package com.example.karrot.lawoof.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.karrot.lawoof.AddPetPopup
import com.example.karrot.lawoof.R
import com.example.karrot.lawoof.Util.HTTPS.LawoofApiRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class TestActivity : AppCompatActivity() {

    // [START declare_auth]
        private lateinit var auth: FirebaseAuth
        private lateinit var googleSignInClient: GoogleSignInClient
        private val RC_SIGN_IN: Int = 100
        private lateinit var googleSignInButton : SignInButton
        private lateinit var signOutAndDisconnect : Button
        private lateinit var txtDetail : TextView
        private lateinit var txtStatus : TextView
    // [END declare_auth]



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val loginButton = findViewById<Button>(R.id.btn_login)

        val responseField = findViewById<TextView>(R.id.txtview_response)

        loginButton.setOnClickListener{
            val apiRequest = LawoofApiRequest()

            apiRequest.login("ex3c@ex3c.de", "test123", object: JsonHttpResponseHandler(){
                override fun onSuccess(statusCode : Int, headers : Array<Header>, response : JSONObject){
                    responseField.text = response.toString()
                }
            })

        }

        val getWalksButton = findViewById<Button>(R.id.btn_getWalks)

        getWalksButton.setOnClickListener{
            val apiRequest = LawoofApiRequest()

            apiRequest.getWalks("ex3c@ex3c.de", "test123", object: JsonHttpResponseHandler(){

                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONArray?) {
                    super.onSuccess(statusCode, headers, response)
                    responseField.text = response.toString()
                }

                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
                    super.onSuccess(statusCode, headers, response)
                    responseField.text = response.toString()
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                    super.onFailure(statusCode, headers, throwable, errorResponse)
                    responseField.text = "Error"
                }
            })

        }

        val getPetsButton = findViewById<Button>(R.id.btn_getPetsByEmail)

        getPetsButton.setOnClickListener{
            val apiRequest = LawoofApiRequest()

            apiRequest.getPetsByEmail("ex3c@ex3c.de", "test123", object: JsonHttpResponseHandler(){

                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONArray?) {
                    super.onSuccess(statusCode, headers, response)
                    responseField.text = response.toString()
                }

                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
                    super.onSuccess(statusCode, headers, response)
                    responseField.text = response.toString()
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                    super.onFailure(statusCode, headers, throwable, errorResponse)
                    responseField.text = "Error"
                }
            })

        }

        val addPetButton = findViewById<Button>(R.id.btn_addPet)

        addPetButton.setOnClickListener{

            val intent = Intent(applicationContext, AddPetPopup::class.java)

            startActivity(intent)
        }

        val addUserButton = findViewById<Button>(R.id.btn_addUser)

        addUserButton.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)

            startActivity(intent)
        }

        // Google SignIn Test

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()

        googleSignInButton = findViewById<SignInButton>(R.id.btn_googleSignIn)

        googleSignInButton.setOnClickListener {
            val intent = Intent(applicationContext, com.example.karrot.lawoof.Activities.GoogleSignIn::class.java)
            startActivity(intent)
            //signIn()
        }

        signOutAndDisconnect = findViewById(R.id.btn_signOut)

        txtDetail = findViewById(R.id.txt_detail)
        txtStatus = findViewById(R.id.txt_status)

        signOutAndDisconnect.setOnClickListener {
            signOut()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    private fun signOut(){
        auth.signOut()

        googleSignInClient.signOut().addOnCompleteListener {
            UpdateUI(null)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e : ApiException){
                /// Todo: Google Sign In failed update UI!

            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        println("Firebase auth with Google: " + acct.id)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
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
                        println("signInWithCredential:failure")
                        Snackbar.make(findViewById(R.id.cl_testActivity), "Authentication Failed.", Snackbar.LENGTH_LONG).show()
                        UpdateUI(null)
                    }

                    // ...

                }
    }

        public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        UpdateUI(currentUser)
    }

    private fun UpdateUI(user: FirebaseUser?) {
        //hideProgressDialog()
        if (user != null) {
            txtStatus.text = getString(R.string.google_status_fmt, user.email)
            txtDetail.text = getString(R.string.firebase_status_fmt, user.uid)

            googleSignInButton.visibility = View.GONE
            signOutAndDisconnect.visibility = View.VISIBLE
        } else {
            txtStatus.setText(R.string.signed_out)
            txtDetail.text = null

            googleSignInButton.visibility = View.VISIBLE
            signOutAndDisconnect.visibility = View.GONE
        }
    }

}
