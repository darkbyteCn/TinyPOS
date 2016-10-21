package com.tinyappsdev.tinypos.ui.OrderFragments;


import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
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
import android.widget.AdapterView;
import android.widget.Button;
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
import com.tinyappsdev.tinypos.ui.Helper.OrderShareContext;
import com.tinyappsdev.tinypos.ui.OrderActivity;
import com.tinyappsdev.tinypos.ui.OrderActivityInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class OrderMenuFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener
{

    private OrderActivityInterface mOrderActivity;
    private MyAdapter mMyAdapter;

    private Stack<Long> mCategoryIdStack;

    @BindView(R.id.order_menu_gridview) GridView mGridView;
    @BindView(R.id.search_box_layout) View mSearchBoxLayout;
    @BindView(R.id.menu_toolbar_layout) View mMenuToolbarLayout;
    @BindView(R.id.search_close_btn) Button mSearchCloseBtn;

    private Unbinder mUnbinder;

    private DataSetObserver mFoodObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            mMyAdapter.notifyDataSetChanged();
        }
    };

    public OrderMenuFragment() {

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

        view.findViewById(R.id.menu_go_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCategoryIdStack.size() == 0) return;
                mCategoryIdStack.pop();
                loadCategory(mCategoryIdStack.size() == 0 ? 0: mCategoryIdStack.peek());
            }
        });

        view.findViewById(R.id.menu_go_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCategoryIdStack.size() == 0) return;
                mCategoryIdStack.clear();
                loadCategory(0);
            }
        });

        view.findViewById(R.id.menu_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenuToolbarLayout.setVisibility(View.INVISIBLE);
                mSearchBoxLayout.setVisibility(View.VISIBLE);
            }
        });

        mSearchCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenuToolbarLayout.setVisibility(View.VISIBLE);
                mSearchBoxLayout.setVisibility(View.GONE);
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

        mOrderActivity.registerObserverForFood(mFoodObserver);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mOrderActivity.unregisterObserverForFood(mFoodObserver);
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
            int attrChoiceRequired = 0;
            List<TicketFoodAttr> ticketFoodAttrList = new ArrayList<TicketFoodAttr>();
            for (FoodAttrGroup group: attrGroupList) {
                if(group.getAttr().size() <= 1) continue;

                FoodAttr attr = group.getAttr().get(0);
                price += attr.getPriceDiff();
                attrChoiceRequired++;

                TicketFoodAttr ticketFoodAttr = new TicketFoodAttr();
                ticketFoodAttr.setName(group.getName());
                ticketFoodAttr.setValue(attr.getName());
                ticketFoodAttrList.add(ticketFoodAttr);
            }

            String foodName = food.getFoodName();
            TicketFood foodItem = new TicketFood();
            foodItem.setId(foodId);
            foodItem.setFoodName(foodName);
            foodItem.setQuantity(1);
            foodItem.setPrice(price);
            foodItem.setExPrice(price * 1);

            if(attrChoiceRequired > 0) {
                mOrderActivity.openFoodDetailWnd(foodId, -1);
                return;
            }

            List<TicketFood> foodItems = mOrderActivity.getTicket().getFoodItems();
            foodItems.add(foodItem);

            Map<Long, Integer> foodCountById = mOrderActivity.getFoodMap();
            if(foodCountById.containsKey(foodId))
                foodCountById.put(foodId, foodCountById.get(foodId) + 1);
            else
                foodCountById.put(foodId, 1);

            Toast.makeText(
                    getContext().getApplicationContext(),
                    "Added " + foodName,
                    Toast.LENGTH_SHORT
            ).show();

            mOrderActivity.notifyChangedForFood();
            mOrderActivity.notifyChangedForTicket();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor cursor = (Cursor)mMyAdapter.getItem(i);
        if(cursor == null) return false;

        long foodId = cursor.getLong(cursor.getColumnIndex(Menu.Schema.COL_FOODID));
        if(foodId == 0) return false;

        mOrderActivity.openFoodDetailWnd(foodId, -1);

        return true;
    }

    @Override
    public void onAttach(Context context) {
        Log.i("PKT", ">>>>>>>>onAttach");
        super.onAttach(context);

        if(context instanceof OrderActivityInterface)
            mOrderActivity = (OrderActivityInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOrderActivity = null;
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

            long foodId = cursor.getLong(cursor.getColumnIndex(Menu.Schema.COL_FOODID));
            Map<Long, Integer> foodMap = mOrderActivity.getFoodMap();

            holder.itemName.setText(cursor.getString(cursor.getColumnIndex(Menu.Schema.COL_MENUNAME)));
            holder.itemQuantity.setText(foodMap.containsKey(foodId) ? foodMap.get(foodId).toString() : "");
        }

    }

}
