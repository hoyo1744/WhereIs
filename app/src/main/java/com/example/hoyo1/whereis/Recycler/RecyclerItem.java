package com.example.hoyo1.whereis.Recycler;

public class RecyclerItem {


    private String content;
    private String date;

    public RecyclerItem(){}
    public RecyclerItem(String content,String date){
        this.content=content;
        this.date=date;
    }


    public String getContent(){
        return this.content;
    }
    public String getDate(){
        return this.date;
    }
    public void setContent(String content){
        this.content=content;
    }
    public void setDate(String date){
        this.date=date;
    }
}
