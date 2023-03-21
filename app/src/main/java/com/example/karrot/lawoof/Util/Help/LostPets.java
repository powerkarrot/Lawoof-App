package com.example.karrot.lawoof.Util.Help;

import com.example.karrot.lawoof.Content.Pet;

import java.util.ArrayList;

/**
 * Created by karrot on 18/03/2017.
 * Will at some point handle lost pets from other people
 */

public class LostPets {

    public String owner;
    public ArrayList<Pet> lostpet = new ArrayList<>();
    public Pet pet;

    /*
    public LostPets(String owner, Pet pet) {
        this.owner = owner;
        this.lostpet.add(pet);
    }
    */

    public LostPets(String owner, Pet pet) {
        this.owner = owner;
        this.pet = pet;
    }

    public String toString() {
        return pet.name + " " + owner;
    }

}
