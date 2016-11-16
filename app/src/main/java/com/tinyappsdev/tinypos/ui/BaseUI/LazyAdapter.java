package com.tinyappsdev.tinypos.ui.BaseUI;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinyappsdev.tinypos.rest.ApiCall;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class LazyAdapter extends RecyclerView.Adapter {
    static class PageCache {
        Call requestCall;
        Object[] rows;
    }

    public static class PageResult {
        public int total;
        public Object[] rows;
    }

    protected ApiCall mApiCall = ApiCall.getInstance();
    protected Context mContext;
    protected int mTotal;
    protected int mPageSize;
    protected Map<Integer, PageCache> mCache;
    protected int mResourceId;
    protected Uri mUri;

    public LazyAdapter(Context context, int resourceId, Uri uri) {
        mUri = uri;
        mResourceId = resourceId;
        mContext = context;
        mPageSize = 50;
        newCache(1);
    }

    public void newCache(int total) {
        mTotal = total;
        mCache = new HashMap<Integer, PageCache>();
    }

    public void setUri(Uri uri) {
        if(uri == mUri) return;

        mUri = uri;
        newCache(1);
        notifyDataSetChanged();
    }

    public RecyclerView.ViewHolder createViewHolder(View view) {
        return null;
    }

    public void renderViewHolder(RecyclerView.ViewHolder holder, int position, Object data) {

    }

    public PageResult parseResult(String json) {
        return null;
    }

    public void loadMore() {
        if((mTotal % mPageSize) != 0) return;

        int pageIdx = mTotal / mPageSize;
        PageCache pageCache = mCache.get(pageIdx);
        if(pageCache == null || pageCache.requestCall == null && pageCache.rows == null) {
            requestPage(pageIdx);
        }
    }

    public void requestPage(final int pageIdx) {
        PageCache pageCache = mCache.get(pageIdx);
        if(pageCache == null) {
            pageCache = new PageCache();
            mCache.put(pageIdx, pageCache);
        }

        final PageCache _pageCache = pageCache;
        Uri uri = mUri.buildUpon()
                .appendQueryParameter("limit", String.valueOf(mPageSize))
                .appendQueryParameter("skip", String.valueOf(pageIdx * mPageSize))
                .build();
        pageCache.requestCall = mApiCall.callApiAsync(uri.toString(), null, new ApiCall.ApiCallbacks() {
            @Override
            public void onApiResponse(String error, String json) {
                try {
                    if(_pageCache != mCache.get(pageIdx)) return;
                    if(error != null) return;
                    PageResult result = parseResult(json);
                    if(result == null) return;

                    if(result.total < 0 || result.total == mTotal) {
                        int lastPageIdx = (mTotal - 1) / mPageSize;
                        if(pageIdx <= lastPageIdx || pageIdx == lastPageIdx + 1 && (mTotal % mPageSize) == 0) {
                            if(result.rows.length != mPageSize || pageIdx >= lastPageIdx)
                                mTotal = pageIdx * mPageSize + result.rows.length;

                            _pageCache.rows = result.rows;
                            notifyDataSetChanged();
                        }
                    } else {
                        newCache(result.total);
                        PageCache pageCache = new PageCache();
                        pageCache.rows = result.rows;
                        mCache.put(pageIdx, pageCache);
                        notifyDataSetChanged();
                    }

                } finally {
                    _pageCache.requestCall = null;
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mResourceId, parent, false);
        return createViewHolder(view);
    }

    public Object getItem(int position) {
        if(position < 0 || position >= mTotal) return null;

        int pageIdx = position / mPageSize;
        int rowIdx = position % mPageSize;
        PageCache pageCache = mCache.get(pageIdx);
        if(pageCache == null || pageCache.rows == null) return null;

        return pageCache.rows[rowIdx];
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object data = null;
        Log.i("PKT", ">>>>" + position + "-" + mTotal);
        if(position >= 0 && position < mTotal) {
            int pageIdx = position / mPageSize;
            int rowIdx = position % mPageSize;
            PageCache pageCache = mCache.get(pageIdx);
            if(pageCache == null || pageCache.requestCall == null && pageCache.rows == null) {
                requestPage(pageIdx);
            } else if(pageCache.rows != null) {
                data = pageCache.rows[rowIdx];
            }
        }

        renderViewHolder(holder, position, data);
    }

    @Override
    public int getItemCount() {
        return mTotal;
    }

}
