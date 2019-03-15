package com.example.hoyo1.whereis;

public class SingerItem {

    String name;
    int resId;


    public SingerItem(String name,int resId){
        this.name=name;
        this.resId=resId;
    }

    public String getName(){
        return this.name;
    }

    public int getResId(){
        return this.resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public void setName(String name){
        this.name=name;
    }
}
