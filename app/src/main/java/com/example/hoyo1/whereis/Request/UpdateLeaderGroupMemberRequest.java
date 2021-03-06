package com.example.hoyo1.whereis.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateLeaderGroupMemberRequest extends StringRequest {
    final static private String url = "";
    private Map<String, String> parameters;

    public UpdateLeaderGroupMemberRequest(String groupID, String selectedUserNo,String currentUserNo,Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("groupID", groupID);
        parameters.put("selectedUserNo",selectedUserNo);
        parameters.put("currentUserNo",currentUserNo);
    }

    public Map<String, String> getParams() {

        return parameters;
    }
}
