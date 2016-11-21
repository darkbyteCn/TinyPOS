package com.tinyappsdev.tinypos.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.tinyappsdev.tinypos.AppGlobal;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.helper.TinyMap;
import com.tinyappsdev.tinypos.helper.TinyUtils;
import com.tinyappsdev.tinypos.rest.ApiCallClient;
import com.tinyappsdev.tinypos.ui.BaseUI.KitchenActivityInterface;
import com.tinyappsdev.tinypos.ui.KitchenFragment.PendingFoodFragment;
import com.tinyappsdev.tinypos.ui.KitchenFragment.PendingOrderFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KitchenActivity extends SyncableActivity implements
        KitchenActivityInterface {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void goBack(View view) {
        finish();
    }


    static class ApiResponseFulfillFood {
        static class TicketStatus {
            long _id;
            boolean success;
            boolean allFulfilled;
        }

        boolean success;
        List<TicketStatus> resultList;
    }

    @Override
    public void fulfillFood(Map<Long, Map<Integer, Integer>> items) {
        AppGlobal.getInstance().getUiApiCallClient().makeCall(
                "/Ticket/fulfill",
                items,
                ApiResponseFulfillFood.class,
                new ApiCallClient.OnResultListener<ApiResponseFulfillFood>() {
                    @Override
                    public void onResult(ApiCallClient.Result<ApiResponseFulfillFood> result) {
                        if(result.error != null || result.data == null || !result.data.success) {
                            TinyUtils.showMsgBox(getApplicationContext(), R.string.error_occurred);
                        } else {
                            onFulfilledFood(result.data);
                        }
                    }
                }
        );
    }

    public void onFulfilledFood(ApiResponseFulfillFood result) {
        if(result.resultList == null) return;

        List<String> list = new ArrayList();
        for(ApiResponseFulfillFood.TicketStatus ticketStatus : result.resultList) {
            if(!ticketStatus.success || !ticketStatus.allFulfilled) continue;
            list.add(String.format(
                    getString(R.string.format_ticket_all_food_fulfilled),
                    ticketStatus._id
            ));
        }
        if(list.size() == 0) return;


        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.fulfilled_list))
                .setMessage(TextUtils.join("\n", list))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

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
                    return getString(R.string.title_kitchen_pending_food_fragment);
                case 1:
                    return getString(R.string.title_kitchen_pending_order_fragment);
            }
            return null;
        }
    }

}
