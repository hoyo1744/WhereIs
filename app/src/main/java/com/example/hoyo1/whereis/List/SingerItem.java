package com.example.hoyo1.whereis.List;

public class SingerItem {

    String name;
    String leaderID;
    String leaderName;
    int resId;
    int leaderResId;


    public SingerItem(String name,int resId,String leadrID,String leaderName,int leaderResId){
        this.name=name;
        this.resId=resId;
        this.leaderID=leadrID;
        this.leaderName=leaderName;
        this.leaderResId=leaderResId;
    }

    public String getName(){
        return this.name;
    }
    public String getLeaderID(){
        return this.leaderID;
    }
    public String getLeaderName() {return this.leaderName;}
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
    public void setLeaderID(String ID){
        this.leaderID=ID;
    }
    public void setLeaderName(String leaderName){this.leaderName=leaderName;}
}
