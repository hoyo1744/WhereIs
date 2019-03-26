package com.example.hoyo1.whereis;

//싱글톤 로그인
public class SingletonUser {


    private String userNumber;
    private String userName;
    private String userEmail;
    private String userId;
    private String userPhone;
    private String userLevel;




    private SingletonUser(){

    }
    public void Initialize(){
        this.userNumber=null;
        this.userId=null;
        this.userName=null;
        this.userEmail=null;
        this.userPhone=null;
        this.userLevel=null;
    }
    public void setUserInfo(String Number,String id,String name,String email,String phone,String level){
        this.userNumber=Number;
        this.userId=id;
        this.userName=name;
        this.userEmail=email;
        this.userPhone=phone;
        this.userLevel=level;
    }
    public String getUserNumber(){
        return this.userNumber;
    }
    public String getUserId(){
        return this.userId;
    }
    public String getUserEmail(){
        return this.userEmail;
    }
    public String getUserName(){
        return this.userName;
    }
    public String getUserPhone(){
        return this.userPhone;
    }
    public String getUserLevel(){
        return this.userLevel;
    }


    //---------------------------------------------------------
    private static class SingletonUserHolder{
        public static final SingletonUser instance=new SingletonUser();
    }

    public static  SingletonUser getInstance(){
        return SingletonUserHolder.instance;
    }



}
