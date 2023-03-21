package com.example.karrot.lawoof.Fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.karrot.lawoof.Content.Pet
import com.google.android.gms.maps.GoogleMap

class GoogleMapViewModel : ViewModel() {


    //just for testing.
    val googleMap = MutableLiveData<GoogleMap>()

    //for example: Update Map markers when a pet is lost...
    //or: update map when people walking dogs looking for playmates nearby
    //Last one can even just be transmitted per mqtt or similar mechanism, no need
    //to save in database... then just observe that value :D
    val lostPetsMarkers = MutableLiveData<ArrayList<Pet>>()

    //init values go here
    init {

    }

    // TODO: Implement the ViewModel


}
