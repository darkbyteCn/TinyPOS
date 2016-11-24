package com.tinyappsdev.tinypos.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.tinyappsdev.tinypos.AppGlobal;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.helper.TinyMap;
import com.tinyappsdev.tinypos.helper.TinyUtils;
import com.tinyappsdev.tinypos.rest.ApiCallClient;
import com.tinyappsdev.tinypos.ui.BaseUI.ActivityInterface;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseActivity;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseDialog;
import com.tinyappsdev.tinypos.ui.BaseUI.ReportActivityInterface;
import com.tinyappsdev.tinypos.ui.CustomerFragment.CustomerInfoFragment;
import com.tinyappsdev.tinypos.ui.CustomerFragment.CustomerOrderHistoryFragment;
import com.tinyappsdev.tinypos.ui.ReportFragment.ReportFoodFragment;
import com.tinyappsdev.tinypos.ui.ReportFragment.ReportTicketFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ReportActivity extends BaseActivity implements ReportActivityInterface {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private TinyMap mReportData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = (TabLayout)findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        if(savedInstanceState == null) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            setReportDate(String.format("%d%02d%02d", year, month, day));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.action_pick_date: {
                new MyDatePickerDialog().show(
                        getSupportFragmentManager(),
                        MyDatePickerDialog.class.getSimpleName()
                );
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setReportDate(String date) {
        if(mResult != null) mResult.cancel();
        mResult = AppGlobal.getInstance().getUiApiCallClient().makeCall(
                "/Report/getTicketOverAll?date=" + date,
                null,
                Map.class,
                new ApiCallClient.OnResultListener<Map>() {
                    @Override
                    public void onResult(ApiCallClient.Result<Map> result) {
                        TinyMap map = TinyMap.AsTinyMap(result.data);
                        if(result.error != null || result.data == null)
                            TinyUtils.showMsgBox(ReportActivity.this, R.string.error_occurred);
                        else if(!map.getBoolean("success"))
                            TinyUtils.showMsgBox(ReportActivity.this, R.string.unexpected_error);
                        else {
                            mReportData = map;
                            sendMessage(R.id.reportActivityOnDataUpdate);
                        }
                    }
                }
        );
    }

    @Override
    public TinyMap getReportData() {
        return mReportData;
    }

    public static class MyDatePickerDialog extends BaseDialog<ReportActivityInterface> implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            mActivity.setReportDate(String.format("%d%02d%02d", year, month, dayOfMonth));
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return ReportTicketFragment.newInstance();
            else if(position == 1)
                return ReportFoodFragment.newInstance();

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        public CharSequence getPageTitle(int position) {
            if(position == 0)
                return getString(R.string.title_report_ticket_fragment);
            else if(position == 1)
                return getString(R.string.title_report_food_fragment);

            return null;
        }

    }

}
