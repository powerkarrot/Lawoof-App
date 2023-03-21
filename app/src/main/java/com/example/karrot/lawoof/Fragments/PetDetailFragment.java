package com.example.karrot.lawoof.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.karrot.lawoof.Activities.MainActivity;
import com.example.karrot.lawoof.Activities.PetDetailActivity;
import com.example.karrot.lawoof.Activities.PetListActivity;
import com.example.karrot.lawoof.R;
import com.example.karrot.lawoof.Util.GPS.GPSService;
import com.example.karrot.lawoof.Content.Pet;
import com.example.karrot.lawoof.Content.User;
import com.example.karrot.lawoof.Content.PetContent;
import com.example.karrot.lawoof.Util.Help.LostPets;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.SYSTEM_HEALTH_SERVICE;
import static com.example.karrot.lawoof.Activities.MainActivity.LOSTPETS_KEY;

/**
 * A fragment representing a single Pet detail screen.
 * This fragment is either contained in a {@link PetListActivity}
 * in two-pane mode (on tablets) or a {@link PetDetailActivity}
 * on handsets.
 */
public class PetDetailFragment extends Fragment implements OnMapReadyCallback {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    //TODO: make isSearch not static
    static final int PICK_CONTACT=1;
    public static ArrayList<String[]> contacts = new ArrayList<>();
    private boolean isSearch = false;
    public static ArrayList <String> emailcontacts = new ArrayList<>();
    public static ArrayList<String[]> getContacts() {
        return contacts;
    }
    public  boolean isSearch() {
        return isSearch;
    }

    public static String ARG_ITEM_ID = "item_id";
    public static GoogleMap map;
    public static SupportMapFragment mapFragment;
    public static Marker singlePetMarker;
    public static Hashtable<Pet, Boolean>searchPet = new Hashtable<>();
    public LostPets lp = new LostPets(null, null);
    SharedPreferences preferences;

    MarkerOptions petMarkerOptions = new MarkerOptions();
    EditText pet_age;
    EditText pet_class;
    EditText pet_species;
    EditText pet_breed;
    EditText pet_color;
    EditText pet_sex;
    EditText pet_castrated;
    RatingBar pet_friendliness;
    EditText pet_description;
    Switch lost;


