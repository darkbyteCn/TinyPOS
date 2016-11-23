package com.tinyappsdev.tinypos.ui.TicketFragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketPayment;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.OrderActivityInterface;
import com.tinyappsdev.tinypos.ui.BaseUI.SimpleCustomDialog;
import com.tinyappsdev.tinypos.ui.BaseUI.TicketActivityInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class TicketPaymentFragment extends BaseFragment<TicketActivityInterface> {

    public static String[] sPaymentTypeArrays;

    @BindView(R.id.total) TextView mTotal;
    @BindView(R.id.subtotal) TextView mSubtotal;
    @BindView(R.id.tax) TextView mTax;
    @BindView(R.id.due) TextView mDue;
    @BindView(R.id.paymentList) ListView mPaymentList;
    @BindView(R.id.paymentListEmptyView) View mPaymentListEmptyView;
    private Unbinder mUnbinder;

    private MyAdapter mMyAdapter;

    public TicketPaymentFragment() {
    }

    public static TicketPaymentFragment newInstance() {
        TicketPaymentFragment fragment = new TicketPaymentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments != null) {
        }

        sPaymentTypeArrays = getResources().getStringArray(R.array.paymentTypeArray);
    }

    @Override
    protected void onMessage(Message msg) {
        switch(msg.what) {
            case R.id.ticketActivityOnTicketUpdate: {
                updateUI();
                break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_payment, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mPaymentList.setEmptyView(mPaymentListEmptyView);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        updateUI();
    }

    private void updateUI() {
        Ticket ticket = mActivity.getTicket();

        String format_currency = getString(R.string.format_currency);
        mTotal.setText(String.format(format_currency, ticket.getTotal()));
        mSubtotal.setText(String.format(format_currency, ticket.getSubtotal()));
        mTax.setText(String.format(format_currency, ticket.getTax()));
        mDue.setText(String.format(format_currency, ticket.getBalance()));

        List<TicketPayment> ticketPaymentList = ticket.getPayments();
        if(ticketPaymentList == null) ticketPaymentList = new ArrayList();

        mMyAdapter = new MyAdapter(getContext(), ticketPaymentList);
        mPaymentList.setAdapter(mMyAdapter);
    }

    class MyAdapter extends ArrayAdapter {

        public MyAdapter(Context context, List list) {
            super(context, R.layout.fragment_payment_list_item, list);
        }

        class ViewHolder {
            @BindView(R.id.label) TextView label;
            @BindView(R.id.value) TextView value;
            int position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.fragment_payment_list_item, parent, false);
                final MyAdapter.ViewHolder holder = new MyAdapter.ViewHolder();
                ButterKnife.bind(holder, convertView);

                convertView.setTag(holder);
            }

            TicketPayment ticketPayment = (TicketPayment)getItem(position);
            MyAdapter.ViewHolder holder = (MyAdapter.ViewHolder)convertView.getTag();
            holder.position = position;
            int ticketType = ticketPayment.getType();
            if(ticketType >= 0 && ticketType < sPaymentTypeArrays.length)
                holder.label.setText(sPaymentTypeArrays[ticketType]);
            else
                holder.label.setText("");
            holder.value.setText(
                    String.format(getString(R.string.format_currency), ticketPayment.getAmount())
            );

            return convertView;
        }

    }

}
