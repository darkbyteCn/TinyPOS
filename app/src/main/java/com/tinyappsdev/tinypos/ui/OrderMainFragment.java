package com.tinyappsdev.tinypos.ui;


import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.widget.AdapterView;

/**
 * Created by pk on 10/22/2016.
 */
public class OrderMainFragment extends Fragment {
    protected int mFragmentId;
    protected OrderMainActivityInterface mOrderMainActivity;

    protected DataSetObserver mFragmentChangeObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            onFragmentChange();
        }
    };

    public void onFragmentChange() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFragmentId = getArguments().getInt("fragmentId", -1);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mOrderMainActivity.registerObserverForFragmentChange(mFragmentChangeObserver);
        if(mOrderMainActivity.getCurrentFragmentId() == mFragmentId)
            onFragmentChange();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mOrderMainActivity.unregisterObserverForFragmentChange(mFragmentChangeObserver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OrderMainActivityInterface)
            mOrderMainActivity = (OrderMainActivityInterface)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOrderMainActivity = null;
    }

}
