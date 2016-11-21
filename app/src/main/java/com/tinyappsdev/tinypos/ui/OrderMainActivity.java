package com.tinyappsdev.tinypos.ui;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;

import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.ui.BaseUI.OrderMainActivityInterface;
import com.tinyappsdev.tinypos.ui.OrderMainFragment.DeliveryFragment;
import com.tinyappsdev.tinypos.ui.OrderMainFragment.DineInFragment;
import com.tinyappsdev.tinypos.ui.OrderMainFragment.ToGoFragment;

public class OrderMainActivity extends SyncableActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OrderMainActivityInterface {

    private TabLayout mTabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private int[] mTotalCount = new int[3];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout)findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.dinein);
        mTabLayout.getTabAt(1).setIcon(R.drawable.togo);
        mTabLayout.getTabAt(2).setIcon(R.drawable.delivery);

        getLoaderManager().initLoader(0, null, this);

        if(savedInstanceState == null) {
                mViewPager.setCurrentItem(Math.abs(getIntent().getIntExtra("ticketType", 0)));
        }
    }

    public void goBack(View view) {
        finish();
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
    }

    String getTitle(int ticketType) {
        String format = getString(R.string.title_format_order_main_fragment);
        switch (ticketType) {
            case 0:
                return mTotalCount[0] > 0
                        ? String.format(format, getString(R.string.dine_in), mTotalCount[0])
                        : getString(R.string.dine_in);
            case 1:
                return mTotalCount[1] > 0
                        ? String.format(format, getString(R.string.to_go), mTotalCount[1])
                        : getString(R.string.to_go);
            case 2:
                return mTotalCount[2] > 0
                        ? String.format(format, getString(R.string.delivery), mTotalCount[2])
                        : getString(R.string.delivery);
        }
        return null;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
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

    }

}
