package com.tinyappsdev.tinypos.ui;

import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.DineTable;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketFood;
import com.tinyappsdev.tinypos.ui.Helper.OrderShareContext;
import com.tinyappsdev.tinypos.ui.OrderFragments.FoodDetailFragment;
import com.tinyappsdev.tinypos.ui.OrderFragments.OrderMenuFragment;
import com.tinyappsdev.tinypos.ui.OrderFragments.OrderTicketFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends SyncableActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OrderActivityInterface {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private long mTableId;
    private long mTicketId;

    private Ticket mTicket;
    private DataSetObservable mTicketObservable;
    private DataSetObservable mFoodObservable;
    private Map<Long, Integer> mFoodMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        mTableId = bundle.getLong("tableId");
        mTicketId = bundle.getLong("ticketId");
        Log.i("PKT", ">>>>>>>>OrderActivity ->onCreate" + mTicketId);

        mTicketObservable = new DataSetObservable();
        mFoodObservable = new DataSetObservable();
        mFoodMap = new HashMap<Long, Integer>();

        mTicket = null;
        if(savedInstanceState != null) {
            try {
                String json = (String)savedInstanceState.get("ticket");
                mTicket = (new ObjectMapper()).readValue(json, Ticket.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(mTicket == null) {
            if (mTicketId != 0)
                getSupportLoaderManager().initLoader(0, null, this);
            else {
                mTicket = new Ticket();
                mTicket.setFoodItems(new ArrayList<TicketFood>());
                mTicket.setTableId(mTableId);
            }
        }

        if(mTicket != null) prepareTicket();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        if(mTicketId != 0) swipeToTicketFragment(null);
    }

    protected void prepareTicket() {
        if(mTicket != null) {
            List<TicketFood> foodItems = mTicket.getFoodItems();
            for (TicketFood foodItem : foodItems) {
                long id = foodItem.getId();
                int qty = foodItem.getQuantity();
                Integer curQty = mFoodMap.get(id);
                mFoodMap.put(id, curQty == null ? qty : curQty + qty);
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        try {
            outState.putString("ticket", new ObjectMapper().writeValueAsString(mTicket));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void swipeToTicketFragment(View view) {
        mViewPager.setCurrentItem(1);
    }

    public void openFoodDetailWnd(long foodId, int index) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, FoodDetailFragment.newInstance(foodId, index));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this.getApplicationContext(),
                ContentProviderEx.BuildUri(Ticket.Schema.TABLE_NAME, mTicketId + ""),
                null, null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst()) {
            Toast.makeText(this, String.format("Can't find Ticket #%d", mTicketId), Toast.LENGTH_LONG).show();
            return;
        }

        mTicket = ModelHelper.TicketFromCursor(data);
        notifyChangedForFood();
        notifyChangedForTicket();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public Ticket getTicket() {
        return mTicket;
    }

    @Override
    public Map<Long, Integer> getFoodMap() {
        return mFoodMap;
    }

    @Override
    public void registerObserverForFood(DataSetObserver observer) {
        mFoodObservable.registerObserver(observer);
    }

    @Override
    public void unregisterObserverForFood(DataSetObserver observer) {
        mFoodObservable.unregisterObserver(observer);
    }

    @Override
    public void notifyChangedForFood() {
        mFoodObservable.notifyChanged();
    }

    @Override
    public void registerObserverForTicket(DataSetObserver observer) {
        mTicketObservable.registerObserver(observer);
    }

    @Override
    public void unregisterObserverForTicket(DataSetObserver observer) {
        mTicketObservable.unregisterObserver(observer);
    }

    @Override
    public void notifyChangedForTicket() {
        mTicketObservable.notifyChanged();
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return OrderMenuFragment.newInstance();
            else if(position == 1)
                return OrderTicketFragment.newInstance();

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

    }

}
