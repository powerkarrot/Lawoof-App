package com.example.karrot.lawoof.Util.GPS;

import com.example.karrot.lawoof.Activities.MainActivity;
import com.example.karrot.lawoof.Fragments.GoogleMapFragment;
import com.example.karrot.lawoof.Fragments.PetDetailFragment;
import com.example.karrot.lawoof.R;
import com.example.karrot.lawoof.Util.Help.LatLngTime;
import com.example.karrot.lawoof.Util.Help.LostPets;
import com.example.karrot.lawoof.Util.MQTT.MQTTClient;
import com.example.karrot.lawoof.Content.Pet;
import com.example.karrot.lawoof.Content.User;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.*;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Hashtable;

import androidx.core.app.ActivityCompat;

/**
 * Created by karrot on 02/03/2017.
 *
 * Service that listens for User and Pet Location changes
 */

public class GPSService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static boolean isRunning = false;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    protected MarkerOptions userMarkerOptions = new MarkerOptions();
    protected MarkerOptions petMarkerOptions = new MarkerOptions();
    protected Marker myMarker;
    protected ArrayList<Marker> petMarkers = new ArrayList<>();
    protected MainActivity mainActivity;
    protected MQTTClient MQTTClient;
    protected User user;
    protected LatLng updatedPosition;

    //Handle others' lost pets
    protected ArrayList<Marker> lostPetsMarkers = new ArrayList<>();
    protected MarkerOptions lostPetOptions = new MarkerOptions();

    //Handle own lost pets
    protected static ArrayList<Pet> ownLostPets = new ArrayList<>();

    public static ArrayList<Pet> getOwnLostPets() {
        return ownLostPets;
    }

    //TODO: Hashmap pets to markers. lost pet markers get different color.
    public Hashtable<Pet, Marker> markerHashtable = new Hashtable<>();

    /**
     * On create, service starts and configures Google API client, begins listening for location updates
     * and starts MQTT Client
     */
    @Override
    public void onCreate() {

        super.onCreate();
        System.out.println("Starting MQTT Client");
        MQTTClient = new MQTTClient();
        try {
            MQTTClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("MyService", "Service Started.");
        System.out.println("Starting GPS Client");
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        isRunning = true;

        //try to connect
        try {
            buildGoogleApiClient();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //lel
        }
        createLocationRequest();
        System.out.println("All Done!");
    }

    /**
     * Connects Google API client when service is started
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("GPS Service", "Received start id " + startId + ": " + intent);
        if (mGoogleApiClient == null) {
            mGoogleApiClient.connect();
        }
        // If  android  has to kill the service to free up valuable resources,
        // then restart the service when resources become available again
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Initializes pet markers once connected
     */
    @Override
    public void onConnected(Bundle bundle) {
        initPetMarkers();
    }

    @Override
    public void onConnectionFailed(ConnectionResult bundle) {}

    @Override
    public void onConnectionSuspended(int i) {}

    /**
     * LocationRequest Callback
     * Called by startLocationUpdates()
     *
     * Defines behaviour on changed location:
     * - Updates pet and user positions
     * - Handles marker movements
     * - Publishes lost pets locations
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Simulates movement:
        removeMarkers();

        //last known location becomes new location
        mLastLocation = location;
        updatedPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        updatePositions();
    }

    /**
     * Configures Google Api Client
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
//                .addApi(com.google.android.gms.location.places.Places.GEO_DATA_API)
       //         .addApi(Places.GEO_DATA_API)
                .build();

        mGoogleApiClient.connect();
    }

    /**
     * Gets location updateUI using the LocationListener callback approach
     * (alternative: pending intent with location in extended data)
     * because getMyLocation() is deprecated. Of  course. Why make life easier.
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Updates User and Pet position,
     * sets marker in last known pet location for all pets that receive location updates and
     * publishes own lost pets location data
     */
    private void updatePositions() {
        //User Marker
        if (updatedPosition != null) userMarkerOptions.position(updatedPosition).title(User.username);
        //TODO: Karrots was here
//        myMarker = mainActivity.googleMap.addMarker(userMarkerOptions);
        myMarker = GoogleMapFragment.Companion.getMMap().addMarker(userMarkerOptions);

        //Save place and time in array to create circles in map (done in MainActivity)
        LatLngTime lt = new LatLngTime(new LatLng(myMarker.getPosition().latitude, myMarker.getPosition().longitude));
        MainActivity.myPlaces.add(lt);

        //Users' pets markers
        for(Pet pet : User.getPets()) {
            Marker m;
            System.out.println("GPSService: Pet data for Tracking: + " + pet.places.toString());
            if(pet.places.size() > 1) {
                System.out.println("TIERE PLÄTZE: " + pet + pet.places.toString());
                petMarkerOptions.position(pet.places.get(pet.places.size() - 1).position).title(pet.name);
                m = mainActivity.googleMap.addMarker(petMarkerOptions);
                petMarkers.add(m);

                //updates pet marker position and moves camera in PetDetailFragment
                if(PetDetailFragment.singlePetMarker != null) {
                    if (pet.name.toString().equals(PetDetailFragment.singlePetMarker.getTitle())) {

                        PetDetailFragment.singlePetMarker = PetDetailFragment.map.addMarker(petMarkerOptions);

                        final CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(PetDetailFragment.singlePetMarker.getPosition())  // Sets the center of the map to pet
                                .zoom(18)                   // Sets the zoom for Buildings
                                .build();                   // Creates a CameraPosition from the builder
                        PetDetailFragment.map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
                //publishes own lost pets locations
                if (ownLostPets.size() > 0) {
                    for (Pet p : ownLostPets) {
                        if(p == pet) {
                            LatLngTime llt = new LatLngTime(petMarkerOptions.getPosition());
                            System.out.println("GPSService: lost pet " + p.name + " position at time: " + llt);
                            MQTTClient.publish(llt, p);
                        }
                    }
                }
            }
        }
        //draws markers for other peoples lost pets
        if(MainActivity.getLostPets().size() > 0) {
            for (LostPets lp : MainActivity.getLostPets()) {
                Marker l;
                if (lp.pet.places.size() > 1 && !lp.owner.toString().equals(User.email)) {
                    lostPetOptions.position(lp.pet.places.get(lp.pet.places.size() - 1).position)
                            .title(lp.pet.name).snippet("Owner:" + lp.owner);
                    l = MainActivity.googleMap.addMarker(lostPetOptions);
                    lostPetsMarkers.add(l);
                }
            }
        }
    }

    /**
     * Removes markers with outdated positions in all maps
     */
    private void removeMarkers() {
        //Pets in MainActivity
        try {
            for (Marker m : petMarkers) {
                m.remove();
            }
            petMarkers.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //User in MainActivity
        myMarker.remove();

        //Lost Pets from other people
        try {
            for (Marker m : lostPetsMarkers) {
                m.remove();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Single Pet in PetDetailFragment
        if (PetDetailFragment.singlePetMarker != null) PetDetailFragment.singlePetMarker.remove();
    }

    /**
     * Gets users' last known position and initializes user and pet markers
     */
    private void initPetMarkers() {
        //Set to last known Position
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //Gets the users' last known location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            LatLng lastKnownLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            System.out.println("Last known position is " + lastKnownLocation.toString());

            userMarkerOptions.position(lastKnownLocation).title(User.username);
            // BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.paw);

            //We don't want a new marker every time we change activities -.-'
            if (userMarkerOptions.getPosition() != null) {
                //TODO: Karrots was here
                //mainActivity.googleMap.clear();
                    GoogleMapFragment.Companion.getMMap().clear();
                //TODO: Karrots was here
                //myMarker = mainActivity.googleMap.addMarker(userMarkerOptions);
                myMarker = GoogleMapFragment.Companion.getMMap().addMarker(userMarkerOptions);


            }

            //Make marker icon for pets
            int height = 100;
            int width = 100;
            BitmapDrawable icon = (BitmapDrawable) getResources().getDrawable(R.drawable.paw); //paw is cute, paw is awesome!!
            // BitmapDrawable icon2 = (BitmapDrawable)getResources().getDrawable(R.drawable.pp);
            Bitmap b = icon.getBitmap();
            Bitmap pawMarker = Bitmap.createScaledBitmap(b, width, height, false);

            petMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(pawMarker));

            lostPetOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

            //go to last known position on app start
            final CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(lastKnownLocation)  // Sets the center of the map to tobi
                    .zoom(20)                   // Sets the zoom for Buildings
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            //mainActivity.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            //TODO: Karrots was here
            GoogleMapFragment.Companion.getMMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            // Callback to onLocationChanged
            startLocationUpdates();
        }
    }

    /**
     * Creates and configures LocationRequest
     */
    protected void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Returns whether GPSService is running
     */
    public static boolean isRunning()
    {
        return isRunning;
    }
}
