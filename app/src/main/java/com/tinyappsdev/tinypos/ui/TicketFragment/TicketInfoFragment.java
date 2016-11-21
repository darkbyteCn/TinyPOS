package com.tinyappsdev.tinypos.ui.TicketFragment;


import android.content.Intent;
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
import com.tinyappsdev.tinypos.ui.BaseUI.TicketActivityInterface;
import com.tinyappsdev.tinypos.ui.CustomerActivity;
import com.tinyappsdev.tinypos.ui.TicketActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class TicketInfoFragment extends BaseFragment<TicketActivityInterface> {

    @BindView(R.id.ticketInfo) TextView mticketInfo;
    @BindView(R.id.ticketType) TextView mTicketType;
    @BindView(R.id.ticketTableNumer) TextView mTicketTableNumer;
    @BindView(R.id.ticketGuestCount) TextView mTicketGuestCount;
    @BindView(R.id.ticketCustomerInfo) TextView mTicketCustomerInfo;
    @BindView(R.id.ticketNotes) TextView mTicketNotes;
    @BindView(R.id.ticketTypeDineInOnly) View mTicketTypeDineInOnly;
    private Unbinder mUnbinder;

    public TicketInfoFragment() {
    }

    public static TicketInfoFragment newInstance() {
        TicketInfoFragment fragment = new TicketInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    protected void updateUI() {
        Ticket ticket = mActivity.getTicket();

        if(ticket.getTableId() >= 0) {
            mTicketType.setText(getString(R.string.dine_in));
            mTicketTableNumer.setText(ticket.getTableName() == null
                    ? getString(R.string.no_table)
                    : ticket.getTableName()
            );
            mTicketGuestCount.setText(String.format(
                    getString(R.string.format_ticket_num_guest),
                    ticket.getNumGuest() == 0 ? 1 : ticket.getNumGuest()
            ));
            mTicketTypeDineInOnly.setVisibility(View.VISIBLE);

        } else if(ticket.getTableId() == -1) {
            mTicketType.setText(getString(R.string.to_go));
            mTicketTypeDineInOnly.setVisibility(View.GONE);

        } else if(ticket.getTableId() == -2) {
            mTicketType.setText(getString(R.string.delivery));
            mTicketTypeDineInOnly.setVisibility(View.GONE);

        }

        mticketInfo.setText(String.format(
                getString(R.string.format_ticket_primary_info),
                ticket.getId(),
                DateUtils.getRelativeTimeSpanString(ticket.getCreatedTime())
        ));

        if(ticket.getCustomer() == null)
            mTicketCustomerInfo.setText(R.string.no_customer);
        else {
            Customer customer = ticket.getCustomer();
            mTicketCustomerInfo.setText(String.format(
                    getString(R.string.format_customer_info),
                    customer.getName(),
                    customer.getPhone(),
                    customer.getAddress(), customer.getAddress2(),
                    customer.getCity(), customer.getState()
            ));
        }

        mTicketNotes.setText(
                ticket.getNotes() == null ? getString(R.string.no_notes) : ticket.getNotes()
        );
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_info, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        ((LinearLayout)mTicketCustomerInfo.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mActivity.getTicket().getCustomer() == null) return;

                Intent intent = new Intent(getContext(), CustomerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("customerId", mActivity.getTicket().getCustomer().getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

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