    /**
     * The dummy content this fragment is presenting.
     */
    public Pet mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PetDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mapFragment = SupportMapFragment.newInstance();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = PetContent.PET_MAP.get(getArguments().getString(ARG_ITEM_ID));
            System.out.println("MITEM: " + mItem + " id: " + ARG_ITEM_ID);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.name);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pet_detail, container, false);

        // Show the dummy content as text in a TextView.

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.get_pet_description)).setText(mItem.description);

            pet_age = rootView.findViewById(R.id.get_pet_age);
            pet_class = rootView.findViewById(R.id.get_pet_class);
            pet_species = rootView.findViewById(R.id.get_pet_species);
            pet_breed = rootView.findViewById(R.id.get_pet_breed);
            pet_color = rootView.findViewById(R.id.get_pet_color);
            pet_sex = rootView.findViewById(R.id.get_pet_sex);
          //  pet_castrated = rootView.findViewById(R.id.get_pet_castrated);
            pet_friendliness = rootView.findViewById(R.id.walk_difficulty);
           // pet_description = rootView.findViewById(R.id.get_pet_description);
            lost = rootView.findViewById(R.id.lost_switch);

            pet_age.setText(mItem.age);
            pet_class.setText(mItem.species_class);
            pet_species.setText(mItem.species);
            pet_breed.setText(mItem.breed);
            pet_color.setText(mItem.color);
            pet_sex.setText(mItem.sex);

            //get switch position for individual pet
            //will remember state across restarts
            preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            lost.setChecked(preferences.getBoolean(mItem.name+"switch", false));
            lp.owner = User.email;
            lp.pet = mItem;

            lost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                   // preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    isSearch = preferences.edit().putBoolean(mItem.name+"switch", b).commit();
                    if (b) {

                        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        System.out.println(i.getData());
                        startActivityForResult(i, PICK_CONTACT);
                        GPSService.getOwnLostPets().add(mItem);

                        //TODO: pick one

                       // if(!MainActivity.getLostPets().contains(lp.pet.name) && (!MainActivity.getLostPets().contains(lp.owner))) MainActivity.lostPets.add(lp);
                        boolean bool = true;
                        for(LostPets tmp : MainActivity.getLostPets()) {
                            System.out.println("PetDetail: " + tmp.pet.toString() + "" + tmp.owner);
                            if((tmp.pet.name.equals(lp.pet.name))) {
                                bool = false;
                            }
                        }
                        System.out.println("PetDetail: " + bool);
                        if(bool) {
                            MainActivity.getLostPets().add(lp);
                        }
                        /*
                        if(!MainActivity.getLostPets().contains(lp.pet)) {
                            MainActivity.getLostPets().add(lp);
                        }
*/



                        //TODO: change it so it doesnt save a possibly VERY long places array -.-
                        //TODO: as of now it is not possible to remove another users pet from alllostpets
                        Gson gson = new Gson();
                        String json = gson.toJson(MainActivity.lostPets);

                        //TODO: changed commit() to apply(). Test.
                        preferences.edit().remove(LOSTPETS_KEY).apply();
                        //TODO: here!!
                        preferences.edit().putString(LOSTPETS_KEY, json).apply();
                        System.out.println("PetDetailFragment: SharedPrefs own lost pets: " + GPSService.getOwnLostPets().toString());
                        System.out.println("PetDetailFragment: SharedPrefs all lost pets: " + MainActivity.lostPets.toString());

                    } else {
                        GPSService.getOwnLostPets().remove(mItem);

                        /*
                        boolean bool = false;
                        for(LostPets tmp : MainActivity.getLostPets()) {
                            if(tmp.pet.name.equals(lp.pet.name)) {
                                bool = true;
                                break;
                            }
                        }
                        if(bool) {
                            MainActivity.getLostPets().remove(lp);
                        }
                        */

                        Boolean boo = false;
                        int a = -1;
                        for(int i = 0; i < MainActivity.getLostPets().size(); i++) {
                            if (MainActivity.getLostPets().get(i).pet.name.equals(lp.pet.name)) {
                                a = i;
                                boo = true;
                                break;
                            }
                        }
                        if (boo) MainActivity.getLostPets().remove(a);

                        System.out.println("PetDetail: removed:" + boo);
                        Gson gson = new Gson();
                        String json = gson.toJson(MainActivity.getLostPets());
                        //TODO: test change from commit() to apply()
                        preferences.edit().remove(LOSTPETS_KEY).apply();
                        preferences.edit().putString(LOSTPETS_KEY, json).apply();
                        System.out.println("PetDetailFragment: SharedPrefs own lost pets: " + GPSService.getOwnLostPets().toString());
                        System.out.println("PetDetailFragment: SharedPrefs all lost pets: " + MainActivity.getLostPets().toString());
                    }
                }
            });
        }

        mapFragment.getMapAsync(this);
        FragmentManager sFm = getActivity().getSupportFragmentManager();

        if (!mapFragment.isAdded()) {
            sFm.beginTransaction().add(R.id.map, mapFragment).commit();
        } else
            sFm.beginTransaction().show(mapFragment).commit();
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(false);
        Pet thispet = null;
        for (Pet pet : User.getPets()) {
            if (pet.name.equals(mItem.name)) {
                thispet = pet;
            }
        }

        int height = 100;
        int width = 100;
        BitmapDrawable icon = (BitmapDrawable) getResources().getDrawable(R.drawable.paw); //paw is cute, paw is awesome!!
//        BitmapDrawable icon2 = (BitmapDrawable)getResources().getDrawable(R.drawable.pp);
        Bitmap b = icon.getBitmap();
        Bitmap pawMarker = Bitmap.createScaledBitmap(b, width, height, false);

        petMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(pawMarker));

        if (thispet != null) {
            petMarkerOptions.position(thispet.places.get(thispet.places.size() - 1).position).title(thispet.toString());
            singlePetMarker = map.addMarker(petMarkerOptions);

            final CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(singlePetMarker.getPosition())  // Sets the center of the map to tobi
                    .zoom(20)                   // Sets the zoom for Buildings
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));        // shows blue myLocation marker
         }

        final Pet transfer = thispet;

        System.out.println("PetDetailFragment: "+ transfer.places + transfer.toString());
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {


                Intent i = new Intent(getContext(), MainActivity.class);
                Bundle mBundle = new Bundle();
                if(transfer.places.size() > 1 && transfer.places != null) {
                    mBundle.putDouble("PetLocation latitude", transfer.places.get(transfer.places.size() - 1).position.latitude);
                    mBundle.putDouble("PetLocation longitude", transfer.places.get(transfer.places.size() - 1).position.longitude);
                    i.putExtras(mBundle);

                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        String[] help = new String[2];

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c = getActivity().managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {

                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        // String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.CONTENT_ITEM_TYPE.));

                        Cursor emails = getActivity().getContentResolver().query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id,
                                null, null
                        );
                        if(emails != null) {
                            emails.moveToFirst();

                            help[0] = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            emailcontacts.add(help[0]);
                            System.out.println("PetDetailActivity: emails are:" + help[0]);
                            help[1] = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            contacts.add(help);
                            System.out.println("PetDetailActivity: Contacts are:" + contacts.toString());
                            emails.close();
                        }
                    }
                }
                //  }
                break;
        }
    }
}
