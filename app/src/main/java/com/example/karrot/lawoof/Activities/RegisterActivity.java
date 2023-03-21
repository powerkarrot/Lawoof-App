package com.example.karrot.lawoof.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.karrot.lawoof.R;
import com.example.karrot.lawoof.Util.HTTPS.LawoofApiRequest;
import com.example.karrot.lawoof.Util.Help.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    EditText name;
    EditText lastname;
    EditText email;
    EditText password;
    EditText password_re;
    EditText phone;
    EditText username;
    EditText city;
    EditText zip;
    EditText street;
    LawoofApiRequest request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.karrot.lawoof.R.layout.activity_register);

        //getSupportActionBar().setTitle("LaWoof - Register");

        name =findViewById(R.id.register_name);
        lastname = findViewById(R.id.register_lastname);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        password_re = findViewById(R.id.register_password_re);
        phone = findViewById(R.id.register_phone);
        username = findViewById(R.id.register_username);
        city = findViewById(R.id.register_city);
        zip = findViewById(R.id.register_zip);
        street = findViewById(R.id.register_street);

        Button button = findViewById(R.id.register_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              if( isEmailValid() && passwordsMatch()) {
                  request = new LawoofApiRequest();
                  request.registerUser("test123", email.getText().toString(), lastname.getText().toString(), name.getText().toString(), street.getText().toString(), city.getText().toString(), zip.getText().toString(), username.getText().toString(), password.getText().toString(),
                          new JsonHttpResponseHandler() {

                      @Override
                      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                          try {
                              if (response.get("status").equals("OK")) {
                                  Util.Companion.createAccount(email.getText().toString(), password.getText().toString(), getApplicationContext());
                                  System.out.println("Account created!");
                                  System.out.println("Obj status is: " + response.get("status"));
                                  finish();
                                  updateUI();
                              } else {
                                  System.out.println("Account not created: " + response);
                                  System.out.println("Obj status is: " + response.get("status"));
                              }
                          } catch (JSONException e) {
                              e.printStackTrace();
                          }
                          finish();
                      }


                  });
              }
                /* Todo: TelephonyManager permission chrasht in Android 8 neue permissions checken! Empfehlung: Komplett neu aufbauen die Klasse.
                if( isEmailValid() && passwordsMatch()){
                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getParent(), new String[]{android.Manifest.permission.READ_PHONE_STATE}, 1);
                        return;
                    } else {
                        TelephonyManager tMgr = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            String id = tMgr.getDeviceId() ;
                            /*
                            request = Request.register(id, name.getText().toString(), lastname.getText().toString(),
                                    phone.getText().toString(), password.getText().toString(), username.getText().toString(),
                                    email.getText().toString(), street.getText().toString(), city.getText().toString(), zip.getText().toString(), callback);
                        }
                    }
                }
                */
            }
        });

        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * 0.9f), (int)(height * 0.7f));

        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
    }

    //TODO reimplement when done testing
    public boolean isEmailValid(){
        return email.getText().toString().contains("@");
    }

    public boolean passwordsMatch(){
        return password.getText().toString().equals(password_re.getText().toString());
    }

    public void updateUI() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
