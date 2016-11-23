package com.tinyappsdev.tinypos.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tinyappsdev.tinypos.AppGlobal;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.Customer;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketFood;
import com.tinyappsdev.tinypos.data.TicketFoodAttr;
import com.tinyappsdev.tinypos.data.TicketPayment;
import com.tinyappsdev.tinypos.helper.TinyMap;
import com.tinyappsdev.tinypos.helper.TinyUtils;
import com.tinyappsdev.tinypos.rest.ApiCallClient;
import com.tinyappsdev.tinypos.ui.BaseUI.OrderActivityInterface;
import com.tinyappsdev.tinypos.ui.OrderFragment.FoodDetailFragment;
import com.tinyappsdev.tinypos.ui.OrderFragment.OrderInfoFragment;
import com.tinyappsdev.tinypos.ui.OrderFragment.OrderMenuFragment;
import com.tinyappsdev.tinypos.ui.OrderFragment.OrderTicketFragment;
import com.tinyappsdev.tinypos.ui.OrderFragment.OrderPaymentFragment;

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

    private Ticket mTicket;
    private Map<Long, Integer> mFoodMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mFoodMap = new HashMap<Long, Integer>();
        mTicket = null;
        if(savedInstanceState != null)
            mTicket = ModelHelper.fromJson(savedInstanceState.getString("ticket"), Ticket.class);

        if(mTicket == null) {
            Bundle bundle = getIntent().getExtras();
            mTicket = new Ticket();
            mTicket.setEmployeeId(mSharedPreferences.getInt("employeeCode", 0));
            mTicket.setEmployeeName(mSharedPreferences.getString("employeeName", ""));
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

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager)findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if(mTicket.getId() != 0) mViewPager.setCurrentItem(2);
    }

    public void goBack(View view) {
        finish();
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
        saveOrder(false, false);
    }

    @Override
    public void saveOrder(boolean doPay, boolean doComplete) {
        if(mTicket.getTableId() < 0 && mTicket.getCustomer() == null) {
            TinyUtils.showMsgBox(getApplicationContext(), R.string.customer_required);
            mViewPager.setCurrentItem(0);
            return;
        }

        if(doPay || doComplete) {
            if(mTicket.getBalance() > 0) {
                TinyUtils.showMsgBox(this, R.string.payment_due);
                return;
            }
        }

        String uri = String.format("/Ticket/%sDoc?payMode=%d",
                mTicket.getId() != 0 ? "update" : "new",
                doComplete ? 2 : (doPay ? 1 : 0)
        );
        AppGlobal.getInstance().getUiApiCallClient().makeCall(
                uri,
                mTicket,
                Map.class,
                new ApiCallClient.OnResultListener<Map>() {
                    @Override
                    public void onResult(ApiCallClient.Result<Map> result) {
                        TinyMap map = TinyMap.AsTinyMap(result.data);
                        if(result.error != null || result.data == null)
                            TinyUtils.showMsgBox(getApplicationContext(), R.string.error_occurred);
                        else if(!map.getBoolean("success"))
                            TinyUtils.showMsgBox(getApplicationContext(), R.string.save_order_error);
                        else {
                            if(mTicket.getId() == 0) mTicket.setId(map.getLong("_id"));
                            onSavedOrder(map);
                        }
                    }
                }
        );

    }

    public void openMap(View view) {
        if(mTicket.getCustomer() == null) return;

        Customer customer = mTicket.getCustomer();
        String query = String.format(
                "%s,%s,%s",
                customer.getAddress(),
                customer.getCity(),
                customer.getState()
        );
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    public void checkout() {
        if(mTicket.getId() == 0) return;

        int stateMask = Ticket.STATE_PAID | Ticket.STATE_FULFILLED;
        if((mTicket.getState() & stateMask) != stateMask) {
            TinyUtils.showMsgBox(this, R.string.checkout_requirement);
            return;
        }

        Map map = new HashMap();
        map.put("_id", mTicket.getId());
        AppGlobal.getInstance().getUiApiCallClient().makeCall(
                "/Ticket/checkout",
                map,
                Map.class,
                new ApiCallClient.OnResultListener<Map>() {
                    @Override
                    public void onResult(ApiCallClient.Result<Map> result) {
                        TinyMap map = TinyMap.AsTinyMap(result.data);
                        if(result.error != null || result.data == null)
                            TinyUtils.showMsgBox(getApplicationContext(), R.string.error_occurred);
                        else if(!map.getBoolean("success")) {
                            TinyUtils.showMsgBox(getApplicationContext(), R.string.cant_checkout);
                        } else {
                            finish();
                        }
                    }
                }
        );
    }

    void onSavedOrder(TinyMap map) {
        double changeGiven = map.getDouble("changeGiven");
        if(changeGiven != 0) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.change_given_back))
                    .setMessage(String.format(
                            getString(R.string.format_currency), changeGiven
                    ))
                    .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            OrderActivity.this.finish();
                        }
                    })
                    .show();
        } else {
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ticket", ModelHelper.toJson(mTicket));
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

    public void syncTicket() {
        double total = 0, due = 0;
        int fulfilled = 0, numFood = 0;
        double subtotal = 0;
        double tax = 0;

        List<TicketFood> ticketFoodList = mTicket.getFoodItems();
        if(ticketFoodList != null) {
            for(TicketFood ticketFood : ticketFoodList) {
                ticketFood.setFulfilled(
                        Math.max(Math.min(ticketFood.getFulfilled(), ticketFood.getQuantity()), 0)
                );
                fulfilled += ticketFood.getFulfilled();
                numFood += Math.max(ticketFood.getQuantity(), 0);

                subtotal += ticketFood.getExPrice();
                tax += TinyUtils.toPrecision(ticketFood.getExPrice() * ticketFood.getTaxRate(), 2);
            }
        }

        tax = TinyUtils.toPrecision(tax, 2);
        subtotal = TinyUtils.toPrecision(subtotal, 2);
        total = TinyUtils.toPrecision(subtotal + tax + mTicket.getFee(), 2);

        due = total;
        List<TicketPayment> ticketPaymentList = mTicket.getPayments();
        if(ticketPaymentList != null) {
            for(TicketPayment ticketPayment : ticketPaymentList)
                due -= ticketPayment.getAmount();
        }
        due = TinyUtils.toPrecision(due, 2);

        mTicket.setNumFoodFullfilled(fulfilled);
        mTicket.setNumFood(numFood);
        mTicket.setSubtotal(subtotal);
        mTicket.setTax(tax);
        mTicket.setTotal(total);
        mTicket.setBalance(due);
    }

    @Override
    public void addFood(TicketFood ticketFood) {
        ticketFood.setPrice(TinyUtils.toPrecision(ticketFood.getPrice(), 2));

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
                lastTicketFood.setExPrice(TinyUtils.toPrecision(
                        lastTicketFood.getPrice() * lastTicketFood.getQuantity(), 2
                ));
                shouldAdd = false;
            }
        }

        if(shouldAdd) {
            ticketFood.setExPrice(TinyUtils.toPrecision(
                    ticketFood.getPrice() * ticketFood.getQuantity(), 2
            ));
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
        price = TinyUtils.toPrecision(price, 2);
        ticketFood.setPrice(price);
        ticketFood.setExPrice(TinyUtils.toPrecision(quantity * price, 2));

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
        bundle.putBoolean("IsResultNeeded", true);
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
        AppGlobal.getInstance().getUiApiCallClient().makeCall(
                "/Ticket/deleteDoc",
                mTicket,
                Map.class,
                new ApiCallClient.OnResultListener<Map>() {
                    @Override
                    public void onResult(ApiCallClient.Result<Map> result) {
                        TinyMap map = TinyMap.AsTinyMap(result.data);
                        if(result.error != null || result.data == null || !map.getBoolean("success")) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    getString(R.string.delete_order_error),
                                    Toast.LENGTH_LONG
                            ).show();
                        } else {
                            finish();
                        }
                    }
                }
        );
    }

    @Override
    public void openPaymentWnd() {
        openWnd(
                OrderPaymentFragment.class.getSimpleName(),
                OrderPaymentFragment.newInstance()
        );
    }

    @Override
    public void closePaymentWnd() {
        closeWnd(OrderPaymentFragment.class.getSimpleName());
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
        if(!data.moveToFirst()) {
            Toast.makeText(this,
                    String.format(getString(R.string.open_order_error), mTicket.getId()),
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
                    return getString(R.string.title_order_info_fragment);
                case 1:
                    return getString(R.string.title_order_menu_fragment);
                case 2:
                    return getString(R.string.title_order_cart_fragment);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

}
