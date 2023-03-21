package com.example.karrot.lawoof.Content;

import android.preference.PreferenceActivity;

import com.example.karrot.lawoof.Util.CallableActivity.CallbackUI;
import com.example.karrot.lawoof.Util.HTTPS.LawoofApiRequest;
import com.example.karrot.lawoof.Util.Help.Util;
import com.example.karrot.lawoof.Util.Serialization.SerializationTask;
import com.google.android.libraries.places.compat.Place;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static java.sql.DriverManager.println;

/**
 * Created by ex3c on 27.02.2017.
 */

public class Walk implements Serializable {
    public  Place place;
    private  ArrayList<Pet> selectedPets = new ArrayList<Pet>();
    private static int amountWalks = -1;

    private  String time, date , walkid, documentid, title, description, difficulty, location, locationName, locationtype;
    private ArrayList<String> participants = new ArrayList<String>();
    private  boolean placeSet = false;
    private  boolean timeSet = false;
    private  boolean dateSet = false;
    private  boolean petSet = false;
    private boolean titleSet = false;
    private  CallbackUI callback;
    Gson gson = new Gson();


    public  void setPlace(Place place) {
        this.place = place;
        this.placeSet = true;
        this.checkIfReadyToSend();
        SerializationTask<Place> serializationTask = new SerializationTask<Place>();
        serializationTask.serialize(place);

        if(callback != null)
            callback.updateUI("location", this.place.getName().toString());
    }

    public  void setCallback(CallbackUI callback){
        this.callback = callback;
    }
    public Place getPlace() {
        return place;
    }

    public ArrayList<Pet> getSelectedPets() {
        return selectedPets;
    }

    public void setSelectedPets(ArrayList<Pet> selectedPets) {
        this.selectedPets = selectedPets;
    }

    public static int getAmountWalks() {
        return amountWalks;
    }

    public static void setAmountWalks(int amountWalks) {
        Walk.amountWalks = amountWalks;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getWalkid() {
        return walkid;
    }

    public void setWalkid(String walkid) {
        this.walkid = walkid;
    }

    public String getDocumentid() {
        return documentid;
    }

    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.titleSet = true;
        this.checkIfReadyToSend();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location)
    {
  //      placeSet = true;
        this.location = location;
  //      this.checkIfReadyToSend();
    }

    public String getLocationtype() {
        return locationtype;
    }

    public void setLocationtype(String locationtype) {
        this.locationtype = locationtype;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    LawoofApiRequest request = new LawoofApiRequest();


    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    public  void setTime(String time) {
        this.time = time;
        this.timeSet = true;
        this.checkIfReadyToSend();
    }

    public  void setDate(String date) {
        this.date = date;
        this.dateSet = true;
        this.checkIfReadyToSend();
    }

    public  void setPets(ArrayList<Pet> pets){
        this.selectedPets = pets;
        this.petSet = true;
        this.checkIfReadyToSend();
    }
    private  void checkIfReadyToSend(){
        if(placeSet && timeSet && dateSet && petSet && titleSet){

            println("Trying");
            //ToDo: Methode zur Serilation schreiben. CouchDB updaten.
//            System.out.println("Walk.class: Walk: " + place.getName() + ", " + date + ", " + time + ", " + selectedPets);
            //User.myWalks.add(this);
//            System.out.println("Walk class: WALK ADDED: " + User.myWalks.get(0).place.getName() + ", " + User.myWalks.get(0).getTime() + ", " + User.myWalks.get(0).getTitle());
            request.addWalks("test123", User.get_id(), title, date, time, "Description is missing",
                    "Difficulty is missing", place.getLatLng().toString(), placeToJSON(), "Location type is missing", "",
                    new  JsonHttpResponseHandler() {
                        @Override
                public void onSuccess(int statusCode , Header[] headers, JSONObject response) {
                    println("Added walk:" + response.toString());
                    //updateUI()
                    Util.Companion.updateWalks();
                }
            });
        }
    }
    public String toString(){
        return "Walk:{" + walkid + ", " + title + ", " + description + ", " + documentid + "}" ;
    }

    public boolean addDetail(String key, String value){
        switch (key){
            case "id":
                documentid = value;
                return true;
            case "walkid":
                walkid = value;
                return true;
            case "amount_walks":
                Walk.amountWalks = Integer.parseInt(value);
                return true;
            case "title":
                title = value;
                return true;
            case "date":
                date = value;
                return true;
            case "time":
                time = value;
                return true;
            case "description":
                description = value;
                return true;
            case "difficulty":
                difficulty = value;
                return true;
            case "location":
                location = value;
                return true;
            case "locationtype":
                locationtype = value;
                return true;
        }
        return false;
    }

    //Add place name
    public boolean addDetail2(String key, String value){
        switch (key){
            case "_id":
                documentid = value;
                return true;
            case "email":
                walkid = value;
                return true;
            case "amount_walks":
                Walk.amountWalks = Integer.parseInt(value);
                return true;
            case "title":
                title = value;
                return true;
            case "date":
                date = value;
                return true;
            case "time":
                time = value;
                return true;
            case "description":
                description = value;
                return true;
            case "difficulty":
                difficulty = value;
                return true;
            case "location":
                location = value;
                return true;
            case "locationname":
 //               locationName = value;
                place = gson.fromJson(locationName, Place.class);
                return true;
            case "locationtype":
                locationtype = value;
                return true;
            case "participants":
                getParticipants().add(value);
                return true;
        }
        return false;
    }

    public boolean hasNoDocumentId(){
        return documentid == "null";
    }

    public void addParticipant(String value) {
        this.participants.add(value);
    }

    public String placeToJSON() {

        return new GsonBuilder().create().toJson(place, Place.class);
    }

    public void JSONToPlace() {
//        return new GsonBuilder().create().fromJson(place, Place.class);

        Gson gson = new Gson();
        place = gson.fromJson(locationName, Place.class);
//        return place;
//
    }

}
