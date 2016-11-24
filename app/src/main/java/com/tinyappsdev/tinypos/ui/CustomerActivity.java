package com.tinyappsdev.tinypos.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.tinyappsdev.tinypos.AppGlobal;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.Customer;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.helper.TinyUtils;
import com.tinyappsdev.tinypos.rest.ApiCallClient;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseActivity;
import com.tinyappsdev.tinypos.ui.BaseUI.CustomerActivityInterface;
import com.tinyappsdev.tinypos.ui.CustomerFragment.CustomerInfoFragment;
import com.tinyappsdev.tinypos.ui.CustomerFragment.CustomerMapFragment;
import com.tinyappsdev.tinypos.ui.CustomerFragment.CustomerOrderHistoryFragment;
import com.tinyappsdev.tinypos.ui.CustomerFragment.CustomerSearchFragment;

import java.util.Map;

public class CustomerActivity extends SyncableActivity implements
        CustomerActivityInterface,
        SearchView.OnQueryTextListener {

    public static final String SearchFragmentTag = CustomerSearchFragment.class.getSimpleName();

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Customer mCustomer;
    private String mLastSearchQuery;
    private boolean mIsSearchActive;
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private Handler mHandler;
    private Runnable mSearchDelay;
    private boolean mIsResultNeeded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        mHandler = new Handler();
        mSearchDelay = new Runnable() {
            @Override
            public void run() {
                sendMessage(R.id.customerActivityOnSearchQueryChange);
            }
        };

        mIsResultNeeded = bundle != null && bundle.getBoolean("IsResultNeeded");

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = (TabLayout)findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        if(savedInstanceState != null)
            mCustomer = ModelHelper.fromJson(savedInstanceState.getString("mCustomer"), Customer.class);

        if(mCustomer == null) {
            mCustomer = new Customer();
            mCustomer.setDbRev(-1);
            if(bundle != null) mCustomer.setId(bundle.getLong("customerId"));
        }

        if(savedInstanceState == null) {
            if(mCustomer.getId() == 0) {
                showSearchResults();
                sendMessage(R.id.customerActivityOnSearchQueryChange);
            }
        }

        loadCustomer(mCustomer.getId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.list: {
                showSearchResults();
                sendMessage(R.id.customerActivityOnSearchQueryChange);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void showSearchResults() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SearchFragmentTag);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == null) {
            fragment = CustomerSearchFragment.newInstance();
            fragmentTransaction.add(R.id.content_customer, fragment, SearchFragmentTag);
        } else {
            fragmentTransaction.show(fragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_customer, menu);

        mSearchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        mSearchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                showSearchResults();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mSearchView.setQuery(mLastSearchQuery, false);
                    }
                });

                mIsSearchActive = true;

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                if(mCustomer.getId() > 0 || mCustomer.getDbRev() == 0) {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(SearchFragmentTag);
                    if (fragment != null) {
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.hide(fragment);
                        fragmentTransaction.commit();
                    }
                }

                mLastSearchQuery = mSearchView.getQuery().toString();
                mIsSearchActive = false;
                mHandler.removeCallbacks(mSearchDelay);

                return true;
            }
        });

        return true;
    }

    @Override
    public String getSearchQuery() {
        return mSearchView.getQuery().toString();
    }

    @Override
    public Customer getCustomer() {
        return mCustomer;
    }

    @Override
    public void selectCustomer(long customerId) {
        loadCustomer(customerId);
        mSearchItem.collapseActionView();
    }

    @Override
    public void saveCustomer(final Customer customer) {
        View view = getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }

        customer.setId(mCustomer.getId());
        customer.setDbRev(mCustomer.getDbRev());

        if(customer.getName().equals(mCustomer.getName())
                && customer.getPhone().equals(mCustomer.getPhone())
                && customer.getAddress().equals(mCustomer.getAddress())
                && customer.getAddress2().equals(mCustomer.getAddress2())
                && customer.getCity().equals(mCustomer.getCity())
                && customer.getState().equals(mCustomer.getState())
                ) {
            if(isResultNeeded()) setResult(customer);

            return;
        }

        AppGlobal.getInstance().getUiApiCallClient().saveCustomer(
                customer,
                Map.class,
                new ApiCallClient.OnResultListener() {
                    @Override
                    public void onResult(ApiCallClient.Result result) {
                        if(result.error != null || result.data == null) {
                            Toast.makeText(CustomerActivity.this, "Can't Save", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Map res = (Map)result.data;
                        if(customer.getId() == 0) customer.setId(((Number)res.get("_id")).longValue());

                        if(isResultNeeded()) {
                            setResult(customer);
                        } else {
                            mCustomer.setDbRev(-1);
                            loadCustomer(customer.getId());
                        }
                    }
                }
        );
    }

    @Override
    public void setResult(Customer customer) {
        String customerJs = ModelHelper.toJson(customer);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("customerJs", customerJs);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean isResultNeeded() {
        return mIsResultNeeded;
    }

    public void loadCustomer(long customerId) {
        if(mResult != null) mResult.cancel();
        if(customerId == mCustomer.getId() && mCustomer.getDbRev() >= 0) return;

        mCustomer = new Customer();
        mCustomer.setId(customerId);

        if(customerId == 0) {
            sendMessage(R.id.customerActivityOnCustomerUpdate);
            return;
        }

        mCustomer.setDbRev(-1);
        mResult = AppGlobal.getInstance().getUiApiCallClient().makeCall(
                "/Customer/getDoc?_id=" + customerId,
                null,
                Customer.class,
                new ApiCallClient.OnResultListener<Customer>() {
                    @Override
                    public void onResult(ApiCallClient.Result<Customer> result) {
                        if(result.error != null || result.data == null) {
                            TinyUtils.showMsgBox(getApplicationContext(), R.string.error_occurred);
                        } else {
                            mCustomer = result.data;
                            sendMessage(R.id.customerActivityOnCustomerUpdate);
                        }
                    }
                }
        );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mHandler.removeCallbacks(mSearchDelay);
        if(mIsSearchActive) mHandler.postDelayed(mSearchDelay, 300);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return onQueryTextSubmit(newText);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return CustomerInfoFragment.newInstance();
            else if(position == 1)
                return CustomerOrderHistoryFragment.newInstance();
            else if(position == 2)
                return CustomerMapFragment.newInstance();
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        public CharSequence getPageTitle(int position) {
            if(position == 0)
                return getString(R.string.title_customer_info_fragment);
            else if(position == 1)
                return getString(R.string.title_customer_order_history_fragment);
            //else if(position == 2)
            //    return getString(R.string.title_customer_map_fragment);

            return null;
        }

    }

}
