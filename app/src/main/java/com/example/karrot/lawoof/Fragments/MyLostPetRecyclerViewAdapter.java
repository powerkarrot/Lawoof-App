package com.example.karrot.lawoof.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.karrot.lawoof.Activities.MainActivity;
import com.example.karrot.lawoof.Content.Pet;
import com.example.karrot.lawoof.Fragments.LostPetFragment.OnListFragmentInteractionListener;
import com.example.karrot.lawoof.R;
import com.example.karrot.lawoof.Util.Help.LostPets;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Pet} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyLostPetRecyclerViewAdapter extends RecyclerView.Adapter<MyLostPetRecyclerViewAdapter.ViewHolder> {

    private final List<LostPets> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyLostPetRecyclerViewAdapter(List<LostPets> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_lostpet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).pet.name); //pet
        holder.mContentView.setText(MainActivity.lostPets.get(position).owner); //pets owner

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mIdView;
        private final TextView mContentView;
        private LostPets mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.id);
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
