package com.tinyappsdev.tinypos.ui.OrderFragment;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.Food;
import com.tinyappsdev.tinypos.data.FoodAttr;
import com.tinyappsdev.tinypos.data.FoodAttrGroup;
import com.tinyappsdev.tinypos.data.Menu;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.data.TicketFood;
import com.tinyappsdev.tinypos.data.TicketFoodAttr;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.OrderActivityInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class OrderMenuFragment extends BaseFragment<OrderActivityInterface> implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener
{
    private MyAdapter mMyAdapter;
    private Stack<Long> mCategoryIdStack;

    @BindView(R.id.order_menu_gridview) GridView mGridView;
    private Unbinder mUnbinder;

    public OrderMenuFragment() {

    }

    public static OrderMenuFragment newInstance() {
        OrderMenuFragment fragment = new OrderMenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onMessage(Message msg) {
        switch(msg.what) {
            case R.id.orderActivityOnTicketChange:
            case R.id.orderActivityOnTicketFoodChange: {
                mMyAdapter.notifyDataSetChanged();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        long[] ids = new long[mCategoryIdStack.size()];
        for(int i = 0; i < ids.length; i++)
            ids[i] = mCategoryIdStack.get(i);
        outState.putLongArray("categoryIdStack", ids);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_menu, container, false);
        mUnbinder = ButterKnife.bind(this, view);


        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCategoryIdStack.size() == 0) return;
                mCategoryIdStack.pop();
                loadCategory(mCategoryIdStack.size() == 0 ? 0: mCategoryIdStack.peek());
            }
        });

        mMyAdapter = new MyAdapter(this.getContext(), null, false);
        mGridView.setAdapter(mMyAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnItemLongClickListener(this);

        mCategoryIdStack = new Stack<Long>();
        if(savedInstanceState != null) {
            long[] ids = savedInstanceState.getLongArray("categoryIdStack");
            for(int i = 0; i < ids.length; i++)
                mCategoryIdStack.add(ids[i]);
        }
        Bundle bundle = new Bundle();
        bundle.putLong("categoryId", mCategoryIdStack.size() == 0 ? 0 : mCategoryIdStack.peek());
        getLoaderManager().initLoader(0, bundle, this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void loadCategory(long categoryId) {
        Bundle bundle = new Bundle();
        bundle.putLong("categoryId", categoryId);
        getLoaderManager().restartLoader(0, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Long categoryId = args.getLong("categoryId");
        String[] foodNames = Food.Schema.getColNames();
        String[] projects = new String[foodNames.length + 1];
        projects[0] = Menu.Schema.TABLE_NAME + ".*";
        for(int i = 0; i < foodNames.length; i++) projects[i + 1] = foodNames[i];

        return new CursorLoader(this.getContext().getApplicationContext(),
                ContentProviderEx.BuildUri(Menu.Schema.TABLE_NAME + "_" + Food.Schema.TABLE_NAME),
                projects,
                "categoryId=?",
                new String[] {categoryId + ""},
                null
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor cursor = (Cursor)mMyAdapter.getItem(i);
        if(cursor == null) return;

        Food food = ModelHelper.FoodFromCursor(cursor, Food.Schema.TABLE_NAME + "_");
        long foodId = food.getId();
        if(foodId == 0) {
            long categoryId = cursor.getLong(0);
            mCategoryIdStack.push(categoryId);
            loadCategory(categoryId);

        } else {
            double price = food.getPrice();
            List<FoodAttrGroup> attrGroupList = food.getAttrGroup();
            List<TicketFoodAttr> ticketFoodAttrList = new ArrayList<TicketFoodAttr>();
            if(attrGroupList != null) {
                for (FoodAttrGroup group : attrGroupList) {
                    if (group.getAttr().size() <= 1) continue;

                    FoodAttr attr = group.getAttr().get(0);
                    price += attr.getPriceDiff();

                    TicketFoodAttr ticketFoodAttr = new TicketFoodAttr();
                    ticketFoodAttr.setName(group.getName());
                    ticketFoodAttr.setValue(attr.getName());
                    ticketFoodAttrList.add(ticketFoodAttr);
                }
            }
            if(ticketFoodAttrList.size() > 0) {
                mActivity.openFoodDetailWnd(foodId, -1);
                return;
            }

            String foodName = food.getFoodName();
            TicketFood foodItem = new TicketFood();
            foodItem.setId(foodId);
            foodItem.setTaxRate(
                    food.getTaxable() != 0 ? mActivity.getConfigCache().getDouble("taxRate") : 0.0
            );
            foodItem.setFoodName(foodName);
            foodItem.setQuantity(1);
            foodItem.setPrice(price);
            foodItem.setExPrice(price * 1);
            foodItem.setAttr(ticketFoodAttrList);

            Toast.makeText(
                    getContext().getApplicationContext(),
                    String.format(getString(R.string.added_ticket_item), foodName),
                    Toast.LENGTH_SHORT
            ).show();

            mActivity.addFood(foodItem);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor cursor = (Cursor)mMyAdapter.getItem(i);
        if(cursor == null) return false;

        long foodId = cursor.getLong(cursor.getColumnIndex(Menu.Schema.COL_FOODID));
        if(foodId == 0) return false;

        mActivity.openFoodDetailWnd(foodId, -1);
        return true;
    }

    class MyAdapter extends CursorAdapter {

        public MyAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        class ViewHolder {
            @BindView(R.id.item_name) TextView itemName;
            @BindView(R.id.item_quantity) TextView itemQuantity;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_order_menu_item, parent, false);

            ViewHolder holder = new ViewHolder();
            ButterKnife.bind(holder, view);
            view.setTag(holder);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder)view.getTag();

            ModelHelper.MenuCursor menuCursor = new ModelHelper.MenuCursor(cursor);
            long foodId = menuCursor.getFoodId();
            Map<Long, Integer> foodMap = mActivity.getFoodMap();

            if(menuCursor.getFoodId() == 0) {
                ((CardView) view).setCardBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
                holder.itemQuantity.setText("");
            } else {
                ((CardView) view).setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.menuFoodItem));
                Integer count = foodMap.get(foodId);
                holder.itemQuantity.setText(count != null && count > 0 ? foodMap.get(foodId).toString() : "");
            }
            holder.itemName.setText(menuCursor.getMenuName());
        }

    }

}
