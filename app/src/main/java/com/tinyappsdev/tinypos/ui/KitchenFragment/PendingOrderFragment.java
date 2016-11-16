package com.tinyappsdev.tinypos.ui.KitchenFragment;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketFood;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.KitchenActivityInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PendingOrderFragment extends BaseFragment<KitchenActivityInterface> implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private ExpandableListView mExpandableListView;
    private MyAdapter mMyAdapter;
    private Map<Long, Map<Integer, Integer>> mFoodMarker;

    public PendingOrderFragment() {

    }

    public static PendingOrderFragment newInstance() {
        PendingOrderFragment fragment = new PendingOrderFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_order, container, false);

        mExpandableListView = (ExpandableListView)view.findViewById(R.id.expandableListView);
        mMyAdapter = new MyAdapter(null, this.getContext(), false);
        mExpandableListView.setAdapter(mMyAdapter);

        view.findViewById(R.id.uncheckAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFoodMarker.clear();
                mMyAdapter.notifyDataSetChanged(false);
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

    public void fulfillFood() {
        mActivity.fulfillFood(mFoodMarker);
        mFoodMarker.clear();
        mMyAdapter.notifyDataSetChanged(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == 0) {
            return new CursorLoader(this.getContext(),
                    ContentProviderEx.BuildUri(Ticket.Schema.TABLE_NAME),
                    null,
                    String.format("(%s&%s)=0", Ticket.Schema.COL_STATE, Ticket.STATE_COMPLETED | Ticket.STATE_FULFILLED),
                    null,
                    "_id asc"
            );
        } else {
            return new CursorLoader(this.getContext(),
                    ContentProviderEx.BuildUri(TicketFood.Schema.TABLE_NAME),
                    null,
                    String.format("%s=%s", TicketFood.Schema.COL_TICKETID, args.getLong("_id")),
                    null,
                    String.format("%s asc", TicketFood.Schema.COL_ITEMID)
            );
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("PKT", ">>>>onLoadFinished >" + loader.getId());
        if(loader.getId() == 0)
            mMyAdapter.changeCursor(data);
        else
            if(mMyAdapter.getGroup(loader.getId() - 1) != null)
                mMyAdapter.setChildrenCursor(loader.getId() - 1, data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == 0)
            mMyAdapter.changeCursor(null);
        else
            if(mMyAdapter.getGroup(loader.getId() - 1) != null)
                mMyAdapter.setChildrenCursor(loader.getId() - 1, null);
    }

    class MyAdapter extends CursorTreeAdapter {

        public MyAdapter(Cursor cursor, Context context, boolean autoRequery) {
            super(cursor, context, autoRequery);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor cursor) {
            ModelHelper.TicketCursor ticketCursor = new ModelHelper.TicketCursor(cursor);
            Bundle bundle = new Bundle();
            bundle.putLong("_id", ticketCursor.getId());
            getLoaderManager().restartLoader(cursor.getPosition() + 1, bundle, PendingOrderFragment.this);
            return null;
        }

        class GroupViewHolder {
            @BindView(R.id.ticket_id) TextView ticketId;
            @BindView(R.id.ticket_quantity) TextView ticketQuantity;
            @BindView(R.id.checkBox) CheckBox ticketCheckbox;
            int position;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        protected View newGroupView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_pending_order_group_item, viewGroup, false);
            final GroupViewHolder holder = new GroupViewHolder();
            ButterKnife.bind(holder, view);
            view.setTag(holder);

            holder.ticketCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Cursor cursor = getGroup(holder.position);
                    ModelHelper.TicketCursor ticket = new ModelHelper.TicketCursor(cursor);

                    List<TicketFood> ticketFoodList = ticket.getFoodItems();
                    if(holder.ticketCheckbox.isChecked()) {
                        Map<Integer, Integer> subMarker = new HashMap();
                        mFoodMarker.put(ticket.getId(), subMarker);
                        if(ticketFoodList != null) {
                            for(TicketFood ticketFood : ticketFoodList)
                                subMarker.put(
                                        ticketFood.getItemId(),
                                        ticketFood.getQuantity() - ticketFood.getFulfilled()
                                );
                        }
                    } else {
                        mFoodMarker.remove(ticket.getId());
                    }
                    notifyDataSetChanged(false);
                }
            });

            return view;
        }

        @Override
        protected void bindGroupView(View view, Context context, Cursor cursor, boolean b) {
            GroupViewHolder holder = (GroupViewHolder)view.getTag();
            holder.position = cursor.getPosition();
            ModelHelper.TicketCursor ticket = new ModelHelper.TicketCursor(cursor);

            holder.ticketId.setText(ticket.getId() + "");
            holder.ticketQuantity.setText(ticket.getNumFoodFullfilled() + "/" + ticket.getNumFood());

            Map<Integer, Integer> subMarker = mFoodMarker.get(ticket.getId());
            if(subMarker == null || subMarker.size() == 0) {
                holder.ticketCheckbox.setChecked(false);
                holder.ticketCheckbox.setText("");
            } else {
                int totalQty = 0;
                for(Integer qty : subMarker.values())
                    totalQty += qty;
                holder.ticketCheckbox.setChecked(true);
                holder.ticketCheckbox.setText("x" + totalQty);
            }
        }

        class ChildView {
            @BindView(R.id.ticketFoodSeq) TextView ticketFoodSeq;
            @BindView(R.id.ticketFoodName) TextView ticketFoodName;
            @BindView(R.id.checkBox) CheckBox ticketFoodCheckbox;
            int groupPosition;
            int childPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return (groupPosition << 16) | childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
            ChildView holder = (ChildView)view.getTag();
            holder.groupPosition = groupPosition;
            holder.childPosition = childPosition;
            return view;
        }

        @Override
        protected View newChildView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_pending_order_child_item, viewGroup, false);
            final ChildView holder = new ChildView();
            ButterKnife.bind(holder, view);
            view.setTag(holder);

            holder.ticketFoodCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Cursor cursor = getChild(holder.groupPosition, holder.childPosition);
                    ModelHelper.TicketFoodCursor ticketFood = new ModelHelper.TicketFoodCursor(cursor);

                    Map<Integer, Integer> subMarker = mFoodMarker.get(ticketFood.getTicketId());
                    if(holder.ticketFoodCheckbox.isChecked()) {
                        if(subMarker == null) {
                            subMarker = new HashMap();
                            mFoodMarker.put(ticketFood.getTicketId(), subMarker);
                        }
                        int qty = ticketFood.getQuantity() - ticketFood.getFulfilled();
                        subMarker.put(ticketFood.getItemId(), qty);
                    } else {
                        if(subMarker != null) subMarker.remove(ticketFood.getItemId());
                    }

                    notifyDataSetChanged(false);
                }
            });

            return view;
        }

        @Override
        protected void bindChildView(View view, Context context, Cursor cursor, boolean b) {
            ChildView holder = (ChildView)view.getTag();
            ModelHelper.TicketFoodCursor ticketFood = new ModelHelper.TicketFoodCursor(cursor);

            holder.ticketFoodSeq.setText("#" + ticketFood.getItemId());
            holder.ticketFoodName.setText(ticketFood.getFoodName());

            Map<Integer, Integer> subMarker = mFoodMarker.get(ticketFood.getTicketId());
            Integer qty = subMarker == null || subMarker.size() == 0
                    ? null : subMarker.get(ticketFood.getItemId());
            if(qty == null) {
                holder.ticketFoodCheckbox.setChecked(false);
                holder.ticketFoodCheckbox.setText("");
            } else {
                holder.ticketFoodCheckbox.setChecked(true);
                holder.ticketFoodCheckbox.setText("x" + qty);
            }
        }
    }

}
