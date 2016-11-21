package com.tinyappsdev.tinypos.ui.TicketFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.LazyAdapter;
import com.tinyappsdev.tinypos.ui.BaseUI.TicketActivityInterface;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TicketSearchFragment extends BaseFragment<TicketActivityInterface> {

    private RecyclerView mRecyclerView;
    private LazyRecyclerAdapter mAdapter;
    private String mQuery;

    private final static Uri DEFAULT_GETDOCS_URI = new Uri.Builder()
            .appendEncodedPath("Ticket/getDocs")
            .appendQueryParameter("sortDirection", "-1")
            .build();


    public static TicketSearchFragment newInstance() {
        Bundle args = new Bundle();

        TicketSearchFragment fragment = new TicketSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_search, container, false);

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
                R.layout.fragment_ticket_search_item,
                DEFAULT_GETDOCS_URI
        );
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    protected void onMessage(Message msg) {
        switch(msg.what) {
            case R.id.ticketActivityOnSearchQueryChange: {
                loadList(mActivity.getSearchQuery());
                break;
            }
        }
    }

    protected void loadList(String query) {
        if (query == null || query.isEmpty()) {
            mQuery = null;
            mAdapter.setUri(DEFAULT_GETDOCS_URI);

        } else if(!query.equals(mQuery)) {
            mQuery = query;
            Uri uri = new Uri.Builder()
                    .appendEncodedPath("Ticket/search")
                    .appendQueryParameter("terms", query).build();
            mAdapter.setUri(uri);

        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ticketId) TextView ticketId;
        @BindView(R.id.ticketType) TextView ticketType;
        @BindView(R.id.ticketQuantity) TextView ticketQuantity;
        @BindView(R.id.ticketCustomerInfo) TextView ticketCustomerInfo;
        @BindView(R.id.ticketAdditionalInfo) TextView ticketAdditionalInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ApiPageResult {
        int total;
        Ticket[] docs;
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
                    Ticket ticket = (Ticket)getItem(viewHolder.getAdapterPosition());
                    if(ticket == null) return;
                    mActivity.selectTicket(ticket.getId());
                }
            });
            return viewHolder;
        }

        @Override
        public void renderViewHolder(RecyclerView.ViewHolder _holder, int position, Object data) {
            Ticket ticket = (Ticket) data;
            ViewHolder holder = (ViewHolder) _holder;

            if(data == null) {
                holder.ticketId.setText("");
                holder.ticketType.setText("");
                holder.ticketQuantity.setText("");
                holder.ticketCustomerInfo.setText("");
                holder.ticketAdditionalInfo.setText("");
            } else {
                holder.ticketId.setText(ticket.getId() + "");

                if(ticket.getTableId() >= 0)
                    holder.ticketType.setText(getString(R.string.dine_in));
                else if(ticket.getTableId() == -1)
                    holder.ticketType.setText(getString(R.string.to_go));
                else if(ticket.getTableId() == -2)
                    holder.ticketType.setText(getString(R.string.delivery));

                if(ticket.getCustomer() == null)
                    holder.ticketCustomerInfo.setVisibility(View.GONE);
                else {
                    holder.ticketCustomerInfo.setText(String.format(
                            getString(R.string.format_ticket_customer_name_and_phone),
                            ticket.getCustomer().getName(),
                            ticket.getCustomer().getPhone()
                    ));
                    holder.ticketCustomerInfo.setVisibility(View.VISIBLE);
                }

                holder.ticketQuantity.setText(String.format(
                        getString(R.string.format_ticket_fulfilled_food_status),
                        ticket.getNumFoodFullfilled(),
                        ticket.getNumFood()
                ));

                holder.ticketAdditionalInfo.setText(String.format(
                        getString(R.string.format_ticket_time_by_waiter),
                        DateUtils.getRelativeTimeSpanString(ticket.getCreatedTime()),
                        ticket.getEmployeeName()
                ));

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
