package com.example.hoyo1.whereis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingletonGroupList {

    //
    class GroupInfo
    {
        public String groupID;
        public String groupName;
        public String groupLeader;
        public String groupLeaderNo;
        public int groupLeaderImage;
        public String groupCategory;
    }

    //해시맵의 키 = 그룹번호(id)
    private HashMap<Integer, GroupInfo> mapGroup;
    private SingletonGroupList(){

    }
    public void Initialize(){
        mapGroup=new HashMap<>();
        mapGroup.clear();
    }
    public void setGroupList(Integer key,String groupID,String groupName,String groupLeader,String groupLeaderNo,int groupLeaderResId,String groupCategory){

        GroupInfo groupInfo=new GroupInfo();
        groupInfo.groupID=groupID;
        groupInfo.groupName=groupName;
        groupInfo.groupLeader=groupLeader;
        groupInfo.groupLeaderNo=groupLeaderNo;
        groupInfo.groupLeaderImage=groupLeaderResId;
        groupInfo.groupCategory=groupCategory;


        mapGroup.put(key,groupInfo);
    }
    public void setGroupList(Integer key,String groupID,String groupName,String groupLeader,String groupLeaderNo,String groupCategory){
        GroupInfo groupInfo=new GroupInfo();
        groupInfo.groupID=groupID;
        groupInfo.groupName=groupName;
        groupInfo.groupLeader=groupLeader;
        groupInfo.groupLeaderNo=groupLeaderNo;
        groupInfo.groupCategory=groupCategory;
        groupInfo.groupLeaderImage=-1;
        mapGroup.put(key,groupInfo);
    }
    public String getGroupLeaderNo(Integer key){return this.mapGroup.get(key).groupLeaderNo;}
    public String getGroupName(Integer key){
        return this.mapGroup.get(key).groupName;
    }
    public String getGroupLeader(Integer key){
        return this.mapGroup.get(key).groupLeader;
    }
    public String getGroupID(Integer key){
        return this.mapGroup.get(key).groupID;
    }
    public String getGroupCategory(Integer key){return this.mapGroup.get(key).groupCategory;}
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
