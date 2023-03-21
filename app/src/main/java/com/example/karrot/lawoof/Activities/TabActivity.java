package com.example.karrot.lawoof.Activities;

import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.karrot.lawoof.R;
import com.example.karrot.lawoof.Util.Help.TabsAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by karrot on 02/03/2017.
 */

//TODO: de-deprecate this
@SuppressWarnings("deprecation")
public class TabActivity extends AppCompatActivity implements ActionBar.TabListener {

    private ViewPager tabsViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_tab);

        tabsViewPager = findViewById(R.id.container);
        TabsAdapter mTabsAdapter = new TabsAdapter(getSupportFragmentManager());
        tabsViewPager.setAdapter(mTabsAdapter);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if (getSupportActionBar() != null) getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //Creates the swipeable tabs for the action bar
        ActionBar.Tab[] tab = new ActionBar.Tab[2];

        for (int i = 0; i < 2; i++) {
            tab[i] = getSupportActionBar().newTab().setTabListener(this);
            TextView ttn = new TextView(this);
            if (i == 0) {
                ttn.setText(Html.fromHtml("<b>My Walks</b><br><small></small>"));
            } else {
                ttn.setText(Html.fromHtml("<b>All Walks</b><br><small></small>"));
            }
//            ttn.setTextColor(Color.WHITE);
            ttn.setGravity(Gravity.CENTER);
            ttn.setHeight(200);
            tab[i].setCustomView(ttn);

            getSupportActionBar().addTab(tab[i]);
        }

        getSupportActionBar().setSelectedNavigationItem(1);

        // Swiping effect for v7 compat library
        tabsViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }


    @Override
    //Inflate the menu: adds action bar items
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed(){}

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        tabsViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }
}
