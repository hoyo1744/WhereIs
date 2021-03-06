package com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.hoyo1.whereis.Activity.Group2Activity;
import com.example.hoyo1.whereis.R;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    public static final int ITEM_VIEW_MAX=2;
    public static final int ITEM_VIEW_TEXT=0;
    public static final int ITEM_VIEW_PROFILE=1;

    ArrayList<SingerProfileItem> profileItems=new ArrayList<SingerProfileItem>();
    public Context adapterContext;
    int nAdapterNum;                //호용 20190429 : 몇번째 카테고리에 해당하는 어댑터인지


    public ListAdapter (Context context){
        adapterContext=context;
    }
    public ListAdapter(Context context,int nCategoryNum){
        adapterContext=context;
        nAdapterNum=nCategoryNum;
    }


    public int getNumCategory(){
        return this.nAdapterNum;
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
        int color;

        //테스트
        //ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(1000,200);
        if (convertView == null) {
            SingerProfileItem item = profileItems.get(position);
            //호용 : 가로,세로크기를 입력받아 그리드셀 크기를 조정해준다.
            gridProfileViewParams=new GridProfileView.LayoutParams(item.getParams().width,item.getParams().height);
            gridTextViewParams=new GridTextView.LayoutParams(item.getParams().width,item.getParams().height);
            color=item.getColor();
            switch (viewType) {
                case ITEM_VIEW_TEXT:
                    GridTextView gridTextView = new GridTextView(adapterContext);
                    gridTextView.setId(R.id.gridTextViewLayout);
                    gridTextView.setGravity(Gravity.CENTER);
                    gridTextView.setContent(item.getContent());
                    gridTextView.gridTextView.setTextColor(Color.BLACK);
                    convertView = gridTextView;
                    convertView.setLayoutParams(gridTextViewParams);//테스트
                    convertView.setBackgroundColor(color);
                    Group2Activity.mapSelectedTextView.put(gridTextView,this);
                    break;
                case ITEM_VIEW_PROFILE:
                    GridProfileView gridProfileView = new GridProfileView(adapterContext);
                    gridProfileView.setId(R.id.gridProfileViewLayout);
                    gridProfileView.setProfileName(item.getName());
                    gridProfileView.setProfileImage(item.getResId());
                    convertView = gridProfileView;
                    convertView.setLayoutParams(gridProfileViewParams);//테스트
                    convertView.setBackgroundColor(color);
                    Group2Activity.mapSelectedProfileView.put(gridProfileView,this);
                    break;
            }
        }
        else{

        }

        return convertView;
    }

}
