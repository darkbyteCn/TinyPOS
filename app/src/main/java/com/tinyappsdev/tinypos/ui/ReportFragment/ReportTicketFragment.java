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


public class ReportTicketFragment extends BaseFragment<ReportActivityInterface> {
    @BindView(R.id.numberOfTickets) TextView mNumberOfTickets;
    private Unbinder mUnbinder;

    public ReportTicketFragment() {
    }

    public static ReportTicketFragment newInstance() {
        ReportTicketFragment fragment = new ReportTicketFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    protected void updateUI() {
        TinyMap reportData = mActivity.getReportData();
        if(reportData == null) {
            mNumberOfTickets.setText("");
        } else {
            mNumberOfTickets.setText(reportData.getString("ticketCount"));
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
        View view = inflater.inflate(R.layout.fragment_report_ticket, container, false);
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
