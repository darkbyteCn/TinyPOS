package com.tinyappsdev.tinypos.ui;


import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.service.MessageService;
import com.tinyappsdev.tinypos.ui.OrderFragments.DeliveryFragment;
import com.tinyappsdev.tinypos.ui.OrderFragments.DineInFragment;
import com.tinyappsdev.tinypos.ui.OrderFragments.FoodDetailFragment;
import com.tinyappsdev.tinypos.ui.OrderFragments.ToGoFragment;

public class OrderMainActivity extends SyncableActivity implements OnFragmentIntComListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private final static int URI_OrderMenuFragment_FoodItemId = 1;
    private final static UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        MATCHER.addURI("com.tinyappsdev.tinypos.ui.OrderFragments", "OrderMenuFragment/#", URI_OrderMenuFragment_FoodItemId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.dinein);
        tabLayout.getTabAt(1).setIcon(R.drawable.togo);
        tabLayout.getTabAt(2).setIcon(R.drawable.delivery);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Object onFragmentIntCom(Uri uri, Object data) {
        switch(MATCHER.match(uri)) {
            case URI_OrderMenuFragment_FoodItemId: {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                //fragmentTransaction.add(new FoodDetailFragment(), "FoodDetailFragment");
                fragmentTransaction.commit();
                break;
            }

            default: {

            }
        }

        return null;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return DineInFragment.newInstance();
            else if(position == 1)
                return ToGoFragment.newInstance();
            else if(position == 2)
                return DeliveryFragment.newInstance();

            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }


        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.dine_in);
                case 1:
                    return getString(R.string.to_go);
                case 2:
                    return getString(R.string.delivery);
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
