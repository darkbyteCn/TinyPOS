package com.tinyappsdev.tinypos.ui.CustomerFragment;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.Customer;
import com.tinyappsdev.tinypos.ui.BaseUI.LazyAdapter;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.CustomerActivityInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerSearchFragment extends BaseFragment<CustomerActivityInterface> {

    private RecyclerView mRecyclerView;
    private LazyRecyclerAdapter mAdapter;
    private String mQuery;
    private LocalContentObserver mLocalContentObserver;

    private final static Uri CUSTOMER_GETDOCS_URI = new Uri.Builder()
            .appendEncodedPath("Customer/getDocs")
            .appendQueryParameter("sortDirection", "-1")
            .build();

    public static CustomerSearchFragment newInstance() {
        Bundle args = new Bundle();

        CustomerSearchFragment fragment = new CustomerSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private class LocalContentObserver extends ContentObserver {
        public LocalContentObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if(mAdapter != null) {
                mAdapter.refresh();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalContentObserver = new LocalContentObserver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_search, container, false);

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.selectCustomer(0);
            }
        });

        getContext().getContentResolver().registerContentObserver(
                ContentProviderEx.BuildUri(Customer.Schema.TABLE_NAME), true, mLocalContentObserver
        );

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager =
                        (LinearLayoutManager)mRecyclerView.getLayoutManager();
                int total = linearLayoutManager.getItemCount();
                int last = linearLayoutManager.findLastVisibleItemPosition();

                if(mAdapter != null && total - last < 15) mAdapter.loadMore();
            }
        });
        mAdapter = new LazyRecyclerAdapter(
                this.getContext(),
                R.layout.fragment_customer_search_item,
                CUSTOMER_GETDOCS_URI
        );
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView.clearOnScrollListeners();
        getContext().getContentResolver().unregisterContentObserver(mLocalContentObserver);
    }

    @Override
    protected void onMessage(Message msg) {
        switch(msg.what) {
            case R.id.customerActivityOnSearchQueryChange: {
                loadCustomerList(mActivity.getSearchQuery());
                break;
            }
        }
    }

    protected void loadCustomerList(String query) {
        if (query == null || query.isEmpty()) {
            mQuery = null;
            mAdapter.setUri(CUSTOMER_GETDOCS_URI);

        } else if(!query.equals(mQuery)) {
            mQuery = query;
            Uri uri = new Uri.Builder()
                    .appendEncodedPath("Customer/search")
                    .appendQueryParameter("terms", query).build();
            mAdapter.setUri(uri);
            
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.customerName) TextView customerName;
        @BindView(R.id.customerAddress) TextView customerAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ApiPageResult {
        public int total;
        public Customer[] docs;
    }

    class LazyRecyclerAdapter extends LazyAdapter {

        public LazyRecyclerAdapter(Context context, int resourceId, Uri uri) {
            super(context, resourceId, uri, 25, ApiPageResult.class);
        }

        @Override
        public RecyclerView.ViewHolder createViewHolder(View view) {
            final ViewHolder viewHolder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Customer customer = (Customer)getItem(viewHolder.getAdapterPosition());
                    if(customer == null) return;
                    mActivity.selectCustomer(customer.getId());
                }
            });

            View btnSelect = view.findViewById(R.id.btn_select);
            if(mActivity.isResultNeeded()) {
                btnSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Customer customer = (Customer) getItem(viewHolder.getAdapterPosition());
                        if (customer == null) return;
                        mActivity.setResult(customer);
                    }
                });
                btnSelect.setVisibility(View.VISIBLE);
            } else {
                btnSelect.setVisibility(View.GONE);
            }

            return viewHolder;
        }

        @Override
        public void renderViewHolder(RecyclerView.ViewHolder holder, int position, Object data) {
            Customer customer = (Customer) data;
            ViewHolder viewHolder = (ViewHolder) holder;

            if (customer == null) {
                viewHolder.customerName.setText("");
                viewHolder.customerAddress.setText("");
            } else {
                List<String> addInfo = new ArrayList<String>();
                if (customer.getAddress() != null && !customer.getAddress().isEmpty())
                    addInfo.add(customer.getAddress());
                if (customer.getAddress2() != null && !customer.getAddress2().isEmpty())
                    addInfo.add(customer.getAddress2());
                if (customer.getCity() != null && !customer.getCity().isEmpty())
                    addInfo.add(customer.getCity());

                viewHolder.customerName.setText(
                        String.format(
                                getString(R.string.customer_search_fragment_item_name),
                                customer.getName(),
                                customer.getPhone()
                        )
                );
                viewHolder.customerAddress.setText(TextUtils.join(", ", addInfo));
            }
        }

        @Override
        protected PageResult parseResult(Object result) {
            PageResult pageResult = new PageResult();
            pageResult.rows = ((ApiPageResult)result).docs;
            pageResult.total = ((ApiPageResult)result).total;
            return pageResult;
        }
    }

}
