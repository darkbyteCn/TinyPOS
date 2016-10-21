package com.tinyappsdev.tinypos.ui.OrderFragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.ui.OnFragmentIntComListener;


public class ToGoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private GridView mGridView;
    private MyAdapter mMyAdapter;

    private OnFragmentIntComListener mListener;

    public ToGoFragment() {
        // Required empty public constructor
    }

    public static ToGoFragment newInstance() {
        ToGoFragment fragment = new ToGoFragment();
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
        View view = inflater.inflate(R.layout.fragment_to_go, container, false);

        mGridView = (GridView)view.findViewById(R.id.to_go_gridview);
        mMyAdapter = new MyAdapter(this.getContext(), null, false);
        mGridView.setAdapter(mMyAdapter);

        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentIntComListener) {
            mListener = (OnFragmentIntComListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentIntComListener");
        }*/

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this.getContext(),
                ContentProviderEx.BuildUri(Ticket.Schema.TABLE_NAME),
                null,
                String.format("%s=-1 and %s=1", Ticket.Schema.COL_TABLEID, Ticket.Schema.COL_STATE),
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("PKT", ">>>>>onLoadFinished");
        mMyAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i("PKT", ">>>>>onLoaderReset");
        mMyAdapter.changeCursor(null);
    }

    class MyAdapter extends CursorAdapter {

        public MyAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        class ViewHolder {
            TextView ticketId;
            TextView ticketWaiter;
            TextView ticketItemsCount;
            TextView ticketElapsedTime;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_to_go_item, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.ticketId = (TextView)view.findViewById(R.id.ticket_id);
            holder.ticketWaiter = (TextView)view.findViewById(R.id.ticket_waiter);
            holder.ticketItemsCount = (TextView)view.findViewById(R.id.ticket_items_count);
            holder.ticketElapsedTime = (TextView)view.findViewById(R.id.ticket_elapsed_time);
            view.setTag(holder);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder)view.getTag();

            //holder.tableWaiter.setVisibility(View.INVISIBLE);
            //holder.tableItemsCount.setVisibility(View.INVISIBLE);
            //holder.tableTime.setVisibility(View.INVISIBLE);

            holder.ticketId.setText(cursor.getString(cursor.getColumnIndex(Ticket.Schema.COL_ID)));
            //holder.tableCapacity.setText(cursor.getString(cursor.getColumnIndex(DineTable.Schema.COL_MAXGUEST)));

        }

    }
}
