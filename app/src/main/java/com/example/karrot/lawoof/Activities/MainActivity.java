package com.example.karrot.lawoof.Activities;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karrot.lawoof.Content.Pet;
import com.example.karrot.lawoof.Content.PetContent;
import com.example.karrot.lawoof.Content.User;
import com.example.karrot.lawoof.Content.Walk;
import com.example.karrot.lawoof.Fragments.AddWalkDialog;
import com.example.karrot.lawoof.Fragments.AddWalkDialogKotlin;
import com.example.karrot.lawoof.Fragments.GoogleMapFragment;
import com.example.karrot.lawoof.Fragments.LostPetFragment;
import com.example.karrot.lawoof.Fragments.PetDetailFragment;
import com.example.karrot.lawoof.R;
import com.example.karrot.lawoof.Util.GPS.GPSService;
import com.example.karrot.lawoof.Util.Help.LatLngTime;
import com.example.karrot.lawoof.Util.Help.LostPets;
import com.example.karrot.lawoof.Util.Help.MarkerData;
import com.example.karrot.lawoof.Util.Serialization.SerializationTask;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete;
import com.google.android.libraries.places.compat.ui.PlaceAutocompleteFragment;
import com.google.android.libraries.places.compat.ui.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Does awesome stuff
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LostPetFragment.OnListFragmentInteractionListener {

    public static final String LOSTPETS_KEY = "LostPets";
//  public GPSService gps = new GPSService();
    protected SupportMapFragment sMapFragment;
    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    public static GoogleMap googleMap;
    protected PlaceAutocompleteFragment autocompleteFragment;

    //Pet history positions
    public static Hashtable<Pet, MarkerData> markerHashTable = new Hashtable<>();

    //User history positions
    public static ArrayList<LatLngTime> myPlaces = new ArrayList<>();
    protected ArrayList<Circle> circles = new ArrayList<>();  //only necessary to remove all circles from map
    private Polyline polyline;
    boolean showHistory = false;

    //Handle lost pets from other people.
    //TODO: replace all this crap with "owner" entry in Pets. temporary fix -.-
    public static ArrayList<LostPets> lostPets = new ArrayList<>();
    public static ArrayList<LostPets> getLostPets() {
        return lostPets;
    }

    private Double latitude = null;
    private Double longitude = null;
    private LatLng petPosition;

    protected Menu menu;
    public Place place = null;

    LostPetFragment lostPetFragment;

    /**
     * Initializes Layout and markers
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initLayout();
        initPetMarkers();
    }

    /**
     * Initializes layout
     */
    private void initLayout() {
        //set layout
        setContentView(R.layout.activity_main);

        // Create new Instance of the MapFragment
        sMapFragment = SupportMapFragment.newInstance();
        PetDetailFragment.mapFragment = SupportMapFragment.newInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true); //its either this or the logo
        }

        //Initialises cute pink button and makes it do it's awesome thang
        //but ze getting of ze position needs to happen in ze background for it to make any sense
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SerializationTask<Walk> s = new SerializationTask<>();
                Walk w = new Walk();
                w.setPets(User.getPets());
                w.setTime("13:37");
               //s.serialize(w);
                String base64 = "rO0ABXNyACNjb20uZXhhbXBsZS5rYXJyb3QubGF3b29mLlV0aWwuV2Fsa0HA0ocF9J1jAgAKWgAH" +
                        "ZGF0ZVNldFoABnBldFNldFoACHBsYWNlU2V0WgAHdGltZVNldEwACGNhbGxiYWNrdAA8TGNvbS9l" +
                        "eGFtcGxlL2thcnJvdC9sYXdvb2YvVXRpbC9DYWxsYWJsZUFjdGl2aXR5L0NhbGxiYWNrVUk7TAAE" +
                        "ZGF0ZXQAEkxqYXZhL2xhbmcvU3RyaW5nO0wACGxvY2F0aW9ucQB-AAJMAAVwbGFjZXQALkxjb20v" +
                        "Z29vZ2xlL2FuZHJvaWQvZ21zL2xvY2F0aW9uL3BsYWNlcy9QbGFjZTtMAAxzZWxlY3RlZFBldHN0" +
                        "ABVMamF2YS91dGlsL0FycmF5TGlzdDtMAAR0aW1lcQB-AAJ4cAABAAFwcHBwc3IAE2phdmEudXRp" +
                        "bC5BcnJheUxpc3R4gdIdmcdhnQMAAUkABHNpemV4cAAAAAN3BAAAAANzcgAiY29tLmV4YW1wbGUu" +
                        "a2Fycm90Lmxhd29vZi5VdGlsLlBldM1LcoC62JAtAgAOTAADYWdlcQB-AAJMAAVicmVlZHEAfgAC" +
                        "TAAJY2FzdHJhdGVkcQB-AAJMAAVjb2xvcnEAfgACTAALZGVzY3JpcHRpb25xAH4AAkwADGZyaWVu" +
                        "ZGxpbmVzc3EAfgACTAACaWRxAH4AAkwACGxvY2F0aW9udAAtTGNvbS9leGFtcGxlL2thcnJvdC9s" +
                        "YXdvb2YvVXRpbC9QZXQkTG9jYXRpb247TAAEbmFtZXEAfgACTAAGcGV0X2lkcQB-AAJMAAZwbGFj" +
                        "ZXNxAH4ABEwAA3NleHEAfgACTAAHc3BlY2llc3EAfgACTAANc3BlY2llc19jbGFzc3EAfgACeHB0" +
                        "AAE0dAAERGVndXQAAWZ0AAZOYXR1ciB0AAZERUdVVVV0AAF0dAABMXNyACtjb20uZXhhbXBsZS5r" +
                        "YXJyb3QubGF3b29mLlV0aWwuUGV0JExvY2F0aW9uZnt403bc40ECAANMAAhsYXRpdHVkZXEAfgAC" +
                        "TAAJbG9uZ2l0dWRlcQB-AAJMAAZ0aGlzJDB0ACRMY29tL2V4YW1wbGUva2Fycm90L2xhd29vZi9V" +
                        "dGlsL1BldDt4cHQAAHEAfgAVcQB-AAp0AANJdnl0ACRjODBhZTliOS1iNjY2LTRkODEtYWUzZi1i" +
                        "ZTgzYjc1YTQ0MmRzcQB-AAYAAAAAdwQAAAAAeHQAAXdxAH4ADHEAfgAMc3EAfgAIcQB-AAtxAH4A" +
                        "DHEAfgAVcQB-ABVxAH4AFXEAfgAVdAABMnNxAH4AEnEAfgAVcQB-ABVxAH4AGnQABVJvYmludAAk" +
                        "NWFmNTU3NDctMzY2OC00MzUxLWJlM2QtMjMwYWNlN2Y3MDkzc3EAfgAGAAAAAHcEAAAAAHhxAH4A" +
                        "FXEAfgAMcQB-AAxzcQB-AAh0AAM2NjZxAH4AFXEAfgAVcQB-ABVxAH4AFXEAfgAVdAABM3NxAH4A" +
                        "EnEAfgAVcQB-ABVxAH4AIHQACEN1dGh1bHUgcHNxAH4ABgAAAAB3BAAAAAB4cQB-ABVxAH4AFXEA" +
                        "fgAVeHQABTEzOjM3";
                s.deserialize(base64);
            }
        });

        //Initialises drawer Menu
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

