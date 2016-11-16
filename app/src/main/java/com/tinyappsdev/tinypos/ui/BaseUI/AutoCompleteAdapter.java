package com.tinyappsdev.tinypos.ui.BaseUI;

import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.List;

public abstract class AutoCompleteAdapter extends BaseAdapter implements Filterable {
    private List mList;

    public AutoCompleteAdapter() {
        super();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList == null || i < 0 || i >= mList.size() ? null : mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public abstract List doFilter(String text);

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                if(charSequence == null) return null;
                FilterResults filterResults = new FilterResults();
                filterResults.values = doFilter(charSequence.toString());
                filterResults.count = filterResults.values == null ? 0 : ((List)filterResults.values).size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mList = filterResults == null ? null : (List)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}

