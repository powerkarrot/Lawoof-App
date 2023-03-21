package com.example.karrot.lawoof.ui.googlesignin

import android.app.Activity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModel

class GoogleSignInViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    lateinit var txtEmail : TextView
    lateinit var txtPwd : TextView
    lateinit var btnLogin : Button
    lateinit var view : View
    lateinit var parentActivity : Activity

    fun activityInitialized() : Boolean{
        return ::parentActivity.isInitialized
    }
}
