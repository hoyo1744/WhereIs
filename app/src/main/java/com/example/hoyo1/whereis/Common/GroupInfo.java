package com.example.hoyo1.whereis.Common;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class GroupInfo implements Parcelable {

    public String groupName;
    public String groupLeaderName;
    public String groupCategoryNum;
    public ArrayList<String> listHead;
    public ArrayList<String> listContent;


    public GroupInfo(){}
    public GroupInfo(Parcel in) {
        listHead=new ArrayList<String>();
        listContent=new ArrayList<String>();

        groupName=in.readString();
        groupLeaderName=in.readString();
        groupCategoryNum=in.readString();
        listHead=in.readArrayList(String.class.getClassLoader());
        listContent=in.readArrayList(String.class.getClassLoader());
    }

    public static final Creator<GroupInfo> CREATOR = new Creator<GroupInfo>() {
        @Override
        public GroupInfo createFromParcel(Parcel in) {
            return new GroupInfo(in);
        }

        @Override
        public GroupInfo[] newArray(int size) {
            return new GroupInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupName);
        dest.writeString(groupLeaderName);
        dest.writeString(groupCategoryNum);
        dest.writeList(listHead);
        dest.writeList(listContent);
    }
}
