package com.tinyappsdev.tinypos.OrderFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;


public class OrderMenuFragment extends Fragment {

    private GridView mGridView;

    public OrderMenuFragment() {
        // Required empty public constructor
    }

    public static OrderMenuFragment newInstance() {
        OrderMenuFragment fragment = new OrderMenuFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_menu, container, false);
        mGridView = (GridView)view.findViewById(R.id.order_menu_gridview);
        mGridView.setAdapter(new MyAdapter(this.getContext()));

        return view;
    }

    static class MyAdapter extends ArrayAdapter<Object> {

        public MyAdapter(Context context) {
            super(context, R.layout.fragment_order_menu_item, new Object[] {
                    "Appetizer",
                    "Combinations",
                    "Soup",
                    "Fried Rice"
            });
        }


        class ViewHolder {
            TextView itemName;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.fragment_order_menu_item, parent, false);

                ViewHolder holder = new ViewHolder();
                holder.itemName = (TextView)convertView.findViewById(R.id.item_name);
                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder)convertView.getTag();
            holder.itemName.setText((String)getItem(position));

            return convertView;
        }

    }

}
