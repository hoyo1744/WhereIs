package com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.example.hoyo1.whereis.SingerItemView;

import java.util.ArrayList;


//GridAdapter1개로 모든 그리드 행과 열을 책임져야함.
public class GridAdapter extends BaseAdapter {


    public static final int ITEM_VIEW_MAX=2;
    public static final int ITEM_VIEW_TEXT=0;
    public static final int ITEM_VIEW_PROFILE=1;


    //프로필어댑터
    ArrayList<SingerProfileItem> profileItems=new ArrayList<SingerProfileItem>();
    public Context adapterContext;

    public GridAdapter(Context context){
        adapterContext=context;
    }

    @Override
    public int getItemViewType(int position) {
        return profileItems.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_MAX;
    }

    public void addItem(SingerProfileItem item){

        profileItems.add(item);
    }

    @Override
    public int getCount() {
        return profileItems.size();
    }

    @Override
    public Object getItem(int position) {
        return profileItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);

        GridProfileView profileView = null;
        GridTextView textView = null;
        if (convertView == null) {
            SingerProfileItem item = profileItems.get(position);

            switch (viewType) {
                case ITEM_VIEW_TEXT:
                    GridTextView gridTextView = new GridTextView(adapterContext);
                    gridTextView.setContent(item.getContent());
                    convertView = gridTextView;
                    break;
                case ITEM_VIEW_PROFILE:
                    GridProfileView gridProfileView = new GridProfileView(adapterContext);
                    gridProfileView.setProfileName(item.getName());
                    gridProfileView.setProfileImage(item.getResId());
                    convertView = gridProfileView;
                    break;
            }
        }

        return convertView;
    }



}
