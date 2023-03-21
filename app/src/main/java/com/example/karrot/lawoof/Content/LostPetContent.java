package com.example.karrot.lawoof.Content;

import com.example.karrot.lawoof.Activities.MainActivity;
import com.example.karrot.lawoof.Util.Help.LostPets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing content for PetList- and Detail UI
 */
public class LostPetContent {

    /**
     * An array of Pet items.
     */
    public static  List<LostPets> PETSLIST = MainActivity.getLostPets();

    public static  List<String> PETS = new ArrayList<String>();

    /**
     * A map of Pet items, by ID.
     */
    public static Map<String, Pet> PET_MAP = new HashMap<String, Pet>();

    private static int COUNT = PETSLIST.size();

    private static void addItem(LostPets pet) {
            PET_MAP.put(pet.pet.id, pet.pet);
            PETS.add(pet.pet.name);
    }

    /**
     * Updates Map when new pet is lost.
     */
    public static void updatePets(){
        LostPetContent.PETSLIST = MainActivity.getLostPets();
            System.out.println("PETCONTENT.class: " + PETSLIST);

            LostPetContent.PET_MAP.clear();
            LostPetContent.PETS.clear();

            for(LostPets lp: PETSLIST){
                addItem(lp);
        }
        LostPetContent.COUNT = PETSLIST.size();
    }
}
