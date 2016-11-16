package com.tinyappsdev.tinypos.ui.TicketFragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketFood;
import com.tinyappsdev.tinypos.data.TicketFoodAttr;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.OrderActivityInterface;
import com.tinyappsdev.tinypos.ui.BaseUI.TicketActivityInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class TicketFoodFragment extends BaseFragment<TicketActivityInterface> {
    private MyAdapter mMyAdapter;

    @BindView(R.id.order_ticket_listview) ListView mListView;
    private Unbinder mUnbinder;

    public TicketFoodFragment() {
    }

    public static TicketFoodFragment newInstance() {
        TicketFoodFragment fragment = new TicketFoodFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    protected void updateUI() {
        Ticket ticket = mActivity.getTicket();
        List<TicketFood> curTicketFoodList = ticket.getFoodItems();
        if(curTicketFoodList == null) curTicketFoodList = new ArrayList();

        mMyAdapter = new MyAdapter(TicketFoodFragment.this.getContext(), curTicketFoodList);
        mListView.setAdapter(mMyAdapter);
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
        View view = inflater.inflate(R.layout.fragment_ticket_food, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        updateUI();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
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
