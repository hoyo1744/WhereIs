package com.example.hoyo1.whereis;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class subGroupAddCategory extends LinearLayout {
    public subGroupAddCategory(Context context) {
        super(context);

        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sub_group_category,this,true);
    }
}

