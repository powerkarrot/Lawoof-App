package com.example.karrot.lawoof.Fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.karrot.lawoof.Content.Pet
import com.example.karrot.lawoof.Content.User
import com.example.karrot.lawoof.Content.Walk
import com.example.karrot.lawoof.R
import com.example.karrot.lawoof.Util.CallableActivity.CallbackUI
import com.example.karrot.lawoof.Util.HTTPS.LawoofApiRequest
import com.example.karrot.lawoof.Util.Help.MultiSpinner
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import java.util.ArrayList


/**
 * Created by karrot on 24.02.2017.
 */

//TODO: not updating database. Check the horrid callback thing.
class AddWalkDialogKotlin : DialogFragment(), CallbackUI {
    private var mContext: CallbackUI = this
    internal var title: EditText = view!!.findViewById(R.id.walk_add_title)
    internal var date: EditText  = view!!.findViewById(R.id.walk_date)
    internal var time: EditText  = view!!.findViewById(R.id.walk_time)
    internal var location: EditText = view!!.findViewById(R.id.walk_place)
    internal var request = LawoofApiRequest()

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)

            //huh??
 //           User.myWalks.add(walk)
            //System.out.println("Walk on on create: " + walk.getTitle());

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // TODO: Add Walk Dialog color is blue. -.-'

        dialog.setTitle("Go take a walk\n you effin pussay")
        walk.setCallback(this)

        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.activity_walk, container, false)


        //val pets = view.findViewById(R.id.walk_spinner)
        val pets = activity!!.findViewById<MultiSpinner>(R.id.walk_spinner)

        /*
         Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, PetContent.PETS);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        */
        val list = ArrayList<String>()
        for (p in User.pets) {
            list.add(p.name)
        }
        // Todo: Fix

        pets.setItems(list, "Pick pets") { selected ->
            val allPets = User.pets
            val selectedPets = ArrayList<Pet>()
            for (i in allPets.indices) {
                if (selected[i])
                    selectedPets.add(allPets[i])
            }
            walk.setPets(selectedPets)
        }


        mContext = this

        date.setOnClickListener {
            // TODO: Eventhandler implementieren!
            //DialogFragment newFragment = new DatePickerFragment();
            val dt = DatePickerFragment()
            dt.setCallback(mContext)
            dt.show(activity!!.supportFragmentManager, "datePicker")
            //newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        }

        time.setOnClickListener {
            // TODO: Eventhandler implementieren!
            val newFragment = TimePickerFragment()
            newFragment.setCallback(mContext)
            newFragment.show(activity!!.supportFragmentManager, "timePicker")
        }

        location.setOnClickListener {
            try {
                //starts place autoselect intent from mainactivity, main activity waits for result:
                //handle event in MainActivity method onActivityResult!
                val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .build(activity)
                activity!!.startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
            } catch (e: GooglePlayServicesRepairableException) {
                // TODO: Handle the error.
            } catch (e: GooglePlayServicesNotAvailableException) {
                // TODO: Handle the error.
            }
        }

        val registerWalk = view.findViewById<Button>(R.id.btn_register_walk)
        registerWalk.setOnClickListener {
            walk.setTitle(title.text.toString())
            User.myWalks[0].title = title.text.toString()
            dialog.dismiss()
        }
        return view
    }

    // TODO: Rename method, updateUI argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
            print("ADDWALK: $uri")
        }
    }

    //TODO: Set description, set difficulty, set locationtype (try to have maps decide!), add participants
    override fun onDetach() {
        val participants = arrayOf("Participant1", "Participant2").toString() //tmp fix

        updateUI()
        addPetsToWalk()
        super.onDetach()
        mListener = null
    }

    //TODO. Also missing from database
    private fun addPetsToWalk() {}

    override fun updateUI(vararg params: String) {
        when (params[0]) {
            "date" -> {
                date.setText(params[1])
                walk.date = params[1]
                //Why overwrite the first entry???
                User.myWalks[0].date = params[1]
            }
            "time" -> {
                time.setText(params[1])
                walk.time = params[1]
                User.myWalks[0].time = params[1]
            }
            "location" -> {
                location.setText(params[1])
 //               walk.setLocation(params[1])
                User.myWalks[0].location = params[1]
            }
        }
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    //Check whether walk really needs to be static, although it simplifies it a bit
    //Don't feel like worrying about it today, sawrry :P
    companion object {

        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"
        var PLACE_AUTOCOMPLETE_REQUEST_CODE = 100
        var walk = Walk()

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddWalkDialog.
         */
        // TODO: Eventually remove unnecessary paramenters.
        fun newInstance(): AddWalkDialog {
            val fragment = AddWalkDialog()
            val args = Bundle()
//            args.putString(ARG_PARAM1, param1)
//            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
