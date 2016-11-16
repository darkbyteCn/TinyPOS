package com.tinyappsdev.tinypos.ui.OrderFragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketFood;
import com.tinyappsdev.tinypos.data.TicketFoodAttr;
import com.tinyappsdev.tinypos.rest.ApiCall;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.OrderActivityInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class OrderTicketFragment extends BaseFragment<OrderActivityInterface> implements
        AdapterView.OnItemClickListener,
        PopupMenu.OnMenuItemClickListener {

    private List<TicketFood> mTicketFoodList;
    private MyAdapter mMyAdapter;

    @BindView(R.id.order_ticket_listview) ListView mListView;
    @BindView(R.id.orderTotal) TextView mOrderTotal;
    @BindView(R.id.orderDue) TextView mOrderDue;
    private Unbinder mUnbinder;

    public OrderTicketFragment() {
    }

    public static OrderTicketFragment newInstance() {
        OrderTicketFragment fragment = new OrderTicketFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    protected void updateUI() {
        Ticket ticket = mActivity.getTicket();
        List<TicketFood> curTicketFoodList = ticket.getFoodItems();
        if(mTicketFoodList != curTicketFoodList) {
            mTicketFoodList = curTicketFoodList;
            mMyAdapter = new MyAdapter(OrderTicketFragment.this.getContext(), mTicketFoodList);
            mListView.setAdapter(mMyAdapter);
        } else
            mMyAdapter.notifyDataSetChanged();

        mOrderTotal.setText(String.format("$%02.2f", ticket.getTotal()));
        mOrderDue.setText(String.format("$%02.2f", ticket.getBalance()));
    }

    @Override
    protected void onMessage(Message msg) {
        switch(msg.what) {
            case R.id.orderActivityOnTicketChange:
            case R.id.orderActivityOnTicketInfoChange:
            case R.id.orderActivityOnTicketFoodChange: {
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
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        view.findViewById(R.id.order_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.openPaymentWnd();
            }
        });
        view.findViewById(R.id.more_options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(OrderTicketFragment.this.getActivity(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_order_list_more_options, popup.getMenu());
                popup.setOnMenuItemClickListener(OrderTicketFragment.this);
                popup.show();
            }
        });
        mListView.setOnItemClickListener(this);

        updateUI();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mTicketFoodList = null;
        mMyAdapter = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TicketFood ticketFood = (TicketFood)mMyAdapter.getItem(i);
        mActivity.openFoodDetailWnd(ticketFood.getId(), i);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearAll: {
                mActivity.clearAllFood();
                return true;
            }
            case R.id.delete: {
                mActivity.deleteTicket();
                return true;
            }
        }

        return false;
    }

    static class MyAdapter extends ArrayAdapter {

        public MyAdapter(Context context, List list) {
            super(context, R.layout.fragment_order_list_item, list);
        }

        class ViewHolder {
            @BindView(R.id.item_name) TextView itemName;
            @BindView(R.id.item_price) TextView itemPrice;
            @BindView(R.id.item_quantity) TextView itemQuantity;
            @BindView(R.id.item_exprice) TextView itemExprice;
            @BindView(R.id.item_attrs) TextView itemAttrs;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.fragment_order_list_item, parent, false);
                ViewHolder holder = new ViewHolder();
                ButterKnife.bind(holder, convertView);
                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder)convertView.getTag();
            TicketFood item = (TicketFood)getItem(position);
            holder.itemName.setText(item.getFoodName());
            holder.itemPrice.setText(String.format("$%.2f", item.getPrice()));
            holder.itemQuantity.setText(String.format("x%d(%d)", item.getQuantity(), item.getFulfilled()));
            holder.itemExprice.setText(String.format("$%.2f",item.getExPrice()));

            List<TicketFoodAttr> ticketFoodAttrList = item.getAttr();
            if(ticketFoodAttrList.size() > 0) {
                String[] attrs = new String[ticketFoodAttrList.size()];
                for(int i = 0; i < ticketFoodAttrList.size(); i++) {
                    TicketFoodAttr ticketFoodAttr = ticketFoodAttrList.get(i);
                    attrs[i] = String.format("%s: %s", ticketFoodAttr.getName(), ticketFoodAttr.getValue());
                }
                holder.itemAttrs.setText(TextUtils.join(", ", attrs));
            } else {
                holder.itemAttrs.setText("");
            }

            return convertView;
        }

    }

}
