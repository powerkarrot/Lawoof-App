package com.example.karrot.lawoof.Util.JSON;

import android.util.JsonReader;
import android.util.JsonToken;

import com.example.karrot.lawoof.Content.Pet;
import com.example.karrot.lawoof.Content.User;
import com.example.karrot.lawoof.Content.Walk;

import java.io.IOException;
import java.util.ArrayList;

/*
 * Created by karrot on 01/12/2016.
 */

public class JSONReader {
    /**
     * JSON Parser that checks if login was successful
     * @param reader
     * @return
     * @throws IOException
     */
    public boolean readStatus(JsonReader reader) throws IOException{
        String description;

        reader.beginObject();
        while (reader.hasNext()){
            String key = reader.nextName();
            if(key.equals("type")){
                description = reader.nextString();
                System.out.println(key + ": " + description);
                if(description.equals("Success")){
                    return true;
                }
            }
        }
        return false;
    }

    public void readAll(JsonReader reader) throws IOException{

    }

    /**
     * Parses our Userdata from JSON and saves it in User.class
     * @param reader JsonReader that needs to be parsed
     * @return always false - welcome to Prolog
     * @throws IOException If json parsing fails.
     */
    public boolean readUserdata(JsonReader reader) throws IOException{
        reader.beginObject();
        while (reader.peek() != JsonToken.END_DOCUMENT){
            while (reader.peek() == JsonToken.BEGIN_OBJECT)
                reader.beginObject();
            while (reader.peek() == JsonToken.BEGIN_ARRAY)
                reader.beginArray();
            String key = reader.nextName();
            String value = tryReadNextValue(reader);
            System.out.println("LOSTPETS_KEY/VALUE: " + key + "|" + value);
            if(value != "")
                User.addDetail(key, value);
            while (reader.peek() == JsonToken.END_OBJECT)
                reader.endObject();
            while (reader.peek() == JsonToken.END_ARRAY)
                reader.endArray();
        }
        return false;
    }

    /**
     * Parses Petdata from JSON and saves it in Pet.class
     * @param reader
     * @return
     * @throws IOException
     */
    public boolean readPets(JsonReader reader) throws IOException {
        Pet pet = new Pet();
        ArrayList<Pet> pets = new ArrayList<>();
        int no = 0;
        while (reader.hasNext()) {
            String key = "";
            while (reader.peek() == JsonToken.BEGIN_OBJECT) {
                reader.beginObject();
            }
            while (reader.peek() == JsonToken.BEGIN_ARRAY) {
                reader.beginArray();
            }

            while (reader.peek() == JsonToken.NAME){
                key = reader.nextName();
                if(reader.peek() == JsonToken.STRING){
                    String value = reader.nextString();
                    pet.addDetail(key, value);
                    System.out.println(key + " + " + value);
                }
            }

            while (reader.peek() == JsonToken.END_OBJECT) {
                reader.endObject();
                if(key.equals("pet_latitude") && reader.peek() == JsonToken.END_OBJECT){
                    pets.add(pet);
                    pet = new Pet();
                }
            }
            while (reader.peek() == JsonToken.END_ARRAY) {
                reader.endArray();
            }
            if(reader.peek() == JsonToken.END_DOCUMENT)
                break;
        }
        if(User.getPets().size() < pets.size()){
            System.out.println("old user pets " + User.getPets());
            System.out.println("generated pets " + pets);
            User.setPets(pets);
            System.out.println("new user pets " + User.getPets());
            return true;
        }
        return false;
    }

    /**
     * Makes sure that next value is parsed correctly
     * @param reader JsonReader that needs to be parsed
     * @return empty String if no value is present; else returns the json value
     * @throws IOException If json parsing fails.
     */
    private String tryReadNextValue(JsonReader reader) throws IOException{
        switch(reader.peek()){
            case BEGIN_OBJECT:
                reader.beginObject();
                break;
            case BEGIN_ARRAY:
                reader.beginArray();
                break;
            case STRING:
                return reader.nextString();
        }
        return "";
    }

    /**
    * Parses Walkdata from all Walks from JSON and saves it in Walk.class
     * @param reader
     * @return
     * @throws IOException
     */
    public boolean readAllWalks(JsonReader reader) throws IOException {
        int amountWalks = 0;
        int amountParticipants = 0;
        String documentid = "";
        Walk walk = new Walk();
        while (reader.hasNext()){
            String key = "Key";
            String value = "{Object}";
            while (reader.peek() == JsonToken.BEGIN_OBJECT)
                reader.beginObject();
            while (reader.peek() == JsonToken.BEGIN_ARRAY)
                reader.beginArray();
            if(reader.peek() == JsonToken.NAME){
                key = reader.nextName();

                if(key.equals("participants")){
                    reader.beginArray();
                    for(int i = 0; i < amountParticipants; i++){
                        value = reader.nextString();
                        //System.out.println("Key/Value: " + key + "|" + value);
                        walk.addParticipant(value);
                    }
                    if(walk.hasNoDocumentId()){
                        walk.addDetail("id", documentid);
                    }
                    User.myWalks.add(walk);
                    walk = new Walk();
                    reader.endArray();
                }
                if(reader.peek() == JsonToken.STRING || reader.peek() == JsonToken.NUMBER) {

                    value = reader.nextString();
                    if(key.equals("total_rows")){
                        amountWalks = Integer.parseInt(value);
                    }
                    if(key.equals("amount_participants")){
                        amountParticipants = Integer.parseInt(value);
                    }

                    if(key.equals("_id")){
                        documentid = value;
                    }
                }
                walk.addDetail(key, value);
                //System.out.println("Key/Value: " + key + "|" + value);
            }
            while (reader.peek() == JsonToken.END_OBJECT)
                reader.endObject();
            while (reader.peek() == JsonToken.END_ARRAY)
                reader.endArray();
            if(key.equals("_deleted_conflicts") || reader.peek() == JsonToken.END_DOCUMENT){
                System.out.println("Finished reading walkdata...");
                System.out.println("Walkdata: " + User.myWalks);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if next JSON Token is of Array or Object type
     * @param reader
     * @throws IOException
     */
    private void checkForArrayOrObject(JsonReader reader) throws IOException {
        while (reader.peek() == JsonToken.BEGIN_OBJECT)
            reader.beginObject();
        while (reader.peek() == JsonToken.BEGIN_ARRAY)
            reader.beginArray();
        while (reader.peek() == JsonToken.END_OBJECT)
            reader.endObject();
        while (reader.peek() == JsonToken.END_ARRAY)
            reader.endArray();
    }
}
