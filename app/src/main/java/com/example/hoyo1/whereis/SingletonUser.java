package com.example.hoyo1.whereis;

//싱글톤 로그인
public class SingletonUser {

    public class UserInfo
    {
        public String userId;
        public String userName;
        public String userEmail;
    }

    private UserInfo user;

    public void Initialize(){
        user.userId="";
        user.userName="";
        user.userEmail="";
    }

    public void setInfo(String id,String name,String email){
        user.userId=id;
        user.userName=name;
        user.userEmail=email;
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
