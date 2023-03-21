package com.example.karrot.lawoof.Activities;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.karrot.lawoof.Content.User;
import com.example.karrot.lawoof.R;
import com.example.karrot.lawoof.Util.HTTPS.LawoofApiRequest;
import com.example.karrot.lawoof.Util.Help.Util;
import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 123;

    public static String ARG_ACCOUNT_TYPE = "example.com";
    public static String PARAM_USER_PASS;
    public static String ARG_AUTH_TYPE;
    public static String ARG_IS_ADDING_NEW_ACCOUNT;
    public static String KEY_ACCOUNT_AUTHENTICATOR_RESPONSE;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    public String password;
    public String email;
    AccountManager accountManager;
    List<String> emails = new ArrayList<>();
    LawoofApiRequest lawoofApiRequest = new LawoofApiRequest();
    TextView txtError;
    Account currentAccount;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme_Launcher);

        super.onCreate(savedInstanceState);

        initLayout();

        //Does user have account?
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkAndRequestPermissions()) {
                System.out.println("Not all permissions were granted");
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        showProgress(true);

        accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(ARG_ACCOUNT_TYPE);
        if (accounts.length >= 1) {
            //TODO:  Either give user a choice... and properly set current account lel; Else only allow one local account. For now, only a single local account is allowed.
            currentAccount = accounts[0];
            email = accounts[0].name;
            password = accountManager.getPassword(accounts[0]);

            //in case we were set the password to null instead of deleting account when signing out lel.
            // then just refill the password. if only this class were a little less horrid and all over the place. I wants to rewrite it.
            if (!password.isEmpty()) submit();

            //New user or user has signed out.
        } else {
            showProgress(false);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();
        for (Account a : accounts) {
            if (a.name.contains("@")) {
                boolean add = true;
                for (String s : emails) {
                    if (s.equals(a.name.trim())) {
                        add = false;
                        break;
                    }
                }
                if (add)
                    emails.add(a.name);
            }
        }

        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(this.emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    /**
     * Initializes layout
     */
    private void initLayout() {
        setContentView(R.layout.activity_login);

        // Set up the login form.
        txtError = findViewById(R.id.txtError);

        mEmailView = findViewById(R.id.email);
        if (Build.VERSION.SDK_INT <= 23) {
            populateAutoComplete();
        }
        populateAutoComplete();
        mPasswordView = findViewById(R.id.password);
        //As soon as user types in password, he can attempt to login
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        OnClickListener error = new OnClickListener() {
            @Override
            public void onClick(View view) {
                txtError.setVisibility(View.INVISIBLE);
            }
        };

        mEmailView.setOnClickListener(error);
        mPasswordView.setOnClickListener(error);

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Apparently unnecessary since android 6 ¯\_(ツ)_/¯
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            return;
        }

        //Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);  //  calls load screen
            submit();
            //mAuthTask = new UserLoginTask(email, password);
            // mAuthTask.execute((Void) null);
        }
    }

    /**
     * Attempts to authenticate login
     */
    public void submit() {
        //ToDO Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            return;
        } else {
            TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                System.out.println(tMgr.getDeviceId());
                // ToDo: use UniqueID
                showProgress(true);
                System.out.println("Submitting");

                lawoofApiRequest.login(email, password, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        System.out.println("Response Success: " + response + " " + email + " " + password + "Status: " + statusCode);
                        try {
                            System.out.println("Response Success: " + response.get("status").toString());
                            if (response.get("status").toString().equals("Success")) {
                                User.setStatus("Success");

                                //In case user has signed out or deleted local account
                                Util.Companion.createAccount(email, password, getApplicationContext());
                                finish();
//                                updateUI();     //LEL nicht wirklich Sinn der Sache aber hey
                            } else {
                                //TODO further differentiation
                                if (response.get("value").toString().equals("User/Password is not correct!")) {
                                    System.out.println(response.get("value"));
                                    User.setStatus("NoAccount");
//                                    User.setStatus("WrongAuth");
//                                    showProgress(false);
                                    finish();
                                } else {
                                    User.setStatus("NoAccount");
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finish();
                        updateUI();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String response, Throwable t) {
                        System.out.println("Response Failure: " + response);

                        finish();
                    }
                });
            }
        }
    }

    /**
     * Decides what to do depending on submit() result
     */
    public void updateUI() {
        final Intent res = new Intent();
        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, email);
        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, "ex3c.de");
        res.putExtra(AccountManager.KEY_AUTHTOKEN, "test");
        res.putExtra(PARAM_USER_PASS, password);
        //TODO delete after testing
        User.setEmail(email);
        User.set_id(email);

        // Check if login was shucsheshful
        System.out.println("Status: " + User.getStatus());
        if (User.getStatus().equals("Success")) {

            finishLogin(res);
        } else if (User.getStatus().equals("NoAccount")) {

            System.out.println(User.getStatus().equals("NoAccount"));
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        } else if (User.getStatus().equals("WrongAuth")) {

        } else {
            txtError.setVisibility(View.VISIBLE);
        }

        showProgress(false);
    }

    /**
     * Initializes User and starts MainActivity
     */
    private void finishLogin(Intent intent) {
        this.intent = intent;

        setResult(RESULT_OK, intent);
        initUser();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        showProgress(false);
        finish();
    }

    /**
     * Initializes user
     */
    private void initUser() {
        User.setEmail(email);
        User.set_id(email);
        Util.Companion.updatePets();
        Util.Companion.updateWalks();
    }

    /**
     * @return Returns true if permissions checked/requested
     */
    private boolean checkAndRequestPermissions() {
//        int permissionReadContacts = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_CONTACTS);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
//        if (permissionReadContacts != PackageManager.PERMISSION_GRANTED) {
//              listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
//        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    /**
     * Populates autocomplete email form
     */
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Checks permissions
     */
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}





