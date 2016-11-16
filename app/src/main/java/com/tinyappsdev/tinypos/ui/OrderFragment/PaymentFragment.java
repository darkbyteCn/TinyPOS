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


public class PaymentFragment extends BaseFragment<OrderActivityInterface> implements
        PopupMenu.OnMenuItemClickListener {

    public static String[] sPaymentTypeArrays;

    @BindView(R.id.title) TextView mTitle;
    @BindView(R.id.total) TextView mTotal;
    @BindView(R.id.subtotal) TextView mSubtotal;
    @BindView(R.id.tax) TextView mTax;
    @BindView(R.id.due) TextView mDue;
    @BindView(R.id.paymentList) ListView mPaymentList;
    @BindView(R.id.paymentListEmptyView) View mPaymentListEmptyView;
    private Unbinder mUnbinder;

    private MyAdapter mMyAdapter;
    private List<TicketPayment> mTicketPaymentList;

    public PaymentFragment() {
    }

    public static PaymentFragment newInstance() {
        PaymentFragment fragment = new PaymentFragment();
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

        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        view.findViewById(R.id.addPayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentCollectionDialog.newInstance(-1).show(
                        PaymentFragment.this.getFragmentManager(),
                        PaymentCollectionDialog.class.getSimpleName()
                );
            }
        });

        mPaymentList.setEmptyView(mPaymentListEmptyView);
        mPaymentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TicketPayment ticketPayment = (TicketPayment)adapterView.getItemAtPosition(i);
                if(ticketPayment.getId() > 0) return;

                PaymentCollectionDialog.newInstance(i).show(
                        PaymentFragment.this.getFragmentManager(),
                        PaymentCollectionDialog.class.getSimpleName()
                );
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

        mTotal.setText(String.format("$%.2f", ticket.getTotal()));
        mSubtotal.setText(String.format("$%.2f", ticket.getSubtotal()));
        mTax.setText(String.format("$%.2f", ticket.getTax()));
        mDue.setText(String.format("$%.2f", ticket.getBalance()));

        if(ticket.getPayments() == mTicketPaymentList) {
            mMyAdapter.notifyDataSetChanged();
        } else {
            mTicketPaymentList = ticket.getPayments();
            mMyAdapter = new MyAdapter(getContext(), mTicketPaymentList);
            mPaymentList.setAdapter(mMyAdapter);
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
            //@BindView(R.id.remove) ImageButton remove;
            int position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.fragment_payment_list_item, parent, false);
                final MyAdapter.ViewHolder holder = new MyAdapter.ViewHolder();
                ButterKnife.bind(holder, convertView);
                /*
                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActivity.removePayment(holder.position);
                    }
                });
                */
                convertView.setTag(holder);
            }

            TicketPayment ticketPayment = (TicketPayment)getItem(position);
            MyAdapter.ViewHolder holder = (MyAdapter.ViewHolder)convertView.getTag();
            holder.position = position;
            holder.label.setText(sPaymentTypeArrays[ticketPayment.getType()]);
            holder.value.setText(String.format("$%.2f", ticketPayment.getAmount()));
            //holder.remove.setVisibility(ticketPayment.getId() > 0 ? View.INVISIBLE : View.VISIBLE);

            return convertView;
        }

    }

    public static class PaymentCollectionDialog extends SimpleCustomDialog<OrderActivityInterface> {
        @BindView(R.id.paymentType) Spinner paymentType;
        @BindView(R.id.paymentAmount) TextView paymentAmount;

        public static PaymentCollectionDialog newInstance(int index) {
            Bundle args = new Bundle();
            args.putInt("index", index);
            args.putString("title", "Collect Payment");

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

            int index = getArguments().getInt("index", -1);
            if(savedInstanceState == null && index >= 0) {
                TicketPayment ticketPayment = mActivity.getTicket().getPayments().get(index);
                paymentType.setSelection(ticketPayment.getType());
                paymentAmount.setText(String.format("%.2f", ticketPayment.getAmount()));
            }

            builder.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mActivity.removePayment(getArguments().getInt("index", -1));
                }
            });

            return view;
        }

        @Override
        public void onConfirm() {
            double amount = Double.parseDouble(paymentAmount.getText().toString());

            mActivity.setPayment(
                    getArguments().getInt("index", -1),
                    paymentType.getSelectedItemPosition(),
                    amount
            );
        }
    }
}
