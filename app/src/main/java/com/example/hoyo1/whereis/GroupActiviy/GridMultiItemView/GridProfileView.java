package com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hoyo1.whereis.R;

public class GridProfileView extends LinearLayout{

    //멤버프로필이미지
    ImageView gridProfileImageVIew;

    //멤버프로필이름
    TextView gridProfileTextView;


    public GridProfileView(Context context) {

        super(context);
        init(context);
    }


    public void init(Context context){
        //getSystemService함수를 통해서 인플레이터객체를 반환
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.grid_profile_view,this,true);

        gridProfileImageVIew=(ImageView)findViewById(R.id.profileGridImageView);
        gridProfileTextView=(TextView)findViewById(R.id.profileGridTextView);


    }

    public void setProfileName(String name){
        gridProfileTextView.setText(name);
    }

    public void setProfileImage(int resId){
        gridProfileImageVIew.setImageResource(resId);
    }

}
