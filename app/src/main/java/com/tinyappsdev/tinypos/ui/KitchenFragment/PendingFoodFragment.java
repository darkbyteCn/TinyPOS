package com.tinyappsdev.tinypos.ui.KitchenFragment;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.data.TicketFood;
import com.tinyappsdev.tinypos.data.TicketFoodAttr;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.KitchenActivityInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PendingFoodFragment extends BaseFragment<KitchenActivityInterface> implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private ListView mListView;
    private MyAdapter mMyAdapter;
    private Map<Long, Map<Integer, Integer>> mFoodMarker;

    public PendingFoodFragment() {

    }

    public static PendingFoodFragment newInstance() {
        PendingFoodFragment fragment = new PendingFoodFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        mFoodMarker = new HashMap();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_food, container, false);

        mListView = (ListView) view.findViewById(R.id.listview);
        mMyAdapter = new MyAdapter(this.getContext(), null, false);
        mListView.setAdapter(mMyAdapter);


        view.findViewById(R.id.uncheckAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFoodMarker.clear();
                mMyAdapter.notifyDataSetChanged();
            }
        });

        view.findViewById(R.id.fulfill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fulfillFood();
            }
        });


        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this.getContext(),
                ContentProviderEx.BuildUri(TicketFood.Schema.TABLE_NAME),
                null,
                null,
                null,
                String.format(
                        "%s asc,%s asc,%s asc",
                        TicketFood.Schema.COL_CREATEDTIME,
                        TicketFood.Schema.COL_TICKETID,
                        TicketFood.Schema.COL_ITEMID
                )
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMyAdapter.changeCursor(data);
    }

    public void fulfillFood() {
        mActivity.fulfillFood(mFoodMarker);
        mFoodMarker.clear();
        mMyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMyAdapter.changeCursor(null);
    }

    class MyAdapter extends CursorAdapter {

        public MyAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        class ViewHolder {
            @BindView(R.id.item_seq) TextView itemSeq;
            @BindView(R.id.item_quantity) TextView itemQuantity;
            @BindView(R.id.item_name) TextView itemName;
            @BindView(R.id.item_attrs) TextView itemAttrs;
            @BindView(R.id.item_elaspedTime) TextView itemElaspedTime;
            @BindView(R.id.checkBox) CheckBox itemcheckBox;
            int position;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_pending_food_item, parent, false);
            final ViewHolder holder = new ViewHolder();
            ButterKnife.bind(holder, view);
            view.setTag(holder);

            holder.itemcheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Cursor cursor = (Cursor)getItem(holder.position);
                    ModelHelper.TicketFoodCursor ticketFood = new ModelHelper.TicketFoodCursor(cursor);
                    Map<Integer, Integer> subMarker = mFoodMarker.get(ticketFood.getTicketId());
                    if(holder.itemcheckBox.isChecked()) {
                        if(subMarker == null) {
                            subMarker = new HashMap();
                            mFoodMarker.put(ticketFood.getTicketId(), subMarker);
                        }
                        int qty = ticketFood.getQuantity() - ticketFood.getFulfilled();
                        subMarker.put(ticketFood.getItemId(), qty);
                        holder.itemcheckBox.setText("x" + qty);
                    } else {
                        if(subMarker != null) subMarker.remove(ticketFood.getItemId());
                        holder.itemcheckBox.setText("");
                    }
                }
            });

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder)view.getTag();
            holder.position = cursor.getPosition();
            ModelHelper.TicketFoodCursor ticketFood = new ModelHelper.TicketFoodCursor(cursor);

            holder.itemSeq.setText(ticketFood.getTicketId() + "-" + ticketFood.getItemId());
            holder.itemQuantity.setText("x" + ticketFood.getQuantity());
            holder.itemName.setText(ticketFood.getFoodName());

            List<TicketFoodAttr> ticketFoodAttrList = ticketFood.getAttr();
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

            holder.itemElaspedTime.setText(DateUtils.getRelativeTimeSpanString(ticketFood.getCreatedTime()));

            Map<Integer, Integer> subMarker = mFoodMarker.get(ticketFood.getTicketId());
            Integer qty = subMarker == null || subMarker.size() == 0
                    ? null : subMarker.get(ticketFood.getItemId());
            if(qty == null || qty == 0) {
                holder.itemcheckBox.setChecked(false);
                holder.itemcheckBox.setText("");
            } else {
                holder.itemcheckBox.setChecked(true);
                holder.itemcheckBox.setText("x" + qty);
            }
        }

    }

}
