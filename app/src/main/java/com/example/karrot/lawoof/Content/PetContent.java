package com.example.karrot.lawoof.Content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing content for PetList- and Detail UI
 */
public class PetContent {

    /**
     * An array of Pet items.
     */
    public static  List<Pet> PETSLIST = User.getPets();
    public static  List<String> PETS = new ArrayList<String>();

    /**
     * A map of Pet items, by ID.
     */
    public static Map<String, Pet> PET_MAP = new HashMap<>();

    private static int COUNT = PETSLIST.size();

    private static void addItem(Pet pet) {
        PET_MAP.put(pet.id, pet);
        PETS.add(pet.name);
    }

    /**
     * Updates Map when new user registers a new pet to Database
     */
    public static void updatePets(){
        PetContent.PETSLIST = User.getPets();
        System.out.println("PETCONTENT.class: " + PETSLIST);
        PetContent.COUNT = PETSLIST.size();

        PetContent.PET_MAP.clear();
        PetContent.PETS.clear();

        for(Pet p: PETSLIST){
            addItem(p);
        }
    }
}
