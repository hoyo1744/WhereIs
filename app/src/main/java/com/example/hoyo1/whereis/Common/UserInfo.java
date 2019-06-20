package com.example.hoyo1.whereis.Common;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.hoyo1.whereis.Activity.Group2Activity;

import java.util.ArrayList;


public class UserInfo  implements Comparable<UserInfo>,Parcelable {

    public String strUserNo;
    public String strUserID;
    public String strUserName;
    public int nCategory;
    public int nUserPriv;
    public ArrayList<String> strArrContent=new ArrayList<String>();


    public UserInfo(){
    }
    public UserInfo(Parcel in) {
        //strArrContent=new ArrayList<String>();

        strUserNo=in.readString();
        strUserID=in.readString();
        strUserName=in.readString();
        nCategory=in.readInt();
        nUserPriv=in.readInt();
        strArrContent=in.readArrayList(String.class.getClassLoader());

    }


    public String getUserNo(){return this.strUserNo;}
    public int getCategory() {
        return this.nCategory;
    }
    public int getUserPriv(){return this.nUserPriv;
    }
    public String getUserID() {
        return this.strUserID;
    }
    public String getContent(int pos) {
        return this.strArrContent.get(pos);
    }
    public String getUserName(){return this.strUserName;}

    public void setUserPriv(int nPriv){
        this.nUserPriv=nPriv;
    }
    public void setCategory(int nParam) {
        this.nCategory = nParam;
    }
    public void setUserNo(String strParam){this.strUserNo=strParam;}
    public void setUserID(String strParam) {
        this.strUserID = strParam;
    }
    public void setUserName(String strParam){this.strUserName=strParam;}
    public void AddContent(String strParam) {
        this.strArrContent.add(strParam);
    }


    @Override
    public int compareTo(UserInfo other){
        if(nUserPriv<other.nUserPriv)
            return -1;
        else if(nUserPriv==other.nUserPriv)
            return 0;
        else
            return 1;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strUserNo);
        dest.writeString(strUserID);
        dest.writeString(strUserName);
        dest.writeInt(nCategory);
        dest.writeInt(nUserPriv);
        dest.writeList(strArrContent);
    }
}
