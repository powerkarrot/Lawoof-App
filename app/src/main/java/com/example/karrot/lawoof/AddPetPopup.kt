package com.example.karrot.lawoof

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.karrot.lawoof.Util.HTTPS.LawoofApiRequest
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import kotlin.math.roundToInt

class AddPetPopup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_pet_popup)

        val dm = DisplayMetrics()

        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width * 0.8f).roundToInt(), (height * 0.7).roundToInt())

        val layoutParams = window.attributes;

        layoutParams.gravity = Gravity.CENTER
        layoutParams.x = 0
        layoutParams.y = -20

        window.attributes = layoutParams

        val btnAddButton = findViewById<Button>(R.id.btn_addPet_add)
        val btnCancelButton = findViewById<Button>(R.id.btn_addPet_cancel)

        val txtName = findViewById<TextView>(R.id.txt_addPet_name)
        val txtEmail = findViewById<TextView>(R.id.txt_addPet_email)
        val txtBirth = findViewById<TextView>(R.id.txt_addPet_dateOfBirth)
        val txtClass = findViewById<TextView>(R.id.txt_addPet_class)
        val txtSpecies= findViewById<TextView>(R.id.txt_addPet_species)
        val txtBreed = findViewById<TextView>(R.id.txt_addPet_breed)
        val txtColor = findViewById<TextView>(R.id.txt_addPet_color)
        val txtSex = findViewById<TextView>(R.id.txt_addPet_sex)
        val txtCastrated = findViewById<TextView>(R.id.txt_addPet_castrated)
        val txtFriendliness = findViewById<TextView>(R.id.txt_addPet_friendliness)
        val txtDescription = findViewById<TextView>(R.id.txt_addPet_description)

        btnAddButton.setOnClickListener{
            val apiRequest = LawoofApiRequest()

            apiRequest.addPet("test123", txtEmail.text.toString(), txtName.text.toString(), txtBirth.text.toString(),
                    txtClass.text.toString(), txtSpecies.text.toString(), txtBreed.text.toString(), txtColor.text.toString(),
                    txtSex.text.toString(), true, true, txtDescription.text.toString(), object: JsonHttpResponseHandler(){


                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
                    super.onSuccess(statusCode, headers, response)

                    //val bundle = Bundle()
                    //bundle.putString("response", response.toString())
                    //parentActivityIntent.putExtras(bundle)

                    val toast = Toast.makeText(applicationContext, response.toString(), Toast.LENGTH_LONG)
                    finish()
                }
            })

        }

        btnCancelButton.setOnClickListener{
            finish()
        }
    }
}
