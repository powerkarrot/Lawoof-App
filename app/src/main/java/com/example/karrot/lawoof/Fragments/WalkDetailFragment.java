package com.example.karrot.lawoof.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.karrot.lawoof.R;
import com.example.karrot.lawoof.Activities.TabActivity;
import com.example.karrot.lawoof.Content.Pet;
import com.example.karrot.lawoof.Content.Walk;
import com.example.karrot.lawoof.Activities.WalkDetailActivity;
import com.example.karrot.lawoof.Content.WalkContent;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.fragment.app.Fragment;

/**
 * A fragment representing a single Walk detail screen.
 * This fragment is either contained in a {@link TabActivity}
 * in two-pane mode (on tablets) or a {@link WalkDetailActivity}
 * on handsets.
 */
public class WalkDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */


    EditText walk_place;
    EditText walk_time;
    EditText walk_date;
    EditText walk_pets;
    EditText walk_participants;
    RatingBar walk_difficulty;

    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Walk mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WalkDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = WalkContent.WALK_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getTitle());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.walk_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
                walk_place = rootView.findViewById(R.id.walk_place);
                walk_date = rootView.findViewById(R.id.walk_date);
                walk_time = rootView.findViewById(R.id.walk_time);
                walk_participants = rootView.findViewById(R.id.walk_participants);
                walk_pets = rootView.findViewById(R.id.walk_pets);

               walk_difficulty = rootView.findViewById(R.id.walk_difficulty);
                // pet_description = (TextView) rootView.findViewById(R.id.get_pet_description);
            String participants = "";
            for(String p : mItem.getParticipants()) {
                participants.concat(p + "\n");
            }
            String pets = "";
            for(Pet p : mItem.getSelectedPets()) {
                participants.concat(p.name + "\n");
            }

            walk_place.setText(mItem.place.getName().toString());
            walk_date.setText(mItem.getDate());
            walk_time.setText(mItem.getTime());
            walk_participants.setText(participants);
            walk_pets.setText(pets);

        }
        return rootView;
    }

}
