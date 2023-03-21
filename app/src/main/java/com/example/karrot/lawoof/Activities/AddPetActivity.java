package com.example.karrot.lawoof.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.karrot.lawoof.R;
import com.example.karrot.lawoof.Util.CallableActivity.CallbackUI;
import com.example.karrot.lawoof.Util.HTTPS.LawoofApiRequest;
import com.example.karrot.lawoof.Util.HTTPS.Request;
import com.example.karrot.lawoof.Content.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;

public class AddPetActivity extends AppCompatActivity implements CallbackUI{

    EditText pet_name;
    EditText pet_age;
    EditText pet_class;
    EditText pet_species;
    EditText pet_breed;
    EditText pet_color;
    EditText pet_sex;
    EditText pet_castrated;
    EditText pet_friendliness;
    EditText pet_description;
    Button pet_add;
    Request add_pet;
    Request get_pet;
    LawoofApiRequest request = new LawoofApiRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        if(getSupportActionBar() != null) getSupportActionBar().setTitle("LaWoof - Add Pet");

        pet_name = findViewById(R.id.pet_name);
        pet_age = findViewById(R.id.pet_age);
        pet_class = findViewById(R.id.pet_class);
        pet_species =  findViewById(R.id.pet_species);
        pet_breed = findViewById(R.id.pet_breed);
        pet_color = findViewById(R.id.pet_color);
        pet_sex = findViewById(R.id.pet_sex);
        pet_castrated = findViewById(R.id.pet_castrated);
        pet_friendliness = findViewById(R.id.pet_friendliness);
        pet_description = findViewById(R.id.pet_description);
        pet_add = findViewById(R.id.pet_add);

        final CallbackUI context = this;

        //TODO: Save local copy first. Else app looses too much functionality if offline
        pet_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pet_name.getText().toString().equals("")) {
                    pet_add.setEnabled(false);
                    System.out.println("USERID " + User.get_id());
//                    add_pet = Request.addPet(User.get_id(), pet_name.getText().toString(), pet_age.getText().toString(), pet_class.getText().toString(), pet_species.getText().toString(),
//                            pet_breed.getText().toString(), pet_color.getText().toString(), pet_sex.getText().toString(),
//                            pet_castrated.getText().toString(), pet_castrated.getText().toString(), pet_description.getText().toString(), context);

                    //TODO fix castration and friendliness
                    request.addPet("test123", User.get_id(),  pet_name.getText().toString(), pet_age.getText().toString(),
                            pet_class.getText().toString(),pet_species.getText().toString(),  pet_breed.getText().toString(),pet_color.getText().toString(),
                            pet_sex.getText().toString(), true,  true, pet_description.getText().toString(),
                            new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Toast.makeText(getApplicationContext(), "Pet probably added lel", Toast.LENGTH_LONG);
                                    System.out.println(response.toString());
                                    finish();
                                }
                            });
                }
            }
        });
    }

    @Override
    public void updateUI(String... params) {
        pet_add.setEnabled(true);
        if(add_pet.isSuccessful() && get_pet == null) {
            //Todo: Richtig implementieren serverseitig!
            //TODO: offline option plz -.-
             get_pet = Request.getMyPets(User.getEmail(), this);

        }
        if(get_pet.isSuccessful()){
            Intent i = new Intent(this, PetListActivity.class);
            startActivity(i);
        }
    }
}
