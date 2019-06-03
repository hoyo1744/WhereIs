package com.example.hoyo1.whereis.Request;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupCreateRequest extends StringRequest {

    final static private String url = "";
    private Map<String, String> parameters;

    public GroupCreateRequest(String groupName, String groupLeader,String groupLeaderName,String groupLeaderID ,String groupCategoryNum, ArrayList<String> categoryHead,ArrayList<String> categoryContent, Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("groupName", groupName);
        parameters.put("groupLeader", groupLeader);
        parameters.put("groupLeaderName", groupLeaderName);
        parameters.put("groupLeaderNo", groupLeaderID);
        parameters.put("groupCategoryNum", groupCategoryNum);



        int nCategoryNum=Integer.parseInt(groupCategoryNum);

        for(int nIdx=1;nIdx<=nCategoryNum;nIdx++) {
            String strHeadNum="categoryHead"+Integer.toString(nIdx);
            String strContentNum="categoryContent"+Integer.toString(nIdx);
            String strHead1=categoryHead.get(nIdx-1).toString();
            String strContent1=categoryContent.get(nIdx-1).toString();
            Log.v("strHead1",strHead1);
            Log.v("strContent1",strContent1);
            parameters.put(strHeadNum, strHead1);
            parameters.put(strContentNum, strContent1);
        }

        //호용 : 무조건 보내고 봐야하는건가? ok
        for(int nIdx=nCategoryNum+1;nIdx<=10;nIdx++)
        {
            String strHeadNum="categoryHead"+Integer.toString(nIdx);
            String strContentNum="categoryContent"+Integer.toString(nIdx);
            String strNull="null";

            parameters.put(strHeadNum, strNull);
            parameters.put(strContentNum, strNull);
        }
    }


    public Map<String, String> getParams() {

        return parameters;
    }


}
