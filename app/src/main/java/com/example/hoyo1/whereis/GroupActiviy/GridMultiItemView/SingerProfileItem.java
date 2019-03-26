package com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView;

public class SingerProfileItem {


    private int type;

    String profileName;
    int profileImageResId;

    String content;


    public SingerProfileItem(String name,int resId,int type){
        profileName=name;
        profileImageResId=resId;
        this.type=type;
    }

    public SingerProfileItem(String content,int type){
        this.content=content;
        this.type=type;
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
}
