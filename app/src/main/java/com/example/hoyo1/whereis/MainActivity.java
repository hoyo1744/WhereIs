package com.example.hoyo1.whereis;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    class SingerAdapter extends BaseAdapter {

        ArrayList<SingerItem> items=new ArrayList<SingerItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(SingerItem item){
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SingerItemView view=new SingerItemView(getApplicationContext());
            SingerItem item= items.get(position);
            view.setName(item.getName());
            view.setImage(item.getResId());

            return view;
        }
    }

    ListView listView;
    SingerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("그룹");
        setContentView(R.layout.activity_main);



        listView=(ListView)findViewById(R.id.listView);

        adapter = new SingerAdapter();

        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp));

        listView.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId=item.getItemId();
        switch (menuId)
        {
            case R.id.searchMenu:
                break;
            case R.id.groupAddMenu:
                break;
            case R.id.settingMenu:
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}