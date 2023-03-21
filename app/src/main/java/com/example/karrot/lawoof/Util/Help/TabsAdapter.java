package com.example.karrot.lawoof.Util.Help;

import com.example.karrot.lawoof.Fragments.WalkListFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Created by karrot on 02/03/2017.
 *
 * Helper class to handle tabs in TabActivity
 *
 */
public class TabsAdapter extends FragmentPagerAdapter {

    private int TOTAL_TABS = 2;
    String userID = "634";

    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Initializes Fragments on WalkListFragment
     * Differenciates between own walks and all walks (within a certain radius)
     * TODO: implement fully
     */
    @Override
    public Fragment getItem(int index) {
        //or just use a boolean or whatever
        switch (index) {
            case 0:
                WalkListFragment tm = WalkListFragment.newInstance(userID);
                return tm;
            case 1:
                WalkListFragment tm1 = WalkListFragment.newInstance(null);
                return tm1;
        }
        return null;
    }

    @Override
    public int getCount() {
        return TOTAL_TABS;
    }
}
