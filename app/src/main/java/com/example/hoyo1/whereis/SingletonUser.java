package com.example.hoyo1.whereis;

//싱글톤 로그인
public class SingletonUser {

    public class UserInfo
    {
        public String userId;
        public String userName;
        public String userEmail;
        public String userPhone;
        public String userLevel;
    }

    private UserInfo user;

    public void Initialize(){
        user.userId="";
        user.userName="";
        user.userEmail="";
        user.userPhone="";
        user.userLevel="";
    }

    public void setInfo(String id,String name,String email,String phone,String level){
        this.user.userName=name;
        this.user.userEmail=email;
        this.user.userPhone=phone;
        this.user.userLevel=level;
        this.user.userId=id;
    }
    public UserInfo getInfo(){
        return this.user;
    }

    private SingletonUser(){

    }

    //---------------------------------------------------------
    private static class SingletonUserHolder{
        public static final SingletonUser instance=new SingletonUser();
    }

    public static  SingletonUser getInstance(){
        return SingletonUserHolder.instance;
    }



}
