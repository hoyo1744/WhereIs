package com.example.hoyo1.whereis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingletonGroupList {

    class GroupInfo
    {
        public String groupName;
        public String groupLeader;
        public int groupLeaderImage;
    }

    //해시맵의 키 = 그룹번호(id)
    private HashMap<Integer, GroupInfo> mapGroup;

    private SingletonGroupList(){

    }
    public void Initialize(){
        mapGroup=new HashMap<>();
    }
    public void setGroupList(Integer key,String groupName,String groupLeader,int groupLeaderResId){
        GroupInfo groupInfo=new GroupInfo();
        groupInfo.groupName=groupName;
        groupInfo.groupLeader=groupLeader;
        groupInfo.groupLeaderImage=groupLeaderResId;

        mapGroup.put(key,groupInfo);
    }
    public void setGroupList(Integer key,String groupName,String groupLeader){
        GroupInfo groupInfo=new GroupInfo();
        groupInfo.groupName=groupName;
        groupInfo.groupLeader=groupLeader;
        groupInfo.groupLeaderImage=-1;
        mapGroup.put(key,groupInfo);
    }
    public String getGroupName(Integer key){
        return this.mapGroup.get(key).groupName;
    }
    public String getGroupLeader(Integer key){
        return this.mapGroup.get(key).groupLeader;
    }
    public int getGroupLeaderImage(Integer key){
        return this.mapGroup.get(key).groupLeaderImage;
    }
    public int getGroupCount(){
        return this.mapGroup.size();
    }


    //---------------------------------------------------------
    private static class SingletonGroupListHolder{
        public static final SingletonGroupList instance=new SingletonGroupList();
    }

    public static  SingletonGroupList getInstance(){
        return SingletonGroupListHolder.instance;
    }
}
