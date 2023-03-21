package com.example.karrot.lawoof.Util.Help;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * Created by karrot on 08/03/2017.
 *
 * Helper class to map users and pets to a marker
 */

public class MarkerData {

    private Polyline polyline;
    private ArrayList<Circle> circles = new ArrayList<>();
    private boolean showHistory = false;
    private Marker marker;

    public void setPolyline(Polyline polyline){
        this.polyline = polyline;
    }

    public void addCircle(Circle circle){
        circles.add(circle);
    }

    public Polyline getPolylines() {
        return polyline;
    }

    public ArrayList<Circle> getCircles() {
        return circles;
    }

    public void setCircles(ArrayList<Circle> circles) {
        this.circles = circles;
    }

    public boolean isShowHistory() {
        return showHistory;
    }

    public void setShowHistory(boolean showHistory) {
        this.showHistory = showHistory;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
