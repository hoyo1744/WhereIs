package com.example.hoyo1.whereis.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class SingerAdapter extends BaseAdapter {

    ArrayList<SingerItem> items=new ArrayList<SingerItem>();
    public Context adpaterContext;
    public SingerAdapter(Context context){
        adpaterContext=context;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    public void removeAll(){items.clear();}
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
        SingerItemView view=null;
        if(convertView!=null)
            view=(SingerItemView)convertView;
        else
        {
            view=new SingerItemView(adpaterContext.getApplicationContext());
        }


        SingerItem item= items.get(position);
        view.setName(item.getName());
        view.setImage(item.getResId());
        view.setLeadrImage(item.getLeaderResId());
        view.setLeaderName(item.getLeaderName());

        return view;
    }
}