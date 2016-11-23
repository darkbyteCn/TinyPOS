package com.tinyappsdev.tinypos.ui.OrderFragment;


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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketPayment;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.OrderActivityInterface;
import com.tinyappsdev.tinypos.ui.BaseUI.SimpleCustomDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class OrderPaymentFragment extends BaseFragment<OrderActivityInterface> implements
        PopupMenu.OnMenuItemClickListener {

    public static String[] sPaymentTypeArrays;

    @BindView(R.id.total) TextView mTotal;
    @BindView(R.id.subtotal) TextView mSubtotal;
    @BindView(R.id.tax) TextView mTax;
    @BindView(R.id.due) TextView mDue;
    @BindView(R.id.paymentList) ListView mPaymentList;
    @BindView(R.id.paymentListEmptyView) TextView mPaymentListEmptyView;
    @BindView(R.id.payOnly) Button mPayOnly;
    @BindView(R.id.addPayment) Button mAddPayment;
    @BindView(R.id.payAndComplete) Button mPayAndComplete;

    private Unbinder mUnbinder;

    private MyAdapter mMyAdapter;
    private List<TicketPayment> mTicketPaymentList;

    public OrderPaymentFragment() {
    }

    public static OrderPaymentFragment newInstance() {
        OrderPaymentFragment fragment = new OrderPaymentFragment();
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
            case R.id.orderActivityOnTicketChange:
            case R.id.orderActivityOnTicketInfoChange: {
                updateUI();
                break;
            }
        }
    }

    public void openPaymentDialog(int index) {
        PaymentCollectionDialog.newInstance(index).show(
                OrderPaymentFragment.this.getFragmentManager(),
                PaymentCollectionDialog.class.getSimpleName()
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.closePaymentWnd();
            }
        });

        mAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPaymentDialog(-1);
            }
        });
        mPayOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.saveOrder(true, false);
            }
        });
        mPayAndComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int state = mActivity.getTicket().getState();
                if((state & Ticket.STATE_PAID) != 0)
                    mActivity.checkout();
                else
                    mActivity.saveOrder(true, true);
            }
        });

        mPaymentListEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPaymentDialog(-1);
            }
        });
        mPaymentListEmptyView.setText(getString(R.string.order_Payment_fragment_empty_payment));
        mPaymentList.setEmptyView(mPaymentListEmptyView);
        mPaymentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TicketPayment ticketPayment = (TicketPayment)adapterView.getItemAtPosition(i);
                if(ticketPayment.getId() > 0) return;
                openPaymentDialog(i);
            }
        });

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

        if(ticket.getPayments() == mTicketPaymentList) {
            mMyAdapter.notifyDataSetChanged();
        } else {
            mTicketPaymentList = ticket.getPayments();
            mMyAdapter = new MyAdapter(getContext(), mTicketPaymentList);
            mPaymentList.setAdapter(mMyAdapter);
        }

        if((ticket.getState() & Ticket.STATE_PAID) != 0) {
            mAddPayment.setVisibility(View.GONE);
            mPayOnly.setVisibility(View.GONE);
            if(ticket.getTableId() < 0)
                mPayAndComplete.setVisibility(View.GONE);
            else if((ticket.getState() & Ticket.STATE_COMPLETED) != 0)
                mPayAndComplete.setVisibility(View.GONE);
            else
                mPayAndComplete.setVisibility(View.VISIBLE);
        } else {
            mAddPayment.setVisibility(View.VISIBLE);
            mPayOnly.setVisibility(ticket.getTableId() < 0 ? View.GONE : View.VISIBLE);
            mPayAndComplete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()) {

        }

        return false;
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

    public static class PaymentCollectionDialog extends SimpleCustomDialog<OrderActivityInterface> {
        @BindView(R.id.paymentType) Spinner paymentType;
        @BindView(R.id.paymentAmount) TextView paymentAmount;

        public static PaymentCollectionDialog newInstance(int index) {
            Bundle args = new Bundle();
            args.putInt("index", index);

            PaymentCollectionDialog fragment = new PaymentCollectionDialog();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateCustomView(Bundle savedInstanceState,
                                       AlertDialog.Builder builder,
                                       LayoutInflater inflater,
                                       ViewGroup parent)
        {
            View view = inflater.inflate(R.layout.dialog_payment, parent);
            ButterKnife.bind(this, view);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                    R.array.paymentTypeArray, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            paymentType.setAdapter(adapter);

            TicketPayment ticketPayment = null;
            int index = getArguments().getInt("index", -1);
            if(index >= 0) ticketPayment = mActivity.getTicket().getPayments().get(index);

            if(savedInstanceState == null && ticketPayment != null) {
                paymentType.setSelection(ticketPayment.getType());
                paymentAmount.setText(String.valueOf(ticketPayment.getAmount()));
            }

            if(ticketPayment != null && ticketPayment.getId() <= 0) {
                builder.setNeutralButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mActivity.removePayment(getArguments().getInt("index", -1));
                    }
                });
            }

            builder.setTitle(getString(R.string.order_Payment_fragment_collect_payment));

            return view;
        }

        @Override
        public void onConfirm() {
            double amount = 0.0;
            try {
                amount = Double.parseDouble(paymentAmount.getText().toString());
            } catch (NumberFormatException e) {
            }
            if(amount > 0)
                mActivity.setPayment(
                        getArguments().getInt("index", -1),
                        paymentType.getSelectedItemPosition(),
                        amount
                );
        }
    }
}
