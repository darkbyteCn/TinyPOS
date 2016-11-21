package com.tinyappsdev.tinypos.ui.OrderMainFragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
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
import com.tinyappsdev.tinypos.data.DineTable;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.OrderMainActivityInterface;
import com.tinyappsdev.tinypos.ui.OrderActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DineInFragment extends BaseFragment<OrderMainActivityInterface> implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    private GridView mGridView;
    private MyAdapter mMyAdapter;

    public DineInFragment() {
    }

    public static DineInFragment newInstance(int fragmentId) {
        DineInFragment fragment = new DineInFragment();
        Bundle args = new Bundle();
        args.putInt("fragmentId", fragmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dine_in, container, false);

        mGridView = (GridView)view.findViewById(R.id.dine_in_gridview);
        mMyAdapter = new MyAdapter(this.getContext(), null, false);
        mGridView.setAdapter(mMyAdapter);
        mGridView.setOnItemClickListener(this);

        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] joinNames = Ticket.Schema.getColNames();
        String[] projects = new String[joinNames.length + 1];
        projects[0] = DineTable.Schema.TABLE_NAME + ".*";
        for(int i = 0; i < joinNames.length; i++) projects[i + 1] = joinNames[i];

        return new CursorLoader(this.getContext(),
                ContentProviderEx.BuildUri(DineTable.Schema.TABLE_NAME + "_" + Ticket.Schema.TABLE_NAME),
                projects, null, null, null
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
        ModelHelper.DineTableCursor dineTableCursor = new ModelHelper.DineTableCursor(cursor);

        Intent intent = new Intent(this.getActivity(), OrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("tableId", dineTableCursor.getId());
        bundle.putString("tableName", dineTableCursor.getName());
        bundle.putLong("ticketId", dineTableCursor.getTicketId());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    class MyAdapter extends CursorAdapter {

        public MyAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        class ViewHolder {
            @BindView(R.id.table_id) TextView tableId;
            @BindView(R.id.table_waiter) TextView tableWaiter;
            @BindView(R.id.table_capacity) TextView tableCapacity;
            @BindView(R.id.table_items_count) TextView tableItemsCount;
            @BindView(R.id.table_time) TextView tableTime;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_dine_in_item, parent, false);
            ViewHolder holder = new ViewHolder();
            ButterKnife.bind(holder, view);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder)view.getTag();

            ModelHelper.DineTableCursor dineTableCursor = new ModelHelper.DineTableCursor(cursor);
            ModelHelper.TicketCursor ticketCursor = new ModelHelper.TicketCursor(cursor, Ticket.Schema.TABLE_NAME + "_");

            if(dineTableCursor.getTicketId() > 0) {
                holder.tableWaiter.setVisibility(View.VISIBLE);
                holder.tableWaiter.setText(ticketCursor.getEmployeeName());

                holder.tableItemsCount.setVisibility(View.VISIBLE);
                holder.tableItemsCount.setText(String.format(
                        getString(R.string.format_ticket_fulfilled_food_status),
                        ticketCursor.getNumFoodFullfilled(),
                        ticketCursor.getNumFood()
                ));

                holder.tableTime.setVisibility(View.VISIBLE);
                holder.tableTime.setText(DateUtils.getRelativeTimeSpanString(ticketCursor.getCreatedTime()));

                holder.tableCapacity.setText(String.format(
                        getString(R.string.format_ticket_guest_status),
                        ticketCursor.getNumGuest(),
                        dineTableCursor.getMaxGuest()
                ));

                Drawable drawable = ContextCompat.getDrawable(
                        getContext(),
                        R.drawable.dine_in_table_number_occupied
                );
                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    holder.tableId.setBackground(drawable);
                else
                    holder.tableId.setBackgroundDrawable(drawable);
            } else {
                holder.tableWaiter.setVisibility(View.INVISIBLE);
                holder.tableItemsCount.setVisibility(View.INVISIBLE);
                holder.tableTime.setVisibility(View.INVISIBLE);

                holder.tableCapacity.setText("" + dineTableCursor.getMaxGuest());

                Drawable drawable = ContextCompat.getDrawable(
                        getContext(),
                        R.drawable.dine_in_table_number
                );
                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    holder.tableId.setBackground(drawable);
                else
                    holder.tableId.setBackgroundDrawable(drawable);
            }

            holder.tableId.setText(dineTableCursor.getName());

        }

    }
}
