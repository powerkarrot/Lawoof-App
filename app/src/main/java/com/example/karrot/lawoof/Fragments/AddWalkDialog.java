package com.example.karrot.lawoof.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.karrot.lawoof.Util.Help.MultiSpinner;
import com.example.karrot.lawoof.R;
import com.example.karrot.lawoof.Util.CallableActivity.CallbackUI;
import com.example.karrot.lawoof.Content.Pet;
import com.example.karrot.lawoof.Content.User;
import com.example.karrot.lawoof.Content.Walk;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.DialogFragment;


/**
 * Created by karrot on 24.02.2017.
 */

public class AddWalkDialog extends DialogFragment implements CallbackUI {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 100;
    CallbackUI mContext;
    EditText title;
    EditText date;
    EditText time;
    EditText location;
    public static Walk walk = new Walk();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddWalkDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddWalkDialog.
     */
    // TODO: Eventually remove unnecessary paramenters.
    public static AddWalkDialog newInstance(String param1, String param2) {
        AddWalkDialog fragment = new AddWalkDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            //huh??
            User.myWalks.add(walk);
  //          System.out.println("Walk on on create: " + walk.getTitle());

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO: Add Walk Dialog color is blue. -.-'

        getDialog().setTitle("Go take a walk\n you effin pussay");
        walk.setCallback(this);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_walk, container, false);

        MultiSpinner pets = view.findViewById(R.id.walk_spinner);

        /*
         Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, PetContent.PETS);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        */
        final List<String> list = new ArrayList<String>();
        for(Pet p : User.pets){
            list.add(p.name);
        }
        pets.setItems(list, "Pick pets", new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                ArrayList<Pet> allPets = User.pets;
                ArrayList<Pet> selectedPets = new ArrayList<>();
                for(int i  = 0; i < allPets.size(); i++){
                    if(selected[i])
                        selectedPets.add(allPets.get(i));
                }
                walk.setPets(selectedPets);
            }
        });

        mContext = this;
        title = view.findViewById(R.id.walk_add_title);
        date = view.findViewById(R.id.walk_date);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Eventhandler implementieren!
                //DialogFragment newFragment = new DatePickerFragment();
                DatePickerFragment dt = new DatePickerFragment();
                dt.setCallback(mContext);
                dt.show(getActivity().getSupportFragmentManager(), "datePicker");
                //newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");


            }
        });
        time = view.findViewById(R.id.walk_time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Eventhandler implementieren!
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setCallback(mContext);
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });

        location = view.findViewById(R.id.walk_place);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //starts place autoselect intent from mainactivity, main activity waits for result:
                    //handle event in MainActivity method onActivityResult!
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(getActivity());
                    getActivity().startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        Button registerWalk = view.findViewById(R.id.btn_register_walk);
        registerWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walk.setTitle(title.getText().toString());
                User.myWalks.get(0).setTitle(title.getText().toString());
                getDialog().dismiss();
            }
        });
        return view;
    }

    // TODO: Rename method, updateUI argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
            System.out.print("ADDWALK: " + uri);
        }
    }

    @Override
    public void onDetach() {
        addPetsToWalk();
        super.onDetach();
        mListener = null;
        System.out.println("New walk: " + walk.getTitle());
    }

    private void addPetsToWalk() {}

    @Override
    public void updateUI(String... params) {
        switch (params[0]){
            case "date":
                date.setText(params[1]);
                walk.setDate(params[1]);
                User.myWalks.get(0).setDate(params[1]);

                break;
            case "time":
                time.setText(params[1]);
                walk.setTime(params[1]);
                User.myWalks.get(0).setTime(params[1]);

                break;
            case "lastKnownLoc":
                location.setText(params[1]);
                User.myWalks.get(0).setLocation(params[1]);

        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
