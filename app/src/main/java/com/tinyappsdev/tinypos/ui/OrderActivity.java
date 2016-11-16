package com.tinyappsdev.tinypos.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.Customer;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketFood;
import com.tinyappsdev.tinypos.data.TicketFoodAttr;
import com.tinyappsdev.tinypos.data.TicketPayment;
import com.tinyappsdev.tinypos.rest.ApiCall;
import com.tinyappsdev.tinypos.rest.ApiCallClient;
import com.tinyappsdev.tinypos.ui.BaseUI.OrderActivityInterface;
import com.tinyappsdev.tinypos.ui.OrderFragment.FoodDetailFragment;
import com.tinyappsdev.tinypos.ui.OrderFragment.OrderInfoFragment;
import com.tinyappsdev.tinypos.ui.OrderFragment.OrderMenuFragment;
import com.tinyappsdev.tinypos.ui.OrderFragment.OrderTicketFragment;
import com.tinyappsdev.tinypos.ui.OrderFragment.PaymentFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends SyncableActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OrderActivityInterface {

    final static int PICK_CUSTOMER = 1;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ApiCall mApiCall = ApiCall.getInstance();

    private Ticket mTicket;
    private Map<Long, Integer> mFoodMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
            Bundle bundle = getIntent().getExtras();
            mTicket = new Ticket();
            mTicket.setFoodItems(new ArrayList());
            mTicket.setPayments(new ArrayList());
            mTicket.setTableId(bundle.getLong("tableId"));
            mTicket.setTableName(bundle.getString("tableName"));
            mTicket.setId(bundle.getLong("ticketId"));
            mTicket.setNumGuest(1);
            mTicket.setDbRev(-1);
        }
        prepareTicket();

        if(mTicket.getId() != 0 && mTicket.getDbRev() == -1)
            getSupportLoaderManager().initLoader(0, null, this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager)findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if(mTicket.getId() != 0) mViewPager.setCurrentItem(2);
    }

    public void goBack(View view) {
        NavUtils.navigateUpFromSameTask(this);
    }

    protected void prepareTicket() {
        if(mTicket != null) {
            List<TicketFood> foodItems = mTicket.getFoodItems();
            for (TicketFood foodItem : foodItems) {
                long id = foodItem.getId();
                int qty = foodItem.getQuantity();
                if(qty <= 0) continue;
                Integer curQty = mFoodMap.get(id);
                mFoodMap.put(id, curQty == null ? qty : curQty + qty);
            }
        }
    }

    public void saveOrder(View view) {
        try {
            if(mTicket == null) return;

            mApiCall.callApiAsync(
                    mTicket.getId() != 0 ? "/Ticket/updateDoc" : "/Ticket/newDoc",
                    (new ObjectMapper()).writeValueAsString(mTicket),
                    new ApiCall.ApiCallbacks() {
                        @Override
                        public void onApiResponse(String error, String json) {
                            Log.i("PKT", String.format(">>>>>>> %s, %s", error, json));
                        }
                    }
            );
            finish();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
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

    public void openWnd(String name, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_content, fragment);
        fragmentTransaction.addToBackStack(name);
        fragmentTransaction.commit();
    }

    public void closeWnd(String name) {
        getSupportFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void openFoodDetailWnd(long foodId, int index) {
        openWnd(
                FoodDetailFragment.class.getSimpleName(),
                FoodDetailFragment.newInstance(foodId, index)
        );
    }

    @Override
    public void closeFoodDetailWnd() {
        closeWnd(FoodDetailFragment.class.getSimpleName());
    }

    public void recalculateTotal() {
        double total = 0, due = 0;
        List<TicketFood> ticketFoodList = mTicket.getFoodItems();
        if(ticketFoodList != null) {
            for(TicketFood ticketFood : ticketFoodList)
                total += ticketFood.getExPrice();
        }

        due = total;
        List<TicketPayment> ticketPaymentList = mTicket.getPayments();
        if(ticketPaymentList != null) {
            for(TicketPayment ticketPayment : ticketPaymentList)
                due -= ticketPayment.getAmount();
        }

        mTicket.setTotal(total);
        mTicket.setBalance(due);
    }

    public void syncTicket() {
        double total = 0, due = 0;
        int fulfilled = 0, numFood = 0;

        List<TicketFood> ticketFoodList = mTicket.getFoodItems();
        if(ticketFoodList != null) {
            for(TicketFood ticketFood : ticketFoodList) {
                total += ticketFood.getExPrice();
                ticketFood.setFulfilled(
                        Math.max(Math.min(ticketFood.getFulfilled(), ticketFood.getQuantity()), 0)
                );
                fulfilled += ticketFood.getFulfilled();
                numFood += Math.max(ticketFood.getQuantity(), 0);
            }
        }

        due = total;
        List<TicketPayment> ticketPaymentList = mTicket.getPayments();
        if(ticketPaymentList != null) {
            for(TicketPayment ticketPayment : ticketPaymentList)
                due -= ticketPayment.getAmount();
        }

        mTicket.setNumFoodFullfilled(fulfilled);
        mTicket.setNumFood(numFood);
        mTicket.setTotal(total);
        mTicket.setBalance(due);
    }

    @Override
    public void addFood(TicketFood ticketFood) {
        List<TicketFood> TicketFoodList = mTicket.getFoodItems();
        boolean shouldAdd = true;
        if(TicketFoodList.size() > 0) {
            TicketFood lastTicketFood = TicketFoodList.get(TicketFoodList.size() - 1);
            if(lastTicketFood.getQuantity() > 0 && ticketFood.getQuantity() > 0
                    && lastTicketFood.getId() == ticketFood.getId()
                    && lastTicketFood.getPrice() == ticketFood.getPrice()
                    && lastTicketFood.getAttr().size() == 0
                    && ticketFood.getAttr().size() == 0) {
                lastTicketFood.setQuantity(lastTicketFood.getQuantity() + ticketFood.getQuantity());
                lastTicketFood.setExPrice(lastTicketFood.getPrice() * lastTicketFood.getQuantity());
                shouldAdd = false;
            }
        }

        if(shouldAdd) {
            mTicket.setCurItemId(mTicket.getCurItemId() + 1);
            ticketFood.setItemId(mTicket.getCurItemId());
            TicketFoodList.add(ticketFood);
        }

        if(ticketFood.getQuantity() > 0) {
            if (mFoodMap.containsKey(ticketFood.getId()))
                mFoodMap.put(ticketFood.getId(),
                        mFoodMap.get(ticketFood.getId()) + ticketFood.getQuantity());
            else
                mFoodMap.put(ticketFood.getId(), ticketFood.getQuantity());
        }

        syncTicket();
        sendMessage(R.id.orderActivityOnTicketFoodChange);
    }

    @Override
    public void changeFood(int index, List<TicketFoodAttr> ticketFoodAttr, int quantity, double price) {
        TicketFood ticketFood = mTicket.getFoodItems().get(index);

        int qtyDiff = Math.max(quantity, 0) - Math.max(ticketFood.getQuantity(), 0);
        if (mFoodMap.containsKey(ticketFood.getId()))
            mFoodMap.put(ticketFood.getId(), mFoodMap.get(ticketFood.getId()) + qtyDiff);
        else
            mFoodMap.put(ticketFood.getId(), qtyDiff);

        ticketFood.setQuantity(quantity);
        ticketFood.setPrice(price);
        ticketFood.setExPrice(quantity * price);

        syncTicket();
        sendMessage(R.id.orderActivityOnTicketFoodChange);
    }

    @Override
    public void removeFood(int index) {
        TicketFood ticketFood = mTicket.getFoodItems().remove(index);

        if(ticketFood.getQuantity() > 0 && mFoodMap.containsKey(ticketFood.getId())) {
            mFoodMap.put(
                    ticketFood.getId(),
                    mFoodMap.get(ticketFood.getId()) - ticketFood.getQuantity()
            );
        }

        syncTicket();
        sendMessage(R.id.orderActivityOnTicketFoodChange);
    }

    @Override
    public void clearAllFood() {
        mTicket.getFoodItems().clear();
        mFoodMap.clear();

        syncTicket();
        sendMessage(R.id.orderActivityOnTicketFoodChange);
    }

    @Override
    public void setNumGuest(int number) {
        mTicket.setNumGuest(number);
        sendMessage(R.id.orderActivityOnTicketInfoChange);
    }

    @Override
    public void openCustomerPicker() {
        Intent intent = new Intent(this, CustomerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("customerId", mTicket.getCustomer() == null ? 0 : mTicket.getCustomer().getId());
        bundle.putBoolean("waitForSelection", true);
        intent.putExtras(bundle);
        startActivityForResult(intent, PICK_CUSTOMER);
    }

    @Override
    public void setNotes(String notes) {
        mTicket.setNotes(notes);
        sendMessage(R.id.orderActivityOnTicketInfoChange);
    }

    @Override
    public void setDineTable(long id, String name) {
        mTicket.setTableId(id);
        mTicket.setTableName(name);
        sendMessage(R.id.orderActivityOnTicketInfoChange);
    }

    @Override
    public void deleteTicket() {
        Log.i("PKT", ">>>>" + mTicket.getId());
        ApiCallClient.getUiInstance().makeCall(
                "/Ticket/deleteDoc",
                mTicket,
                Map.class,
                new ApiCallClient.OnResultListener<Map>() {
                    @Override
                    public void onResult(ApiCallClient.Result<Map> result) {
                        if(result.error == null) {
                            finish();
                        }

                    }
                }
        );
    }

    @Override
    public void openPaymentWnd() {
        openWnd(
                PaymentFragment.class.getSimpleName(),
                PaymentFragment.newInstance()
        );
    }

    @Override
    public void closePaymentWnd() {
        closeWnd(PaymentFragment.class.getSimpleName());
    }

    @Override
    public void setPayment(int index, int type, double amount) {
        TicketPayment ticketPayment;
        if(index >= 0) {
            ticketPayment = mTicket.getPayments().get(index);
            if (ticketPayment.getId() > 0) return;
        } else {
            ticketPayment = new TicketPayment();
            mTicket.getPayments().add(ticketPayment);
        }

        ticketPayment.setType(type);
        ticketPayment.setAmount(amount);
        syncTicket();
        sendMessage(R.id.orderActivityOnTicketInfoChange);
    }

    @Override
    public void removePayment(int index) {
        TicketPayment ticketPayment = mTicket.getPayments().get(index);
        if(ticketPayment.getId() > 0) return;

        mTicket.getPayments().remove(index);
        syncTicket();
        sendMessage(R.id.orderActivityOnTicketInfoChange);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_CUSTOMER) {
            if(resultCode == RESULT_OK) {
                String customerJs = data.getExtras().getString("customerJs");
                Customer customer = customerJs == null
                        ? null : ModelHelper.fromJson(customerJs, Customer.class);

                mTicket.setCustomer(customer);
                sendMessage(R.id.orderActivityOnTicketInfoChange);

                return;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this.getApplicationContext(),
                ContentProviderEx.BuildUri(Ticket.Schema.TABLE_NAME, mTicket.getId() + ""),
                null, null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("PKT", "onLoadFinished ->>>Ticket");
        if(!data.moveToFirst()) {
            Toast.makeText(this,
                    String.format("Can't find Ticket #%d", mTicket.getId()),
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        mTicket = ModelHelper.TicketFromCursor(data);
        data.close();
        sendMessage(R.id.orderActivityOnTicketChange);
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return OrderInfoFragment.newInstance();
            else if(position == 1)
                return OrderMenuFragment.newInstance();
            else if(position == 2)
                return OrderTicketFragment.newInstance();
            return null;
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Info";
                case 1:
                    return "Menu";
                case 2:
                    return "Order";
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

}
