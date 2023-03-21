package com.example.karrot.lawoof.Fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.example.karrot.lawoof.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment


class GoogleMapFragment : Fragment(), OnMapReadyCallback{

    companion object {
        fun newInstance() = GoogleMapFragment()
        var mMap: GoogleMap? = null
    }

    private lateinit var viewModel: GoogleMapViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.googlemap_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GoogleMapViewModel::class.java)

        val mapFragment1 = childFragmentManager
                .findFragmentById(R.id.frg) as? SupportMapFragment
        mapFragment1?.getMapAsync(this)

        //Test!! use of the viewmodel to -partly- initialize map when map is created. Obviously not necessary since there is a callback lel.
        //will keep here as reference.
        viewModel.googleMap.observe(this, object : Observer<GoogleMap> {
            override fun onChanged(t: GoogleMap?) {
                t!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
            }
        })
    }

    override fun onMapReady(map: GoogleMap?) {
        mMap = map as GoogleMap

    }

}
