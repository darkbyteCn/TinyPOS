package com.tinyappsdev.tinypos.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.rest.ApiCallClient;
import com.tinyappsdev.tinypos.ui.BaseUI.KitchenActivityInterface;
import com.tinyappsdev.tinypos.ui.KitchenFragment.PendingFoodFragment;
import com.tinyappsdev.tinypos.ui.KitchenFragment.PendingOrderFragment;

import java.util.Map;

public class KitchenActivity extends SyncableActivity implements
        KitchenActivityInterface {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        //Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void goBack(View view) {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void fulfillFood(Map<Long, Map<Integer, Integer>> items) {
        ApiCallClient.getUiInstance().makeCall(
                "/Ticket/fulfill",
                items, Map.class,
                new ApiCallClient.OnResultListener<Map>() {
                    @Override
                    public void onResult(ApiCallClient.Result<Map> result) {

                    }
                }
        );
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return PendingFoodFragment.newInstance();
            else if (position == 1)
                return PendingOrderFragment.newInstance();

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }


        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Pending Food";
                case 1:
                    return "Pending Orders";
            }
            return null;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

    }

}
