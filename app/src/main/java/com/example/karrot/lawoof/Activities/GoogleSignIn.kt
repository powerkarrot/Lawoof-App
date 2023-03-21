package com.example.karrot.lawoof.Activities

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.karrot.lawoof.R
import com.example.karrot.lawoof.ui.googlesignin.GoogleSignInFragment
import kotlinx.android.synthetic.main.app_bar_main.*


class GoogleSignIn : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if(toolbar != null)
            toolbar.visibility = Toolbar.INVISIBLE
        setContentView(R.layout.google_sign_in_activity)

        val googleSignInFragment = GoogleSignInFragment.newInstance()
        googleSignInFragment.parentActivity = this

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, googleSignInFragment)
                    .commitNow()
        }



    }

    fun Login(){
        val googleSignIn = GoogleSignInFragment.newInstance()
    }

}
