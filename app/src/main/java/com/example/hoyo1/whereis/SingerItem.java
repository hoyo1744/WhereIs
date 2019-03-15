package com.example.hoyo1.whereis;

public class SingerItem {

    String name;
    String leaderName;
    int resId;
    int leaderResId;


    public SingerItem(String name,int resId,String leadrName,int leaderResId){
        this.name=name;
        this.resId=resId;
        this.leaderName=leadrName;
        this.leaderResId=leaderResId;
    }

    public String getName(){
        return this.name;
    }
    public String getLeaderName(){
        return this.leaderName;
    }


    public int getResId(){
        return this.resId;
    }
    public int getLeaderResId(){
        return this.leaderResId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public void setLeadResId(int resId) {
        this.leaderResId= resId;
    }

    public void setName(String name){
        this.name=name;
    }
    public void setLeaderName(String name){
        this.leaderName=name;
    }
}
