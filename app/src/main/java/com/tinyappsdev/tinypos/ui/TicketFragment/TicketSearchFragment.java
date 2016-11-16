package com.tinyappsdev.tinypos.ui.TicketFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import com.tinyappsdev.tinypos.data.Customer;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.CustomerActivityInterface;
import com.tinyappsdev.tinypos.ui.BaseUI.LazyAdapter;
import com.tinyappsdev.tinypos.ui.BaseUI.TicketActivityInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TicketSearchFragment extends BaseFragment<TicketActivityInterface> {

    private RecyclerView mRecyclerView;
    private LazyRecyclerAdapter mAdapter;

    private final static Uri DEFAULT_GETDOCS_URI = new Uri.Builder()
            .appendEncodedPath("Ticket/getDocs").build();


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
        Log.i("PKT", ">>>loadList" + query);

        if (query == null || query.isEmpty()) {
            mAdapter.setUri(DEFAULT_GETDOCS_URI);
        } else {
            Uri uri = new Uri.Builder()
                    .appendEncodedPath("Ticket/search")
                    .appendQueryParameter("terms", query).build();
            mAdapter.setUri(uri);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ticketId) TextView ticketId;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ApiResponse {
        int total;
        List<Ticket> docs;
    }

    class LazyRecyclerAdapter extends LazyAdapter {
        public LazyRecyclerAdapter(Context context, int resourceId, Uri uri) {
            super(context, resourceId, uri);
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
        public void renderViewHolder(RecyclerView.ViewHolder holder, int position, Object data) {
            Ticket ticket = (Ticket) data;
            ViewHolder viewHolder = (ViewHolder) holder;

            if(data == null) {
                viewHolder.ticketId.setText("");
            } else {
                viewHolder.ticketId.setText(ticket.getId() + "");
            }
        }

        @Override
        public PageResult parseResult(String json) {
            ApiResponse response = ModelHelper.fromJson(json, ApiResponse.class);
            if(response == null) return null;

            PageResult result = new PageResult();
            result.rows = response.docs.toArray(new Object[response.docs.size()]);
            result.total = response.total;
            return result;
        }
    }

}