//        drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        // Initializes Map
//        sMapFragment.getMapAsync(this);
//        FragmentManager sFm = getSupportFragmentManager();
//
//        if (!sMapFragment.isAdded()) {
//            sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
//        } else
//            sFm.beginTransaction().show(sMapFragment).commit();



        //GoogleMapFragment.instantiate(getApplicationContext(),"");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map, GoogleMapFragment.Companion.newInstance());
        fragmentTransaction.commit();


//        int PLACE_PICKER_REQUEST = 1;
//
//        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//
//        try {
//            startActivityForResult(builder.build( this), PLACE_PICKER_REQUEST);
//        } catch (GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//        } catch (GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }

        //place autocomplete search bar
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            /**
             * Navigates to place selected in search bar
             *
             * @param place
             * The selected place
             */
            @Override
            public void onPlaceSelected(Place place) {
                //TODO: Get info about the selected place.
                Log.i("Locations Query", "Place: " + place.getName());

                final CameraPosition cameraPosition2 = new CameraPosition.Builder()
                        .target(place.getLatLng())  // Sets the center of the map to tobi
                        .zoom(10)                  // Sets the zoom for city
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));
            }

            @Override
            public void onError(Status status) {
                Log.i("Locations Query" , "An error occurred: " + status);
            }
        });
    }

    /**
     * Override lifecycle behaviour
     */
    @Override
    protected void onStart() {

        super.onStart();

        turnOnGPS();

        Gson gson = new Gson();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String prefs = null;
        if(preferences.getString(LOSTPETS_KEY, null) != null)
            prefs = preferences.getString(LOSTPETS_KEY, null);
        if(prefs != null) {
            lostPets = gson.fromJson(prefs, new TypeToken<ArrayList<LostPets>>(){}.getType());
            System.out.println("MainActivity: SharedPrefs start test: " + lostPets.toString());
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        Gson gson = new Gson();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String prefs = null;
            if(preferences.getString(LOSTPETS_KEY, null) != null)
                prefs = preferences.getString(LOSTPETS_KEY, null);
            if(prefs != null) {
               lostPets = gson.fromJson(prefs, new TypeToken<ArrayList<LostPets>>(){}.getType());
                System.out.println("MainActivity: SharedPrefs resume test: " + lostPets.toString());

            }
    }

    //TODO: implement pets' last_known and position onStop()
    @Override
    protected void onStop() {
        //TODO: decide what to do with client
        super.onStop();

        //TODO: maybe save pets last known position here.
        //TODO: save as LatLmgTime so we remember last seen time as well
        for(Pet pet : User.getPets()) {
            LatLngTime lastKnownLoc = pet.places.get(pet.places.size()-1);
            pet.lastKnownLoc = lastKnownLoc.position;

            //Use Gson?
            //Stringify this: (maybe just a sublist of this tho -.-')
            ArrayList<LatLngTime> positions = pet.places;

            //then save positions and last_known
        }

//        AppIndex.AppIndexApi.end(client, getIndexApiAction());
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.disconnect();
//        }
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.disconnect();
    }

    @Override
    protected void onPause() {
        //TODO: decide what to do with client
        super.onPause();
    }

    /**
     * Handles results from activities,
     * checks whether google play is installed and
     * handles selected lastKnownLoc from the AddWalkDialog
     *
     * @param requestCode
     * The request code
     * @param resultCode
     * The result code
     * @param data
     * The damn data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Google Play Services must be installed.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        //Handles place auto select event in AddWalkDialog
        if (requestCode == AddWalkDialogKotlin.Companion.getPLACE_AUTOCOMPLETE_REQUEST_CODE()) {
            System.out.println("Activity result called!!");
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(this, data);
                System.out.println("Walk place button" + "Place: " + place.getName());
                AddWalkDialog.walk.setPlace(place);
//                AddWalkDialog.walk.setLocation(place.getLatLng().toString());
                AddWalkDialogKotlin.Companion.getWalk().setPlace(place);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("Walk place button", "Error selecting place " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /**
     * UI stuff
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.*
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        try {
            setUserData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, UserProfileActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.map_normal) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (id == R.id.map_hybrid) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (id == R.id.map_sattelite) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (id == R.id.map_terrain) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (id == android.R.id.home) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START);
            }
        } else if(id == R.id.api_test){
            Intent i = new Intent(this, TestActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles the sidebar menu
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        FragmentManager sFm = getSupportFragmentManager();

        if (id == R.id.nav_sucheStarten) {
            //adds/shows map fragment to container in content_main
            if (!sMapFragment.isAdded()) {
                sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
            }
            lostPetFragment = LostPetFragment.newInstance(1);
            if (!lostPetFragment.isAdded()) {
                sFm.beginTransaction().add(R.id.map, lostPetFragment).addToBackStack(null).commit();
            }

        } else if (id == R.id.nav_safeSpace) {
            //adds map fragment to container in content_main
            if (!sMapFragment.isAdded()) {
                sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
            } else
                sFm.beginTransaction().show(sMapFragment).commit();

        } else if (id == R.id.nav_meineTiere) {
            Intent i = new Intent(this, PetListActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_spaziergangMelden) {
            //TODO: handle detach?
            DialogFragment adw = AddWalkDialog.newInstance(null, null);
            DialogFragment adw2 = AddWalkDialogKotlin.Companion.newInstance();
            adw2.show(sFm, "addWalk");

        } else if (id == R.id.nav_spaziergangEinsehen) {
            Intent i = new Intent(this, TabActivity.class);
            startActivity(i);


        } else if (id == R.id.nav_send) { //Sign out
            AccountManager am = AccountManager.get(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            Account[] accounts = am.getAccountsByType(LoginActivity.ARG_ACCOUNT_TYPE);
            for (Account a : accounts) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    am.removeAccountExplicitly(a);
                    System.out.println("Signing out");
 //                   am.clearPassword(a);
//                    am.setPassword(a, "");
                } else {
                    System.out.println("Signing out");
                    am.removeAccount(a, null, null);
//                    am.clearPassword(a);
//                    am.setPassword(a, "");

                }
            }
            Intent j = new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(j);
            finish();

        //TODO: Implement
        } else if (id == R.id.nav_share) {}

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * Defines initial map behaviour
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //TODO: testing, remove this
        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(this);
        preferences1.edit().remove(LOSTPETS_KEY).apply();

        //TODO: find a better place for this
        //TODO: leave this here, remove from onCreate?
        if (getLostPets() != null) {
            for (LostPets pl : getLostPets()) {
                markerHashTable.put(pl.pet, new MarkerData());
            }
        }

        this.googleMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //TODO: Eventually, in the faraway future, maybe consider removing this
        //allows people to stalk tobi
       // LatLng tobi = new LatLng(51.024162, 7.602552);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //Shows blue myLocation marker
        googleMap.setMyLocationEnabled(true);

        //Make map options appear below toolbar and search bar
        int paddingPercentage = this.getWindow().getDecorView().getHeight() / 60;
        int searchBarHeight = this.getWindow().findViewById(R.id.place_autocomplete_fragment).getHeight();
        int height = this.getSupportActionBar() != null ? this.getSupportActionBar().getHeight() : 0;
        googleMap.setPadding(0, height + paddingPercentage + searchBarHeight, 0, 0);

        if(petPosition != null) {
            final CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(petPosition)                                                            // Sets the center of the map to tobi
                    .zoom(20)                                                                       // Sets the zoom for Buildings
                    .bearing(90)                                                                    // Sets the orientation of the camera to east
                    .tilt(30)                                                                       // Sets the tilt of the camera to 30 degrees
                    .build();                                                                       // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));         // shows blue myLocation marker

        }

        showLocations();
        gotoProfile();
    }

    /**
     * Animates camera to a lost pet position on item click
     * @param item
     * The selected pet in the lost pets list
     */
    @Override
    public void onListFragmentInteraction(LostPets item) {

        try {
            //Animate Camera if dog has more than one position
            final CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(item.pet.places.get(item.pet.places.size()-1).position)  // Sets the center of the map to pet
                    .zoom(18)                   // Sets the zoom for Buildings
                    .build();                   // Creates a CameraPosition from the builder
            MainActivity.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
//
        FragmentManager sFm = getSupportFragmentManager();
        // if (lostPetFragment.isAdded()) {
        sFm.beginTransaction().remove(lostPetFragment).commit();
        //   }
    }

    /**
     * Starts GPS Service
     * Checks if GPS is turned on, if not, redirects to Settings
     */
    public void turnOnGPS() {

 //       final LocationManager locationManager = (LocationManager) this.getSystemService(getApplicationContext().LOCATION_SERVICE);
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("GPS is turned off")
                    .setCancelable(false)
                    .setPositiveButton("Turn GPS On", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //   Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            //   startActivity(intent);

                            new Thread() {
                                public void run() {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                    try {
                                        sleep(10000);
                                        startService(new Intent(getApplicationContext(), GPSService.class));
                                        //turnOnGPS();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        }
                    })
                    .setNegativeButton("Leave GPS off", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            startService(new Intent(this, GPSService.class));
        }
    }

    /**
     * User Data in navigation drawer
     */
    private void setUserData() {

        ImageView userProfileView = findViewById(R.id.main_ProfilePic);
        TextView userProfileUsername = findViewById(R.id.main_UserProfileView);
        TextView userProfileStatus = findViewById(R.id.main_Name);

        userProfileUsername.setText(User.getUsername());
        userProfileStatus.setText(User.getProfileStatus());
        LinearLayout profile = findViewById(R.id.user_profile_layout);

        View.OnClickListener profileClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(i);
            }
        };

        profile.setOnClickListener(profileClick);
        userProfileStatus.setOnClickListener(profileClick);
        userProfileUsername.setOnClickListener(profileClick);
        userProfileView.setOnClickListener(profileClick);
    }

    /**
     * Goes to PetDetailActivity
     */
    public void gotoProfile() {
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (Pet pet : User.getPets()) {
                    if (pet.name.equals(marker.getTitle())) {
                        PetContent.updatePets();
                        Intent intent = new Intent(getApplicationContext(), PetDetailActivity.class);
                        intent.putExtra(PetDetailFragment.ARG_ITEM_ID, pet.id);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });
    }

    /**
     * Initializes pet markers
     */
    private void initPetMarkers() {
        for (Pet p : User.getPets()) {
            markerHashTable.put(p, new MarkerData());
        }
        Intent intent = getIntent();
        if (null != intent) {
            latitude = intent.getDoubleExtra("PetLocation latitude", -1);
            longitude = intent.getDoubleExtra("PetLocation longitude", -1);
        }
        if(latitude != -1)
            petPosition = new LatLng(latitude, longitude);
    }

    /**
     * Show a lastKnownLoc history for each marker
     *
     */
    public void showLocations() {
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            public boolean onMarkerClick(Marker marker) {
                //get pet that marker refers to
                Pet thisPet = null;
                for (Pet pet : User.getPets()) {
                    if (pet.name.equals(marker.getTitle())) {
                        thisPet = pet;
                        break;
                    }
                }
                if(thisPet == null) {
                    String[] s;
                    s = marker.getTitle().split("Owner");
                    for (LostPets lp : getLostPets()) {
                        if (lp.pet.name.equals(s[0].trim())) {
                            thisPet = lp.pet;
                            break;
                        }
                    }
                }

                if (marker.getTitle().equals(User.username)) {
                    // Human!!
                    if (!showHistory) {

                        marker.showInfoWindow();

                        final PolylineOptions po = new PolylineOptions();
                        po.width(5);
                        po.color(Color.argb(200, 255, 0, 0)); //make somewhat transparent maybe
                        po.clickable(true);


                        for (LatLngTime llt : myPlaces) {
                            po.add(llt.position);
                        }
                        polyline = googleMap.addPolyline(po);
                        //TODO: maybe popup pet picture associated with line
                        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                            @Override
                            public void onPolylineClick(Polyline polyline) {
                                System.out.println("Polyline clicked!!");
                            }
                        });

                        /*Circle*/
                        final CircleOptions co = new CircleOptions();
                        co.radius(0.05);
                        co.strokeColor(Color.WHITE);
                        co.fillColor(Color.WHITE);

                        for (LatLngTime llt : myPlaces) {
                            co.center(llt.position);
                            co.clickable(true);
                            Circle circle = googleMap.addCircle(co);
                            circle.setClickable(true);
                            circles.add(circle);
                        }

                        googleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                            @Override
                            public void onCircleClick(Circle circle) {
                                String text = null;
                                for (int i = 0; i < myPlaces.size(); i++) {
                                    if (myPlaces.get(i).position.equals(circle.getCenter())) {
                                        text = "Time was : " + myPlaces.get(i).getTime() + "\nActual time: " + java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                        break;
                                    }
                                }
                                Toast.makeText(getApplicationContext(), text,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        showHistory = true;
                    } else {
                        if (polyline != null) {
                            for (Circle c : circles) {
                                c.remove();
                            }
                            polyline.remove();
                            polyline = null;
                        }
                        showHistory = false;
                        marker.hideInfoWindow();
                    }

                } else if (thisPet != null) {
                    // Es ist ein Tier
                    System.out.println("MainActivity: Pet is: " + thisPet);
                    System.out.println("MainActivity: Markertitle is: " + marker.getTitle());
                    if (! markerHashTable.get(thisPet).isShowHistory()) {
                        marker.showInfoWindow();

                        final PolylineOptions po1 = new PolylineOptions();
                        po1.width(5);
                        po1.color(Color.argb(200, 0, 0, 200)); //make somewhat transparent maybe
                        polyline.setClickable(true);

                        for (LatLngTime llt : thisPet.places) {
                            po1.add(llt.position);
                        }

                        markerHashTable.get(thisPet).setPolyline(googleMap.addPolyline(po1));

                        /*circle*/
                        final CircleOptions co1 = new CircleOptions();
                        co1.radius(0.05);
                        co1.strokeColor(Color.WHITE);
                        co1.fillColor(Color.WHITE);

                        for (LatLngTime llt : thisPet.places) {
                            co1.center(llt.position);
                            co1.clickable(true);
                            Circle circle = googleMap.addCircle(co1);
                            circle.setClickable(true);
                            markerHashTable.get(thisPet).addCircle(circle);
                        }

                        final Pet sameThisPet = thisPet;
                        googleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                            @Override
                            public void onCircleClick(Circle circle) {
                                String text = null;
                                for (int i = 0; i < sameThisPet.places.size(); i++) {
                                    if (sameThisPet.places.get(i).position.equals(circle.getCenter())) {
                                        text = "Time was : " + sameThisPet.places.get(i).time + "\nActual time: " + java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                        break;
                                    }
                                }
                                Toast.makeText(getApplicationContext(), text,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                        markerHashTable.get(thisPet).setShowHistory(true);
                    } else {
                        if (markerHashTable.get(thisPet).getPolylines() != null) {
                            for (Circle c : markerHashTable.get(thisPet).getCircles()) {
                                c.remove();
                            }
                            markerHashTable.get(thisPet).getPolylines().remove();
                        }
                        markerHashTable.get(thisPet).setShowHistory(false);
                        marker.hideInfoWindow();
                    }
                }
                return true;
            }
        });
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //TODO: Consider using this to share pet searches (ex. from website :D). Adds Android app links
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-HTTP-HOST-HERE]/main"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}


