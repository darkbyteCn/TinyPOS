package com.tinyappsdev.tinypos.ui.ReportFragment;


import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.helper.TinyMap;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.ReportActivityInterface;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ReportFoodFragment extends BaseFragment<ReportActivityInterface> {
    @BindView(R.id.numberOfFoodItems) TextView mNumberOfFoodItems;
    private Unbinder mUnbinder;

    public ReportFoodFragment() {
    }

    public static ReportFoodFragment newInstance() {
        ReportFoodFragment fragment = new ReportFoodFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    protected void updateUI() {
        TinyMap reportData = mActivity.getReportData();
        if(reportData == null) {
            mNumberOfFoodItems.setText("");
        } else {
            mNumberOfFoodItems.setText(reportData.getString("foodItemCount"));
        }
    }

    @Override
    protected void onMessage(Message msg) {
        switch(msg.what) {
            case R.id.reportActivityOnDataUpdate: {
                updateUI();
                break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_food, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        updateUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

}
