package com.example.hoyo1.whereis;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddGroupMemberRequest extends StringRequest {

    final static private String url = "";
    private Map<String, String> parameters;

    public AddGroupMemberRequest(String groupID, String userNo,String userID,String userPriv ,String groupCategory, Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("groupID", groupID);
        parameters.put("groupCategory", groupCategory);
        parameters.put("userNo", userNo);
        parameters.put("userID", userID);
        parameters.put("userPriv", userPriv);
    }


    public Map<String, String> getParams() {

        return parameters;
    }

}
