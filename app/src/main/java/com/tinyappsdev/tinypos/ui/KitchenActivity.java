package com.tinyappsdev.tinypos.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;

public class KitchenActivity extends SyncableActivity {

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView)findViewById(R.id.kitchen_listview);
        mListView.setAdapter(new MyAdapter(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static class MyAdapter extends ArrayAdapter<Object> {

        public MyAdapter(Context context) {
            super(context, R.layout.kitchen_item, new Object[] {
                    "Wonton",
                    "Chicken Corn",
                    "Boiled Seafood & Vegetables",
            });
        }


        class ViewHolder {
            TextView itemSeq;
            TextView itemName;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.kitchen_item, parent, false);

                ViewHolder holder = new ViewHolder();
                holder.itemSeq = (TextView)convertView.findViewById(R.id.item_seq);
                holder.itemName = (TextView)convertView.findViewById(R.id.item_name);
                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder)convertView.getTag();
            holder.itemName.setText((String)getItem(position));
            holder.itemSeq.setText("100-" + (9 + position));

            return convertView;
        }

    }

}
