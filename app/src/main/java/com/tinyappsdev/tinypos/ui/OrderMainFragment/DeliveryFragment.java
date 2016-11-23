package com.tinyappsdev.tinypos.ui.OrderMainFragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.Customer;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.OrderMainActivityInterface;
import com.tinyappsdev.tinypos.ui.OrderActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DeliveryFragment extends BaseFragment<OrderMainActivityInterface> implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    private GridView mGridView;
    private MyAdapter mMyAdapter;

    public DeliveryFragment() {
    }

    public static DeliveryFragment newInstance(int fragmentId) {
        DeliveryFragment fragment = new DeliveryFragment();
        Bundle args = new Bundle();
        args.putInt("fragmentId", fragmentId);
        fragment.setArguments(args);
        return fragment;
    }

    void openOrderWnd(long ticketId) {
        Intent intent = new Intent(this.getActivity(), OrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("tableId", -2);
        bundle.putLong("ticketId", ticketId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrderWnd(0);
            }
        });

        mGridView = (GridView)view.findViewById(R.id.delivery_gridview);
        mMyAdapter = new MyAdapter(this.getContext(), null, false);
        mGridView.setAdapter(mMyAdapter);
        mGridView.setOnItemClickListener(this);

        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this.getContext(),
                ContentProviderEx.BuildUri(Ticket.Schema.TABLE_NAME),
                null,
                String.format(
                        "%s=-2 and %s&%d=0",
                        Ticket.Schema.COL_TABLEID,
                        Ticket.Schema.COL_STATE,
                        Ticket.STATE_COMPLETED
                ),
                null,
                "_id desc"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMyAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMyAdapter.changeCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Cursor cursor = (Cursor)adapterView.getItemAtPosition(position);
        ModelHelper.TicketCursor ticketCursor = new ModelHelper.TicketCursor(cursor);

        openOrderWnd(ticketCursor.getId());
    }

    class MyAdapter extends CursorAdapter {

        public MyAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        class ViewHolder {
            @BindView(R.id.ticket_id) TextView ticketId;
            @BindView(R.id.ticket_customer) TextView ticketCustomer;
            @BindView(R.id.ticket_waiter) TextView ticketWaiter;
            @BindView(R.id.ticket_items_count) TextView ticketItemsCount;
            @BindView(R.id.ticket_elapsed_time) TextView ticketElapsedTime;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_delivery_item, parent, false);
            ViewHolder holder = new ViewHolder();
            ButterKnife.bind(holder, view);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder)view.getTag();
            ModelHelper.TicketCursor ticketCursor = new ModelHelper.TicketCursor(cursor);

            holder.ticketId.setText(String.valueOf(ticketCursor.getId()));
            holder.ticketElapsedTime.setText(DateUtils.getRelativeTimeSpanString(ticketCursor.getCreatedTime()));
            holder.ticketItemsCount.setText(String.format(
                    getString(R.string.format_ticket_fulfilled_food_status),
                    ticketCursor.getNumFoodFullfilled(),
                    ticketCursor.getNumFood()
            ));
            holder.ticketWaiter.setText(ticketCursor.getEmployeeName());

            Customer customer = ticketCursor.getCustomer();
            holder.ticketWaiter.setText(
                    ticketCursor.getEmployeeName() == null   ? "" : ticketCursor.getEmployeeName()
            );

            holder.ticketCustomer.setText(
                    customer == null || customer.getName() == null ? "" : customer.getName()
            );
        }

    }
}
