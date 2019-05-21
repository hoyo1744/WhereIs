package com.example.hoyo1.whereis.List;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hoyo1.whereis.R;

public class SingerItemView extends LinearLayout {

    //그룹프로필
    ImageView imageView;
    //그룹명
    TextView textView;
    //그룹리더프로필
    ImageView imageLeaderProfileView;
    //그룹리더이름
    TextView textLeaderNameView;

    public SingerItemView(Context context){
        super(context);
        init(context);
    }


    public void init(Context context){
        //getSystemService함수를 통해서 인플레이터객체를 반환
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.singer_item,this,true);

        imageView=(ImageView)findViewById(R.id.imageList);
        textView=(TextView)findViewById(R.id.textList);
        imageLeaderProfileView=(ImageView)findViewById(R.id.leaderImageList);
        textLeaderNameView=(TextView)findViewById(R.id.leaderNameTextList);

    }

    public void setName(String name){
        textView.setText(name);
    }

    public void setImage(int resId){
        imageView.setImageResource(resId);
    }

    public void setLeadrImage(int resId)
    {
        imageLeaderProfileView.setImageResource(resId);
    }

    public void setLeaderName(String name)
    {
        textLeaderNameView.setText(name);
    }
}
