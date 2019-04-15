package com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView;

import android.view.ViewGroup;

public class SingerProfileItem {


    ViewGroup.LayoutParams params;// 호용 : 이걸 통해서 그리드뷰 1개의셀의 크기를 정한다.
    private int type;

    String profileName;
    int profileImageResId;
    String content;


    public SingerProfileItem(String name,int resId,int type){
        profileName=name;
        profileImageResId=resId;
        this.type=type;
    }
    public SingerProfileItem(String name,int resId,int type,int width,int height){
        profileName=name;
        profileImageResId=resId;
        this.type=type;
        params=new ViewGroup.LayoutParams(width,height);
    }

    public SingerProfileItem(String content,int type){
        this.content=content;
        this.type=type;
    }
    public SingerProfileItem(String content,int type,int width,int height){
        this.content=content;
        this.type=type;
        params=new ViewGroup.LayoutParams(width,height);
    }



    public int getType(){
        return this.type;
    }
    public void setType(int type){
        this.type=type;
    }
    public String getContent(){
        return this.content;
    }
    public ViewGroup.LayoutParams getParams(){return this.params;}

    public void setContent(String content){
        this.content=content;
    }
    public String getName(){
        return this.profileName;
    }
    public int getResId(){
        return this.profileImageResId;
    }
    public void setResId(int resId) {
        this.profileImageResId = resId;
    }
    public void setName(String name){
        this.profileName=name;
    }
    public void setParams(int width,int height){this.params=new ViewGroup.LayoutParams(width,height);}
}
