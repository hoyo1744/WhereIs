package com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hoyo1.whereis.R;

public class GridTextView extends LinearLayout {

    //멤버프로필이름
    TextView gridTextView;

    public GridTextView(Context context) {
        super(context);
        init(context);
    }


    public void init(Context context){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.grid_text_view,this,true);


        gridTextView=(TextView)findViewById(R.id.gridTextView);


    }

    public void setContent(String content){
        gridTextView.setText(content);
    }
}
