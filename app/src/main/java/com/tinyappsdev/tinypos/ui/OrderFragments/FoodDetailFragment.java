package com.tinyappsdev.tinypos.ui.OrderFragments;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.Food;
import com.tinyappsdev.tinypos.data.FoodAttr;
import com.tinyappsdev.tinypos.data.FoodAttrGroup;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.ui.Helper.OrderShareContext;
import com.tinyappsdev.tinypos.ui.OrderActivity;
import com.tinyappsdev.tinypos.ui.OrderActivityInterface;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FoodDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private OrderActivityInterface mOrderActivity;

    private Food mFood;
    private long mFoodId;

    @BindView(R.id.food_title_txt) TextView mFoodTitleTxt;
    @BindView(R.id.food_details_layout) LinearLayout mFoodDetailsLayout;

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
            mFoodId = arguments.getLong("foodId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_detail, container, false);
        ButterKnife.bind(this, view);

        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    private void updateUI() {
        int index = getArguments().getInt("index");

        Ticket ticket = mOrderActivity.getTicket();

        mFoodTitleTxt.setText(mFood.getFoodName());
        LayoutInflater layoutInflater = this.getActivity().getLayoutInflater();
        List<FoodAttrGroup> foodAttrGroupList = mFood.getAttrGroup();
        for(FoodAttrGroup group: foodAttrGroupList) {
            List<FoodAttr> foodAttrList = group.getAttr();
            if(foodAttrList.size() <= 1) continue;

            LinearLayout linearLayout = (LinearLayout)layoutInflater.inflate(
                    R.layout.menu_food_detail_radio_group,
                    mFoodDetailsLayout,
                    false
            );
            ((TextView)linearLayout.findViewById(R.id.menu_attr_group_name)).setText(group.getName().toUpperCase());
            RadioGroup radioGroup = (RadioGroup)linearLayout.findViewById(R.id.menu_attr_group_rg);
            for(int i = 0; i < foodAttrList.size(); i++) {
                FoodAttr foodAttr = foodAttrList.get(i);
                RadioButton radioButton = (RadioButton)layoutInflater.inflate(
                        R.layout.menu_food_detail_radio_button,
                        radioGroup,
                        false
                );
                if(i == 0)
                    radioButton.setText(foodAttr.getName());
                else
                    radioButton.setText(String.format(
                            "%s(%+2.2f ea)",
                            foodAttr.getName(),
                            foodAttr.getPriceDiff()
                    ));
                radioGroup.addView(radioButton);
            }

            mFoodDetailsLayout.addView(linearLayout);
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof OrderActivity)
            mOrderActivity = (OrderActivityInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOrderActivity = null;
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
        if(!cursor.moveToFirst()) return;
        mFood = ModelHelper.FoodFromCursor(cursor);
        updateUI();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
