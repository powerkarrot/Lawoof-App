package com.example.karrot.lawoof.Content;

import com.example.karrot.lawoof.Util.Help.LatLngTime;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by karrot on 01/12/2016.
 */
public class Pet implements Serializable {
    // Hi Caro xD
    // Hi Tahbi!
    private static int _id = 1;

    public String email, name, age, species_class, species, breed, color, sex, castrated, friendliness, description, id, pet_id;
    public LatLng lastKnownLoc = new LatLng(0.0, 0.0);
    public ArrayList<LatLngTime> places = new ArrayList<>();
    public Double latitude = 1.0, longitude = 1.0;
    public Double[] positions;

    public Pet(){
        this.id = "" + _id;
        _id++;
    }

    /**
     * Adds a Petdetail provided by the JSON-Reader class
     * @param key JSON-LOSTPETS_KEY
     * @param value JSON-VALUE
     */
    public void addDetail(String key, String value){
        switch (key){
            case "pet_id":
                this.pet_id = value;
                break;
            case "pet_name":
                this.name = value;
                break;
            case "pet_age":
                this.age = value;
                break;
            case "pet_class":
                this.species_class = value;
                break;
            case "pet_species":
                this.species = value;
                break;
            case "pet_breed":
                this.breed = value;
                break;
            case "pet_color":
                this.color = value;
                break;
            case "pet_sex":
                this.sex = value;
                break;
            case "pet_castrated":
                this.castrated = value;
                break;
            case "pet_friendliness":
                this.friendliness = value;
                break;
            case "pet_description":
                this.description = value;
                break;
            case "pet_longitude":
               this.longitude = new Double(Double.parseDouble(value));
                break;
            case "pet_latitude":
                this.latitude = new Double(Double.parseDouble(value));
                break;
        }
    }

    /**
     * Adds a Petdetail provided by the JSON-Reader class
     * @param key JSON-LOSTPETS_KEY
     * @param value JSON-VALUE
     */
    public void addDetail2(String key, String value){
        switch (key){
            case "_id":
                this.pet_id = value;
                break;
            case "email":
                this.email = value;
                break;
            case "name":
                this.name = value;
                break;
            case "age":
                this.age = value;
                break;
            case "class":
                this.species_class = value;
                break;
            case "species":
                this.species = value;
                break;
            case "breed":
                this.breed = value;
                break;
            case "color":
                this.color = value;
                break;
            case "pet_sex":
                this.sex = value;
                break;
            case "castrated":
                this.castrated = value;
                break;
            case "friendliness":
                this.friendliness = value;
                break;
            case "description":
                this.description = value;
                break;
            case "longitude":
                this.longitude = Double.valueOf(value);
                break;
            case "latitude":
                this.latitude = Double.valueOf(value);
                break;
            case "positions":
                break;
        }
    }

    /**
     * Compares pets
     * @param pet
     * The pet to compare
     * @return
     * Returns whether it is the same pet
     */
    public boolean comparePet(Pet pet){

        boolean[] bo = new boolean[4];

        if(pet.name.equals(this.name)) {
            bo[0] = true;
        } if(pet.sex.equals(this.sex)){
            bo[1] = true;
        } if(pet.species.equals(this.species)){
            bo[2] = true;
        } if(pet.description.equals(this.description)){
            bo[3] = true;
        }

        return (bo[0] && bo[1]&& bo[2]&& bo[3]);
    }

    public String toString(){
        return this.id + ". " + this.name;
    }

    public void configurePet() {

        //Only call this at beguinning of app
        //TODO: and only if pet has no position array. change position array to LatLngTime array and put in pet.places
        if(places.size() < 1) {
            lastKnownLoc = new LatLng(latitude, longitude);

            //TODO: save as latlngtime in database
            LatLngTime time = new LatLngTime(lastKnownLoc);
            places.add(time);
        }

    }
}
