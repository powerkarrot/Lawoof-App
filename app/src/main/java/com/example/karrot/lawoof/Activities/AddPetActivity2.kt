package com.example.karrot.lawoof.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.karrot.lawoof.Content.User
import com.example.karrot.lawoof.R
import com.example.karrot.lawoof.Util.HTTPS.LawoofApiRequest
import com.example.karrot.lawoof.Util.Help.Util
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class AddPetActivity2 : AppCompatActivity() {

    internal var request = LawoofApiRequest()
//    val petList : MutableList<Pet> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pet)

        if (supportActionBar != null) supportActionBar!!.title = "LaWoof - Add Pet"

        val pet_name = findViewById<EditText>(R.id.pet_name)
        val pet_age = findViewById<EditText>(R.id.pet_age)
        val pet_class = findViewById<EditText>(R.id.pet_class)
        val pet_species = findViewById<EditText>(R.id.pet_species)
        val pet_breed = findViewById<EditText>(R.id.pet_breed)
        val pet_color = findViewById<EditText>(R.id.pet_color)
        val pet_sex = findViewById<EditText>(R.id.pet_sex)
        val pet_castrated = findViewById<EditText>(R.id.pet_castrated)
        val pet_friendliness = findViewById<EditText>(R.id.pet_friendliness)
        val pet_description = findViewById<EditText>(R.id.pet_description)
        val pet_add = findViewById<Button>(R.id.pet_add)

        //TODO: Save local copy first. Else app looses too much functionality if offline
        pet_add.setOnClickListener {
            if (pet_name.text.toString() != "") {
                pet_add.isEnabled = false
                println("USERID " + User.get_id())

                //TODO fix castration and friendliness
                request.addPet("test123", User.get_id(), pet_name.text.toString(), pet_age.text.toString(),
                        pet_class.text.toString(), pet_species.text.toString(), pet_breed.text.toString(), pet_color.text.toString(),
                        pet_sex.text.toString(), true, true, pet_description.text.toString(),
                        object : JsonHttpResponseHandler() {
                            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                                Toast.makeText(applicationContext, "Pet probably added lel", Toast.LENGTH_LONG)
                                println("added pet:" + response!!.toString())
                                finish()
                                updateUI()
                            }
                        })
            }
        }
    }

    fun updateUI() {

        Util.updatePets()
        finish()
        startIntent()
    }

    fun startIntent() {
        val i = Intent(this, PetListActivity::class.java)
        startActivity(i)
    }
}
