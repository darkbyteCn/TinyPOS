package com.tinyappsdev.tinypos.ui.OrderFragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.Food;
import com.tinyappsdev.tinypos.data.FoodAttr;
import com.tinyappsdev.tinypos.data.FoodAttrGroup;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketFood;
import com.tinyappsdev.tinypos.data.TicketFoodAttr;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.OrderActivityInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FoodDetailFragment extends BaseFragment<OrderActivityInterface> implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Food mFood;
    private long mFoodId;
    private int mIndex = -1;
    List<RadioGroup> mRadioGroupList = new ArrayList<RadioGroup>();

    @BindView(R.id.food_title_txt) TextView mFoodTitleTxt;
    @BindView(R.id.quantityPicker) NumberPicker mQuantityPicker;
    @BindView(R.id.food_details_layout) LinearLayout mFoodDetailsLayout;
    @BindView(R.id.moreOptionsFAB) FloatingActionButton mMoreOptionsFAB;

    public FoodDetailFragment() {
    }

    public static FoodDetailFragment newInstance(long foodId, int index) {
        FoodDetailFragment fragment = new FoodDetailFragment();
        Bundle args = new Bundle();
        args.putLong("foodId", foodId);
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mIndex = arguments.getInt("index", -1);
            mFoodId = arguments.getLong("foodId", -1);
        }

        if(savedInstanceState != null)
            mFood = ModelHelper.fromJson(savedInstanceState.getString("mFood"), Food.class);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mFood", ModelHelper.toJson(mFood));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_detail, container, false);
        ButterKnife.bind(this, view);

        mQuantityPicker.setMinValue(1);
        mQuantityPicker.setMaxValue(50);

        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.closeFoodDetailWnd();
            }
        });

        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mIndex < 0)
                    addFood();
                else
                    changeFood();
            }
        });

        view.findViewById(R.id.moreOptionsFAB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFood();
            }
        });

        if(mIndex < 0) mMoreOptionsFAB.setVisibility(View.GONE);

        if(mFood == null) getLoaderManager().initLoader(0, null, this);

        return view;
    }

    protected void addFood() {
        if(mFood == null) return;

        int quantity = mQuantityPicker.getValue();
        Ticket ticket = mActivity.getTicket();
        TicketFood ticketFood = new TicketFood();
        ticketFood.setId(mFood.getId());
        ticketFood.setTaxRate(
                mFood.getTaxable() != 0 ? mActivity.getConfigCache().getDouble("taxRate") : 0.0
        );
        ticketFood.setFoodName(mFood.getFoodName());

        double price = mFood.getPrice();
        List<TicketFoodAttr> ticketFoodAttrList = new ArrayList<TicketFoodAttr>();
        for(int i = 0; i < mRadioGroupList.size(); i++) {
            RadioGroup radioGroup = mRadioGroupList.get(i);
            FoodAttrGroup foodAttrGroup = mFood.getAttrGroup().get(i);
            TicketFoodAttr ticketFoodAttr = new TicketFoodAttr();
            ticketFoodAttrList.add(ticketFoodAttr);
            ticketFoodAttr.setName(foodAttrGroup.getName());
            FoodAttr foodAttr = foodAttrGroup.getAttr().get(radioGroup.getCheckedRadioButtonId());
            ticketFoodAttr.setValue(foodAttr.getName());
            price += foodAttr.getPriceDiff();
        }

        ticketFood.setPrice(price);
        ticketFood.setQuantity(quantity);
        ticketFood.setExPrice(quantity * price);
        ticketFood.setAttr(ticketFoodAttrList);

        mActivity.addFood(ticketFood);
        Toast.makeText(
                getContext().getApplicationContext(),
                String.format(getString(R.string.added_ticket_item), ticketFood.getFoodName()),
                Toast.LENGTH_SHORT
        ).show();
        mActivity.closeFoodDetailWnd();
    }

    protected void changeFood() {
        if(mFood == null) return;

        int quantity = mQuantityPicker.getValue();
        Ticket ticket = mActivity.getTicket();
        TicketFood ticketFood = ticket.getFoodItems().get(mIndex);

        double price = mFood.getPrice();
        List<TicketFoodAttr> ticketFoodAttrList = new ArrayList<TicketFoodAttr>();
        if(ticketFood.getFulfilled() <= 0) {
            for (int i = 0; i < mRadioGroupList.size(); i++) {
                RadioGroup radioGroup = mRadioGroupList.get(i);
                FoodAttrGroup foodAttrGroup = mFood.getAttrGroup().get(i);
                TicketFoodAttr ticketFoodAttr = new TicketFoodAttr();
                ticketFoodAttrList.add(ticketFoodAttr);
                ticketFoodAttr.setName(foodAttrGroup.getName());
                FoodAttr foodAttr = foodAttrGroup.getAttr().get(radioGroup.getCheckedRadioButtonId());
                ticketFoodAttr.setValue(foodAttr.getName());
                price += foodAttr.getPriceDiff();
            }
        }

        mActivity.changeFood(mIndex, ticketFoodAttrList, quantity, price);
        Toast.makeText(
                getContext().getApplicationContext(),
                String.format(getString(R.string.changed_ticket_item), ticketFood.getFoodName()),
                Toast.LENGTH_SHORT
        ).show();
        mActivity.closeFoodDetailWnd();
    }

    protected void removeFood() {
        if(mFood == null) return;

        Ticket ticket = mActivity.getTicket();
        TicketFood ticketFood = ticket.getFoodItems().get(mIndex);

        mActivity.removeFood(mIndex);
        Toast.makeText(
                getContext().getApplicationContext(),
                String.format(getString(R.string.removed_ticket_item), ticketFood.getFoodName()),
                Toast.LENGTH_SHORT
        ).show();
        mActivity.closeFoodDetailWnd();
    }

    private void updateUI() {
        Ticket ticket = mActivity.getTicket();
        Map<String, String> attrMap = new HashMap<String, String>();

        if(mIndex >= 0) {
            TicketFood ticketFood = ticket.getFoodItems().get(mIndex);
            mQuantityPicker.setValue(ticketFood.getQuantity());
            if(ticketFood.getAttr() != null) {
                for (TicketFoodAttr foodAttr : ticketFood.getAttr())
                    attrMap.put(foodAttr.getName().toLowerCase(), foodAttr.getValue().toLowerCase());
            }
        } else {
            mQuantityPicker.setValue(1);
        }

        mFoodTitleTxt.setText(mFood.getFoodName());
        LayoutInflater layoutInflater = this.getActivity().getLayoutInflater();
        List<FoodAttrGroup> foodAttrGroupList = mFood.getAttrGroup();
        if(foodAttrGroupList == null) return;

        for(FoodAttrGroup group: foodAttrGroupList) {
            List<FoodAttr> foodAttrList = group.getAttr();
            if(foodAttrList.size() <= 1) continue;

            LinearLayout linearLayout = (LinearLayout)layoutInflater.inflate(
                    R.layout.menu_food_detail_radio_group,
                    mFoodDetailsLayout,
                    false
            );
            ((TextView)linearLayout.findViewById(R.id.menu_attr_group_name)).setText(group.getName());
            RadioGroup radioGroup = (RadioGroup)linearLayout.findViewById(R.id.menu_attr_group_rg);
            String attVal = attrMap.get(group.getName());
            int k = 0;
            for(int i = 0; i < foodAttrList.size(); i++) {
                FoodAttr foodAttr = foodAttrList.get(i);
                RadioButton radioButton = (RadioButton)layoutInflater.inflate(
                        R.layout.menu_food_detail_radio_button,
                        radioGroup,
                        false
                );
                radioButton.setId(i);
                radioButton.setSaveEnabled(false);
                if(i == 0)
                    radioButton.setText(foodAttr.getName());
                else
                    radioButton.setText(String.format(
                            getString(R.string.format_ticket_item_attr_with_price),
                            foodAttr.getName(),
                            foodAttr.getPriceDiff()
                    ));

                if(attVal != null && attVal.equals(foodAttr.getName().toLowerCase()))
                    k = i;

                radioGroup.addView(radioButton);
            }
            radioGroup.check(k);
            mRadioGroupList.add(radioGroup);
            mFoodDetailsLayout.addView(linearLayout);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRadioGroupList.clear();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this.getActivity().getApplicationContext(),
                ContentProviderEx.BuildUri(Food.Schema.TABLE_NAME, "" + mFoodId),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()) {
            mFood = ModelHelper.FoodFromCursor(cursor);
            updateUI();
        }
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
