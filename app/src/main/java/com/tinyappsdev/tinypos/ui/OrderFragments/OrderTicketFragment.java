package com.tinyappsdev.tinypos.ui.OrderFragments;


import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketFood;
import com.tinyappsdev.tinypos.rest.ApiCall;
import com.tinyappsdev.tinypos.ui.Helper.OrderShareContext;
import com.tinyappsdev.tinypos.ui.OrderActivity;
import com.tinyappsdev.tinypos.ui.OrderActivityInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class OrderTicketFragment extends Fragment {

    private OrderActivityInterface mOrderActivity;
    private List<TicketFood> mTicketFoodList;
    private List<TicketFood> mTicketEmptyFoodList = new ArrayList<TicketFood>();
    private ApiCall mApiCall = ApiCall.getInstance();

    private MyAdapter mMyAdapter;

    @BindView(R.id.order_ticket_listview) ListView mListView;
    private Unbinder mUnbinder;

    private DataSetObserver mTicketObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();

            Ticket ticket = mOrderActivity.getTicket();
            List<TicketFood> curTicketFoodList = ticket != null
                    ? ticket.getFoodItems() : mTicketEmptyFoodList;
            if(mTicketFoodList != curTicketFoodList) {
                mTicketFoodList = curTicketFoodList;
                mMyAdapter = new MyAdapter(OrderTicketFragment.this.getContext(), mTicketFoodList);
                mListView.setAdapter(mMyAdapter);
            } else
                mMyAdapter.notifyDataSetChanged();
        }
    };

    public OrderTicketFragment() {

    }

    public static OrderTicketFragment newInstance() {
        OrderTicketFragment fragment = new OrderTicketFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_order_ticket, container, false);
        mUnbinder = ButterKnife.bind(this, view);


        view.findViewById(R.id.order_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOrder();
            }
        });

        view.findViewById(R.id.order_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkoutOrder();
            }
        });

        Ticket ticket = mOrderActivity.getTicket();
        mTicketFoodList = ticket != null ? ticket.getFoodItems() : mTicketEmptyFoodList;
        mMyAdapter = new MyAdapter(this.getContext(), mTicketFoodList);
        mListView.setAdapter(mMyAdapter);

        mOrderActivity.registerObserverForTicket(mTicketObserver);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mOrderActivity.unregisterObserverForTicket(mTicketObserver);
        mUnbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof OrderActivityInterface)
            mOrderActivity = (OrderActivityInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOrderActivity = null;
    }

    static class MyAdapter extends ArrayAdapter {

        public MyAdapter(Context context, List list) {
            super(context, R.layout.fragment_order_ticket_item, list);
        }

        class ViewHolder {
            @BindView(R.id.item_name) TextView itemName;
            @BindView(R.id.item_price) TextView itemPrice;
            @BindView(R.id.item_quantity) TextView itemQuantity;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.fragment_order_ticket_item, parent, false);
                ViewHolder holder = new ViewHolder();
                ButterKnife.bind(holder, convertView);
                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder)convertView.getTag();
            TicketFood item = (TicketFood)getItem(position);
            holder.itemName.setText(item.getFoodName());
            holder.itemPrice.setText(String.format("%.2f", item.getPrice()));
            holder.itemQuantity.setText(String.valueOf(item.getQuantity()));

            return convertView;
        }

    }

    protected void checkoutOrder() {
        try {
            Ticket ticket = mOrderActivity.getTicket();
            if(ticket == null) return;

            Map<String, Long> map = new HashMap<String, Long>();
            map.put("ticketId", ticket.getId());
            mApiCall.callApiAsync(
                    "Ticket/checkout",
                    (new ObjectMapper()).writeValueAsString(map),
                    new ApiCall.ApiCallbacks() {
                        @Override
                        public void onApiResponse(String error, String json) {
                            Log.i("PKT", String.format(">>>>>>> %s, %s", error, json));
                        }
                    }
            );
            getActivity().finish();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    protected void saveOrder() {
        try {
            Ticket ticket = mOrderActivity.getTicket();
            if(ticket == null) return;

            mApiCall.callApiAsync(
                    ticket.getId() != 0 ? "Ticket/updateDoc" : "Ticket/newDoc",
                    (new ObjectMapper()).writeValueAsString(ticket),
                    new ApiCall.ApiCallbacks() {
                        @Override
                        public void onApiResponse(String error, String json) {
                            Log.i("PKT", String.format(">>>>>>> %s, %s", error, json));
                        }
                    }
            );
            getActivity().finish();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
