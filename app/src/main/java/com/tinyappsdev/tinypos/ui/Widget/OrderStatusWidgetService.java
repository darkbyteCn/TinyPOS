package com.tinyappsdev.tinypos.ui.Widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.Ticket;

public class OrderStatusWidgetService extends RemoteViewsService {

    static final String TAG = OrderStatusWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            Cursor mCursor;

            @Override
            public void onCreate() {
            }

            @Override
            public int getCount() {
                return mCursor == null ? 0 : mCursor.getCount();
            }

            @Override
            public void onDataSetChanged() {
                if(mCursor != null) mCursor.close();

                final long identityToken = Binder.clearCallingIdentity();
                mCursor = getContentResolver().query(
                        ContentProviderEx.BuildUri(Ticket.Schema.TABLE_NAME),
                        new String[] {
                                String.format("min(0, %s) as ticketType", Ticket.Schema.COL_TABLEID),
                                "count(*) as totalCount"
                        },
                        String.format(
                                "(%s&%s)=0 group by ticketType",
                                Ticket.Schema.COL_STATE, Ticket.STATE_COMPLETED
                        ),
                        null,
                        "ticketType desc"
                );
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        mCursor == null || !mCursor.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.order_status_widget_list_item);

                int ticketType = mCursor.getInt(0);
                int ticketCount = mCursor.getInt(1);
                String ticketTypeStr = null;
                if(ticketType == 0)
                    ticketTypeStr = getString(R.string.dine_in);
                else if(ticketType == -1)
                    ticketTypeStr = getString(R.string.to_go);
                else if(ticketType == -2)
                    ticketTypeStr = getString(R.string.delivery);

                views.setTextViewText(R.id.ticketType, ticketTypeStr);
                views.setTextViewText(R.id.ticketCount, ticketCount + "");

                Intent intent = new Intent();
                intent.putExtra("ticketType", ticketType);
                views.setOnClickFillInIntent(R.id.list_item, intent);

                return views;
            }

            @Override
            public long getItemId(int position) {
                if(mCursor != null && mCursor.move(position))
                    return (long)mCursor.getInt(0);
                return -1;
            }

            @Override
            public void onDestroy() {
                if(mCursor != null) {
                    mCursor.close();
                    mCursor = null;
                }
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

        };
    }


}

