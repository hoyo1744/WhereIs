package com.example.hoyo1.whereis.Singleton;

import java.util.HashMap;

public class SingletonGroupList {

    //
    class GroupInfo
    {
        public String groupID;
        public String groupName;
        public String groupLeader;
        public String groupLeaderNo;
        public String groupLeaderName;
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
    public boolean checkExistGroup(Integer key){
        return mapGroup.containsKey(key);
    }
    public void setGroupList(Integer key,String groupID,String groupName,String groupLeader,String groupLeaderNo,String groupLeaderName,int groupLeaderResId,String groupCategory){

        GroupInfo groupInfo=new GroupInfo();
        groupInfo.groupID=groupID;
        groupInfo.groupName=groupName;
        groupInfo.groupLeader=groupLeader;
        groupInfo.groupLeaderNo=groupLeaderNo;
        groupInfo.groupLeaderName=groupLeaderName;
        groupInfo.groupLeaderImage=groupLeaderResId;
        groupInfo.groupCategory=groupCategory;


        mapGroup.put(key,groupInfo);
    }
    public void setGroupCategory(Integer key,String groupCategory){
        mapGroup.get(key).groupCategory=groupCategory;
    }
    public void setGroupLeaderID(Integer key,String groupLeaderID){
        mapGroup.get(key).groupLeader=groupLeaderID;
    }
    public void setGroupLeaderNo(Integer key,String groupLeaderNo){
        mapGroup.get(key).groupLeaderNo=groupLeaderNo;
    }
    public void setGroupLeaderName(Integer key,String groupLeaderName){
        mapGroup.get(key).groupLeaderName=groupLeaderName;
    }

    public void setGroupList(Integer key,String groupID,String groupName,String groupLeader,String groupLeaderNo,String groupLeaderName,String groupCategory){
        GroupInfo groupInfo=new GroupInfo();
        groupInfo.groupID=groupID;
        groupInfo.groupName=groupName;
        groupInfo.groupLeader=groupLeader;
        groupInfo.groupLeaderNo=groupLeaderNo;
        groupInfo.groupLeaderName=groupLeaderName;
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
    public String getGroupLeaderName(Integer key){
        return this.mapGroup.get(key).groupLeaderName;
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
