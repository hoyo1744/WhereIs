package com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;


public class GridTextAdapter extends BaseAdapter {

    ArrayList<SingerTextItem> Items=new ArrayList<SingerTextItem>();
    public Context adapterContext;

    public GridTextAdapter(Context context){
        adapterContext=context;
    }
    public void addItem(SingerTextItem item){

        Items.add(item);
    }
    @Override
    public int getCount() {
        return Items.size();
    }

    @Override
    public Object getItem(int position) {
        return Items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridTextView view = null;
        if (convertView != null)
            view = (GridTextView) convertView;
        else {
            view = new GridTextView(adapterContext.getApplicationContext());
        }

        SingerTextItem item = Items.get(position);
        view.setContent(item.getContent());
        return view;
    }
}
