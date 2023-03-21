package com.example.karrot.lawoof.Util.Help;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

/**
 * Created by karrot on 05/03/2017.
 *
 *
 * Saves Location and Time for location history in MainActivity map (the Circles and Polylines)
 * Instantiated in onLocationChanged
 */

public class LatLngTime {

    public LatLng position;
    public String time;
    Calendar calendar;

    public LatLngTime(LatLng position) {
        calendar = Calendar.getInstance();
        this.position = position;
    }

    /**
     * Constructor to parse data from MWTT Message
     * @param position
     * @param time
     */
    public LatLngTime(String position, String time) {
        String[] latlng = position.split(",");
        double latitude = Double.parseDouble(latlng[0]);
        double longitude = Double.parseDouble(latlng[1]);
        this.position = new LatLng(latitude, longitude);
        this.time = time;
    }

    public String getTime() {
        return java.text.DateFormat.getDateTimeInstance().format(calendar.getTime());
    }

    public String toString() {
        return position.latitude + "," + position.longitude + "-" + time;
    }
}
