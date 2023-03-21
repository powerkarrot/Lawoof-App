package com.example.karrot.lawoof.Content;

import java.util.ArrayList;

/**
 * Created by karrot on 01/12/2016.
 */

public class User{

    public static String status = "waiting", statusDescription;
    public static String username, _id, _rev, email, last_name, name, profileStatus = "Yep";
    public static ArrayList<Pet> pets = new ArrayList<Pet>();



    public static ArrayList<Walk> myWalks = new ArrayList<>();
    public static Adress adress = new Adress();

    private static Pet pet;

    /**
     * Adds a Userdetail provided by the JSONReader.class
     * @param key   JSON-LOSTPETS_KEY
     * @param value JSON-VALUE
     * @return true if successful, false if not
     */
    public static boolean addDetail(String key, String value){
        switch (key){

            case "type":
                System.out.println("JJAAAAAAA");
                status = value;
                return true;
            case "_id":
                _id = value.trim();
                System.out.println("UserID: " + _id);
                return true;
            case "_rev":
                _rev = value;
                return true;
            case "street":
                adress.street = value;
                return true;
            case "city":
                adress.city = value;
                return true;
            case "win_rar":
                adress.zip = value;
                return true;
            case "email":
                email = value;
                return true;
            case "last_name":
                last_name = value;
                return true;
            case "name":
                name = value;
                return true;
            case "username":
                username = value;
                return true;
            case "pet_latitude":
                // Special case: Represents the end of a Pet!
                pet.addDetail(key, value);
                addIfUnique(pet);
                //pet = null;
                return true;
            case "pet_name":
                // Special case: Represents the beginning of a Pet!
                pet = new Pet();
            default:
                // If the pet is not null we know we created a pet and are parsing them at this moment.
                if (pet != null)
                    pet.addDetail(key, value);
                return false;
        }
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        User.status = status;
    }

    public static String getStatusDescription() {
        return statusDescription;
    }

    public static void setStatusDescription(String statusDescription) {
        User.statusDescription = statusDescription;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        User.username = username;
    }

    public static String get_id() {
        return _id;
    }

    public static void set_id(String _id) {
        User._id = _id;
    }

    public static String get_rev() {
        return _rev;
    }

    public static void set_rev(String _rev) {
        User._rev = _rev;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static String getLast_name() {
        return last_name;
    }

    public static void setLast_name(String last_name) {
        User.last_name = last_name;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }

    public static void setPets(ArrayList<Pet> pets) {
        User.pets = pets;
        for(int i = 1; i <= User.pets.size(); i++){
            User.pets.get( i- 1).id = "" + i;
        }
    }

    public static ArrayList<Pet> getPets(){
        return pets;
    }

    public static Adress getAdress() {
        return adress;
    }

    public static void setAdress(Adress adress) {
        User.adress = adress;
    }

    public static Pet getPet() {
        return pet;
    }

    public static void setPet(Pet pet) {
        User.pet = pet;
    }

    public static String getProfileStatus(){ return User.profileStatus; }

    public static ArrayList<Walk> getMyWalks() {
        return myWalks;
    }

    public static void setMyWalks(ArrayList<Walk> myWalks) {
        User.myWalks = myWalks;
    }

    /**
     * Returns our Userdata as a string
     * @return The formatted userdata.
     */
    public static String getUserdata(){
        return new String(name + " " +  last_name + "\n" + email + "\n" + adress);
    }

    public static void addIfUnique(Pet pet){
        boolean petadd = true;
        for(Pet p : pets){
            System.out.println("PETS: " + p + " | " + pet);
            if(p.comparePet(pet)) {
                petadd = false;
                break;
            }
        }
        if(petadd)
            pets.add(pet);
    }

    public static void reset(){
        pets.clear();
    }

    public static class Adress{
        String street, city, zip;

        public String toString(){
            return new String(street + "\n" + zip + ", " + city);
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }
    }
}
