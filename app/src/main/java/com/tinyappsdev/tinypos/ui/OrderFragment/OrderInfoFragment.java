package com.tinyappsdev.tinypos.ui.OrderFragment;


import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.Customer;
import com.tinyappsdev.tinypos.data.DineTable;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketPayment;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.NumberPickerDialog;
import com.tinyappsdev.tinypos.ui.BaseUI.OrderActivityInterface;
import com.tinyappsdev.tinypos.ui.BaseUI.TextEditorDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class OrderInfoFragment extends BaseFragment<OrderActivityInterface> {

    @BindView(R.id.ticketInfo) TextView mticketInfo;
    @BindView(R.id.ticketType) RadioGroup mTicketType;
    @BindView(R.id.ticketTableNumer) TextView mTicketTableNumer;
    @BindView(R.id.ticketGuestCount) TextView mTicketGuestCount;
    @BindView(R.id.ticketCustomerInfo) TextView mTicketCustomerInfo;
    @BindView(R.id.ticketPaymentInfo) TextView mTicketPaymentInfo;
    @BindView(R.id.ticketNotes) TextView mTicketNotes;
    @BindView(R.id.ticketTypeDineInOnly) View mTicketTypeDineInOnly;
    private Unbinder mUnbinder;

    public OrderInfoFragment() {
    }

    public static OrderInfoFragment newInstance() {
        OrderInfoFragment fragment = new OrderInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    protected void updateUI() {
        Ticket ticket = mActivity.getTicket();

        if(ticket.getTableId() >= 0) {
            mTicketType.check(R.id.ticketType_dineIn);
            mTicketTableNumer.setText(ticket.getTableName() == null ? "* Tap here to select a table"
                    : ticket.getTableName()
            );
            mTicketGuestCount.setText((ticket.getNumGuest() == 0 ? 1 : ticket.getNumGuest())+ "x");

        } else if(ticket.getTableId() == -1) {
            mTicketType.check(R.id.ticketType_toGo);

        } else if(ticket.getTableId() == -2) {
            mTicketType.check(R.id.ticketType_delivery);

        }

        if(ticket.getId() == 0)
            mticketInfo.setText("New Ticket");
        else
            mticketInfo.setText(String.format("TicketId: %d (%s)",
                    ticket.getId(),
                    DateUtils.getRelativeTimeSpanString(ticket.getCreatedTime())
            ));

        if(ticket.getCustomer() == null)
            mTicketCustomerInfo.setText("* Tap to select a customer");
        else {
            Customer customer = ticket.getCustomer();
            mTicketCustomerInfo.setText(String.format(
                    "%s (%s)\n%s, %s\n%s, %s",
                    customer.getName(),
                    customer.getPhone(),
                    customer.getAddress(), customer.getAddress2(),
                    customer.getCity(), customer.getState()
            ));
        }

        List<TicketPayment> ticketPaymentList = ticket.getPayments();
        if(ticketPaymentList == null || ticketPaymentList.size() == 0)
            mTicketPaymentInfo.setText("No payment yet");
        else {
            String[] paymentTypeArray = getResources().getStringArray(R.array.paymentTypeArray);
            String[] payments = new String[ticketPaymentList.size()];
            for(int i = 0; i < ticketPaymentList.size(); i++) {
                TicketPayment ticketPayment = ticketPaymentList.get(i);
                payments[i] = String.format(
                        "%s %s",
                        paymentTypeArray[ticketPayment.getType()],
                        ticketPayment.getAmount()
                );
            }
            mTicketPaymentInfo.setText(TextUtils.join("\n", payments));
        }

        mTicketNotes.setText(ticket.getNotes() == null ? "* Tap to enter notes" : ticket.getNotes());
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_info, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mTicketType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.ticketType_dineIn) {
                    mTicketTypeDineInOnly.setVisibility(View.VISIBLE);
                } else if(i == R.id.ticketType_toGo) {
                    mTicketTypeDineInOnly.setVisibility(View.GONE);
                } else if(i == R.id.ticketType_delivery) {
                    mTicketTypeDineInOnly.setVisibility(View.GONE);
                }
            }
        });

        ((LinearLayout)mTicketCustomerInfo.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.openCustomerPicker();
            }
        });

        ((LinearLayout)mTicketTableNumer.getParent()).setOnClickListener(new TableNumberOnClickListener());
        ((LinearLayout)mTicketGuestCount.getParent()).setOnClickListener(new GuestCountOnClickListener());
        ((LinearLayout)mTicketNotes.getParent()).setOnClickListener(new NotesOnClickListener());

        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        updateUI();
    }

    class GuestCountOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            NumberPickerDialog dialog = new GuestCountDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("min", 1);
            bundle.putInt("max", 20);
            bundle.putInt("val", mActivity.getTicket().getNumGuest());
            bundle.putString("msg", "select number");
            dialog.setArguments(bundle);
            dialog.show(
                    OrderInfoFragment.this.getActivity().getSupportFragmentManager(),
                    "GuestCountDialog"
            );
        }
    }

    public static class GuestCountDialog extends NumberPickerDialog<OrderActivityInterface> {
        @Override
        public void onConfirm(int number) {
            mActivity.setNumGuest(number);
            dismiss();
        }
    }

    class NotesOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            NotesDialog dialog = new NotesDialog();
            Bundle bundle = new Bundle();
            bundle.putString("val", mActivity.getTicket().getNotes());
            bundle.putString("msg", "enter text");
            dialog.setArguments(bundle);
            dialog.show(
                    OrderInfoFragment.this.getActivity().getSupportFragmentManager(),
                    "NotesDialog"
            );
        }
    }

    public static class NotesDialog extends TextEditorDialog<OrderActivityInterface> {
        @Override
        public void onConfirm(String text) {
            mActivity.setNotes(text);
            dismiss();
        }
    }

    class TableNumberOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            TableNumberDialog dialog = new TableNumberDialog();
            Bundle bundle = new Bundle();
            bundle.putString("msg", "Select Table Number");
            bundle.putLong("_id", mActivity.getTicket().getTableId());
            dialog.setArguments(bundle);
            dialog.show(
                    OrderInfoFragment.this.getActivity().getSupportFragmentManager(),
                    "NotesDialog"
            );
        }
    }

    public static class TableNumberDialog extends NumberPickerDialog<OrderActivityInterface> implements
            LoaderManager.LoaderCallbacks<Cursor> {

        class TableNumber {
            long _id;
            String name;
            TableNumber(long _id, String name) { this._id = _id; this.name = name; }
        }

        TableNumber[] mTableNumberList;
        long mId;

        @Override
        public void onConfirm(int number) {
            if(mTableNumberList == null || number >= mTableNumberList.length) return;

            mActivity.setDineTable(mTableNumberList[number]._id, mTableNumberList[number].name);
            dismiss();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Bundle bundle = getArguments();
            mId = savedInstanceState != null
                    ? savedInstanceState.getLong("_id") : bundle.getLong("_id");

            mNumberPicker.setSaveEnabled(false);
            getLoaderManager().initLoader(1, null, this);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putLong("_id", mId);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            mTableNumberList = null;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(this.getActivity().getApplicationContext(),
                    ContentProviderEx.BuildUri(DineTable.Schema.TABLE_NAME),
                    new String[] {DineTable.Schema.COL_ID, DineTable.Schema.COL_NAME},
                    String.format("%s=0", DineTable.Schema.COL_TICKETID),
                    null,
                    DineTable.Schema.COL_ID + " asc"
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if(data == null) return;

            mTableNumberList = new TableNumber[data.getCount()];
            String[] tableNameList = new String[data.getCount()];
            int selIdx = 0;
            for(int i = 0; data.moveToNext(); i++) {
                TableNumber tableNumber = new TableNumber(data.getLong(0), data.getString(1));
                mTableNumberList[i] = tableNumber;
                tableNameList[i] = tableNumber.name;
                if(mId == tableNumber._id) selIdx = i;
            }

            mNumberPicker.setMinValue(0);
            mNumberPicker.setMaxValue(tableNameList.length - 1);
            mNumberPicker.setValue(selIdx);
            mNumberPicker.setDisplayedValues(tableNameList);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

}
