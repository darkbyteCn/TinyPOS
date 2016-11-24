package com.tinyappsdev.tinypos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tinyappsdev.tinypos.AppGlobal;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.helper.TinyMap;
import com.tinyappsdev.tinypos.helper.TinyUtils;
import com.tinyappsdev.tinypos.rest.ApiCallClient;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseActivity;
import com.tinyappsdev.tinypos.ui.BaseUI.LoginActivityInterface;
import com.tinyappsdev.tinypos.ui.LoginFragment.LoginEmployeeFragment;
import com.tinyappsdev.tinypos.ui.LoginFragment.LoginServerFragment;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends BaseActivity implements LoginActivityInterface {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ApiCallClient mApiCallClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mApiCallClient = new ApiCallClient(AppGlobal.getInstance());

        String serverAddress = mSharedPreferences.getString("serverAddress", "");
        if(serverAddress != null && !serverAddress.isEmpty())
            getSupportActionBar().setTitle(String.format(
                    getString(R.string.format_title_with_server_address),
                    getString(R.string.title_activity_login),
                    serverAddress
            ));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.server_config: {
                mViewPager.setCurrentItem(0);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Bundle bundle = getIntent().getExtras();
        if(bundle == null || !bundle.getBoolean("popup")) {
            mSharedPreferences.edit()
                    .putInt("employeeCode", 0)
                    .putString("employeeName", "")
                    .apply();
        }

        String serverAuth = mSharedPreferences.getString("serverAuth", "");
        if(serverAuth == null || serverAuth.isEmpty())
            checkAuth();
        else
            checkEmployee();
    }

    public void checkAuth() {
        String serverAuth = mSharedPreferences.getString("serverAuth", "");
        if(serverAuth == null || serverAuth.isEmpty()) {
            mViewPager.setCurrentItem(0);
            return;
        }

        if(mResult != null) mResult.cancel();
        mResult = AppGlobal.getInstance().getUiApiCallClient().makeCall(
                "/Auth/checkAuth",
                null,
                Map.class,
                new ApiCallClient.OnResultListener<Map>() {
                    @Override
                    public void onResult(ApiCallClient.Result<Map> result) {
                        if(result.error != null || result.data == null) {
                            TinyUtils.showMsgBox(getApplicationContext(), R.string.error_occurred);
                            mViewPager.setCurrentItem(0);
                            return;
                        }
                        TinyMap tinyMap = TinyMap.AsTinyMap(result.data);
                        if(!tinyMap.getBoolean("success")) {
                            mViewPager.setCurrentItem(0);
                            return;
                        }

                        onLoginServer(mSharedPreferences.getString("serverAddress", ""));
                    }
                }
        );
    }

    public void checkEmployee() {
        int employeeCode = mSharedPreferences.getInt("employeeCode", 0);
        if(employeeCode == 0) {
            mViewPager.setCurrentItem(1);
            return;
        }

        loginCustomer(employeeCode);
    }

    @Override
    public void loginServer(final String address, String password) {
        Map map = new HashMap();
        map.put("password", password);

        mApiCallClient.setServerAddress(address);
        if(mResult != null) mResult.cancel();
        mResult = mApiCallClient.makeCall(
                "/Auth/getAuth",
                map,
                Map.class,
                new ApiCallClient.OnResultListener<Map>() {
                    @Override
                    public void onResult(ApiCallClient.Result<Map> result) {
                        if(result.error != null || result.data == null) {
                            TinyUtils.showMsgBox(getApplicationContext(), R.string.error_occurred);
                            mViewPager.setCurrentItem(0);
                            return;
                        }
                        TinyMap tinyMap = TinyMap.AsTinyMap(result.data);
                        if(!tinyMap.getBoolean("success")) {
                            TinyUtils.showMsgBox(getApplicationContext(), R.string.login_failed);
                            mViewPager.setCurrentItem(0);
                            return;
                        }

                        onLoginServer(address);
                    }
                }
        );
    }

    @Override
    public void loginCustomer(final int employeeCode) {
        Map map = new HashMap();
        map.put("employeeCode", employeeCode);

        if(mResult != null) mResult.cancel();
        mResult = AppGlobal.getInstance().getUiApiCallClient().makeCall(
                "/Auth/getEmployeeInfo",
                map,
                Map.class,
                new ApiCallClient.OnResultListener<Map>() {
                    @Override
                    public void onResult(ApiCallClient.Result<Map> result) {
                        if(result.error != null || result.data == null) {
                            TinyUtils.showMsgBox(getApplicationContext(), R.string.error_occurred);
                            mViewPager.setCurrentItem(1);
                            return;
                        }
                        TinyMap tinyMap = TinyMap.AsTinyMap(result.data);
                        if(tinyMap.getBoolean("authFailed")) {
                            TinyUtils.showMsgBox(getApplicationContext(), R.string.login_failed);
                            mViewPager.setCurrentItem(0);
                            return;
                        }
                        if(!tinyMap.getBoolean("success")) {
                            mViewPager.setCurrentItem(1);
                            return;
                        }

                        onLoginCustomer(employeeCode, tinyMap.getString("name"));
                    }
                }
        );
    }

    public void onLoginCustomer(int employeeCode, String employeeName) {
        mSharedPreferences.edit()
                .putInt("employeeCode", employeeCode)
                .putString("employeeName", employeeName)
                .apply();

        finishLogin();
    }

    public void onLoginServer(String address) {
        String oldServerAddress = mSharedPreferences.getString("serverAddress", "");
        if(!address.equals(oldServerAddress)) {
            mSharedPreferences.edit()
                    .clear()
                    .putString("serverAuth", mSharedPreferences.getString("serverAuth", ""))
                    .putLong("syncRequestTs", System.currentTimeMillis())
                    .putBoolean("resyncDatabase", true)
                    .putString("serverAddress", address)
                    .apply();
            AppGlobal.getInstance().onServerInfoChanged(getApplicationContext());
            return;
        }

        mSharedPreferences.edit()
                .putInt("employeeCode", 0)
                .putString("employeeName", "")
                .apply();
        checkEmployee();
    }

    public void finishLogin() {
        Bundle bundle = getIntent().getExtras();
        finish();
        if(bundle == null || !bundle.getBoolean("popup")) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return LoginServerFragment.newInstance();
            else if(position == 1)
                return LoginEmployeeFragment.newInstance();
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

    }
}
