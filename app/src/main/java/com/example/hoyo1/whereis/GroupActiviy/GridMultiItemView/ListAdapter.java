package com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    public static final int ITEM_VIEW_MAX=2;
    public static final int ITEM_VIEW_TEXT=0;
    public static final int ITEM_VIEW_PROFILE=1;

    ArrayList<SingerProfileItem> profileItems=new ArrayList<SingerProfileItem>();
    public Context adapterContext;


    public ListAdapter (Context context){
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
    public void removeAll(){
        profileItems.clear();
    }
    public void addItem(SingerProfileItem item){
        profileItems.add(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int viewType = getItemViewType(position);

        GridProfileView profileView = null;
        GridTextView textView = null;
        GridProfileView.LayoutParams gridProfileViewParams=null;
        GridTextView.LayoutParams gridTextViewParams=null;

        //테스트
        //ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(1000,200);
        if (convertView == null) {
            SingerProfileItem item = profileItems.get(position);
            //호용 : 가로,세로크기를 입력받아 그리드셀 크기를 조정해준다.
            gridProfileViewParams=new GridProfileView.LayoutParams(item.getParams().width,item.getParams().height);
            gridTextViewParams=new GridTextView.LayoutParams(item.getParams().width,item.getParams().height);
            switch (viewType) {
                case ITEM_VIEW_TEXT:
                    GridTextView gridTextView = new GridTextView(adapterContext);
                    gridTextView.setGravity(Gravity.CENTER);
                    gridTextView.setContent(item.getContent());
                    convertView = gridTextView;
                    convertView.setLayoutParams(gridTextViewParams);//테스트


                    break;
                case ITEM_VIEW_PROFILE:
                    GridProfileView gridProfileView = new GridProfileView(adapterContext);
                    gridProfileView.setGravity(Gravity.CENTER);
                    gridProfileView.setProfileName(item.getName());
                    gridProfileView.setProfileImage(item.getResId());
                    convertView = gridProfileView;
                    convertView.setLayoutParams(gridProfileViewParams);//테스트
                    break;
            }
        }
        else{

        }

        return convertView;
    }

}
