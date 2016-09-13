package com.tinyappsdev.tinypos.OrderFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;


public class DineInFragment extends Fragment {

    private GridView mGridView;

    private OnFragmentInteractionListener mListener;

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
        mGridView.setAdapter(new MyAdapter(this.getContext()));

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class MyAdapter extends ArrayAdapter<Object> {

        public MyAdapter(Context context) {
            super(context, R.layout.fragment_dine_in_item, new Object[120]);
        }


        class ViewHolder {
            TextView tableId;
            TextView tableWaiter;
            TextView tableCapacity;
            TextView tableItemsCount;
            TextView tableTime;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.fragment_dine_in_item, parent, false);

                ViewHolder holder = new ViewHolder();
                holder.tableId = (TextView)convertView.findViewById(R.id.table_id);
                holder.tableWaiter = (TextView)convertView.findViewById(R.id.table_waiter);
                holder.tableCapacity = (TextView)convertView.findViewById(R.id.table_capacity);
                holder.tableItemsCount = (TextView)convertView.findViewById(R.id.table_items_count);
                holder.tableTime = (TextView)convertView.findViewById(R.id.table_time);
                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder)convertView.getTag();
            holder.tableId.setText(position + 1 + "");

            if(position != 1 && position != 5) {
                holder.tableId.setBackground(
                        this.getContext().getResources().getDrawable(R.drawable.dine_in_table_number)
                );

                holder.tableWaiter.setVisibility(View.INVISIBLE);
                holder.tableItemsCount.setVisibility(View.INVISIBLE);
                holder.tableTime.setVisibility(View.INVISIBLE);

                holder.tableCapacity.setText("6");
            } else {
                holder.tableId.setBackground(
                        this.getContext().getResources().getDrawable(R.drawable.dine_in_table_number_green)
                );

                holder.tableWaiter.setVisibility(View.VISIBLE);
                holder.tableItemsCount.setVisibility(View.VISIBLE);
                holder.tableTime.setVisibility(View.VISIBLE);

                holder.tableCapacity.setText("6/6");
            }

            return convertView;
        }

    }
}
