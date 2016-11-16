package com.tinyappsdev.tinypos.ui;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;

import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.ui.OrderFragment.DeliveryFragment;
import com.tinyappsdev.tinypos.ui.OrderFragment.DineInFragment;
import com.tinyappsdev.tinypos.ui.OrderFragment.ToGoFragment;

public class OrderMainActivity extends SyncableActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OrderMainActivityInterface {

    private TabLayout mTabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private int[] mTotalCount = new int[3];
    private DataSetObservable mFragmentChangeObservable;

    private final static int URI_OrderMenuFragment_FoodItemId = 1;
    private final static UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        MATCHER.addURI("com.tinyappsdev.tinypos.ui.OrderFragments", "OrderMenuFragment/#", URI_OrderMenuFragment_FoodItemId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_main);

        mFragmentChangeObservable = new DataSetObservable();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(mSectionsPagerAdapter);

        mTabLayout = (TabLayout)findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.dinein);
        mTabLayout.getTabAt(1).setIcon(R.drawable.togo);
        mTabLayout.getTabAt(2).setIcon(R.drawable.delivery);

        getLoaderManager().initLoader(0, null, this);
    }

    public void goBack(View view) {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this.getApplicationContext(),
                ContentProviderEx.BuildUri(Ticket.Schema.TABLE_NAME),
                new String[] {
                        String.format("min(0, %s) as ticketType", Ticket.Schema.COL_TABLEID),
                        "count(*) as totalCount"
                },
                String.format(
                        "(%s&%s)=0 group by ticketType",
                        Ticket.Schema.COL_STATE, Ticket.STATE_COMPLETED
                ),
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i("PKT", ">>>>>OrderMainLoader ->onLoadFinished" + cursor.getCount());

        for(int i = 0; i < mTotalCount.length; i++) mTotalCount[i] = 0;
        while(cursor.moveToNext()) {
            int ticketType = Math.abs(cursor.getInt(0));
            if(ticketType >= mTotalCount.length) continue;
            mTotalCount[ticketType] = cursor.getInt(1);
        }

        for(int i = 0; i < mTotalCount.length; i++)
            mTabLayout.getTabAt(i).setText(getTitle(i));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i("PKT", ">>>>>OrderMainLoader ->onLoaderReset");
    }

    String getTitle(int ticketType) {
        switch (ticketType) {
            case 0:
                return mTotalCount[0] > 0
                        ? String.format("%s (%d)", getString(R.string.dine_in), mTotalCount[0])
                        : getString(R.string.dine_in);
            case 1:
                return mTotalCount[1] > 0
                        ? String.format("%s (%d)", getString(R.string.to_go), mTotalCount[1])
                        : getString(R.string.to_go);
            case 2:
                return mTotalCount[2] > 0
                        ? String.format("%s (%d)", getString(R.string.delivery), mTotalCount[2])
                        : getString(R.string.delivery);
        }
        return null;
    }

    @Override
    public int getCurrentFragmentId() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public void registerObserverForFragmentChange(DataSetObserver observer) {
        mFragmentChangeObservable.registerObserver(observer);
    }

    @Override
    public void unregisterObserverForFragmentChange(DataSetObserver observer) {
        mFragmentChangeObservable.unregisterObserver(observer);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return DineInFragment.newInstance(0);
            else if(position == 1)
                return ToGoFragment.newInstance(1);
            else if(position == 2)
                return DeliveryFragment.newInstance(2);

            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }


        public CharSequence getPageTitle(int position) {
            return getTitle(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mFragmentChangeObservable.notifyChanged();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

}
