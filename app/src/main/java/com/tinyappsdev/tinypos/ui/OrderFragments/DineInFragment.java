package com.tinyappsdev.tinypos.ui.OrderFragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.DineTable;
import com.tinyappsdev.tinypos.data.Food;
import com.tinyappsdev.tinypos.data.Menu;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.ui.OnFragmentIntComListener;
import com.tinyappsdev.tinypos.ui.OrderActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DineInFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    private GridView mGridView;
    private MyAdapter mMyAdapter;

    private OnFragmentIntComListener mListener;

    public DineInFragment() {
        // Required empty public constructor
    }

    public static DineInFragment newInstance() {
        DineInFragment fragment = new DineInFragment();
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
        View view = inflater.inflate(R.layout.fragment_dine_in, container, false);

        mGridView = (GridView)view.findViewById(R.id.dine_in_gridview);
        mMyAdapter = new MyAdapter(this.getContext(), null, false);
        mGridView.setAdapter(mMyAdapter);
        mGridView.setOnItemClickListener(this);

        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        Log.i("PKT", String.format(">>>>>onLoadFinished %d", data.getCount()));
        mMyAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i("PKT", ">>>>>onLoaderReset");
        mMyAdapter.changeCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Cursor cursor = (Cursor)adapterView.getItemAtPosition(position);
        ModelHelper.DineTableCursor dineTableCursor = new ModelHelper.DineTableCursor(cursor);

        Intent intent = new Intent(this.getActivity(), OrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("tableId", dineTableCursor.getId());
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
            long cts = System.currentTimeMillis() / 1000;
            long tableId = ticketCursor.getTableId();

            if(tableId > 0) {
                holder.tableWaiter.setVisibility(View.VISIBLE);
                holder.tableWaiter.setText(ticketCursor.getCustomerName());

                holder.tableItemsCount.setVisibility(View.VISIBLE);
                holder.tableItemsCount.setText(String.format("%d / %d",
                        ticketCursor.getNumFoodFullfilled(),
                        ticketCursor.getNumFood()
                ));

                holder.tableTime.setVisibility(View.VISIBLE);
                holder.tableTime.setText(DateUtils.getRelativeTimeSpanString(ticketCursor.getCreatedTime()));

                holder.tableCapacity.setText(String.format("%d / %d",
                        ticketCursor.getNumGuest(),
                        dineTableCursor.getMaxGuest()
                ));
            } else {
                holder.tableWaiter.setVisibility(View.INVISIBLE);
                holder.tableItemsCount.setVisibility(View.INVISIBLE);
                holder.tableTime.setVisibility(View.INVISIBLE);

                holder.tableCapacity.setText("" + dineTableCursor.getMaxGuest());
            }

            holder.tableId.setText(dineTableCursor.getName());

        }

    }
}
