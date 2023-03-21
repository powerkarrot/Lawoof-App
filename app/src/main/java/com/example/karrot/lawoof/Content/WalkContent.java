package com.example.karrot.lawoof.Content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * /**
 * Helper class for providing content for WalkList- and Detail UI
 */

public class WalkContent {

    /**
     * An array of Walk items.
     */
    public static  List<Walk> WALKLIST = User.myWalks;        //temporary fix
    public static  List<String> WALKS = new ArrayList<String>();

    /**
     * A map of Walk items, by ID.
     */
    public static final Map<String, Walk> WALK_MAP = new HashMap<String, Walk>();

    private static int COUNT = WALKLIST.size();

    private static void addItem(Walk walk) {
        WALK_MAP.put(walk.getWalkid(), walk);
//        WALKS.add(walk.place.toString());
        WALKS.add(walk.getTitle());

    }

    /**
     * Updates Map when new user registers a new pet to Database
     */
    public static void updateWalks(){
        WALKLIST = User.myWalks;
        System.out.println("WALKCONTENT.class: " + WALKS);
        COUNT = WALKLIST.size();

        WALK_MAP.clear();
        WALKS.clear();

        for(Walk w: WALKLIST){
            addItem(w);
        }
    }
}
