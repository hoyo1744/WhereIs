package com.example.hoyo1.whereis;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingerItemView extends LinearLayout {

    ImageView imageView;
    TextView textView;

    public SingerItemView(Context context){
        super(context);
        init(context);
    }


    public void init(Context context){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.singer_item,this,true);

        imageView=(ImageView)findViewById(R.id.imageList);
        textView=(TextView)findViewById(R.id.textList);

    }

    public void setName(String name){
        textView.setText(name);
    }

    public void setImage(int resId){
        imageView.setImageResource(resId);
    }
}
