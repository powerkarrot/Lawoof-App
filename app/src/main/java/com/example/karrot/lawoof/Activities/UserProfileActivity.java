package com.example.karrot.lawoof.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.karrot.lawoof.R;
import com.example.karrot.lawoof.Content.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

/**
 * Created by karrot on 12.11.2016.
 */

public class UserProfileActivity extends AppCompatActivity {

    boolean userdataEditable = false;
    boolean paymentEditable = false;
    boolean userDataValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        //TODO; WTF. SRLSLY.
        if(getActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fillInData();
    }

    private void fillInData(){
        ImageButton editProfile =  findViewById(R.id.user_editpersonal);
        ImageButton editPayment =  findViewById(R.id.user_editpayment);

        final EditText name = findViewById(R.id.user_name);
        final EditText lastname =  findViewById(R.id.user_lastname);
        final EditText phone = findViewById(R.id.user_phone);
        final EditText email = findViewById(R.id.user_email);
        final EditText username = findViewById(R.id.user_username);
        final EditText street = findViewById(R.id.user_street);
        final EditText zip = findViewById(R.id.user_zip);
        final EditText city = findViewById(R.id.user_city);

        name.setText(User.getName());
        lastname.setText(User.getLast_name());
        phone.setText(User.get_id());
        email.setText(User.getEmail());
        username.setText(User.getUsername());
        street.setText(User.getAdress().getStreet());
        zip.setText(User.getAdress().getZip());
        city.setText(User.getAdress().getCity());

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setFocusable(!userdataEditable);
                name.setFocusableInTouchMode(!userdataEditable);

                lastname.setFocusable(!userdataEditable);
                lastname.setFocusableInTouchMode(!userdataEditable);

                phone.setFocusable(!userdataEditable);
                phone.setFocusableInTouchMode(!userdataEditable);

                email.setFocusable(!userdataEditable);
                email.setFocusableInTouchMode(!userdataEditable);

                username.setFocusable(!userdataEditable);
                username.setFocusableInTouchMode(!userdataEditable);

                if(userdataEditable)
                    checkIfPhoneValid();

                if(userDataValid)
                    userdataEditable = !userdataEditable;
            }
        });

        editPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                street.setFocusable(!paymentEditable);
                street.setFocusableInTouchMode(!paymentEditable);

                zip.setFocusable(!paymentEditable);
                zip.setFocusableInTouchMode(!paymentEditable);

                city.setFocusable(!paymentEditable);
                city.setFocusableInTouchMode(!paymentEditable);

                if(paymentEditable)
                    checkAdress();

                if(userDataValid)
                    paymentEditable = !paymentEditable;
            }
        });
    }

    public void checkAdress(){
        // ToDo: Check if new Adress valid and transmit new data to server
        updateDatabase();
    }
    public void checkIfPhoneValid(){
        // ToDo: Check if new Phone valid and transmit new data to server
        updateDatabase();
    }

    public void updateDatabase(){
        // ToDo: Update the database
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
