package com.example.hoyo1.whereis.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateLeaderGroupDBRequest extends StringRequest {
    final static private String url = "";
    private Map<String, String> parameters;

    public UpdateLeaderGroupDBRequest(String groupID, String selectedUserName, String selectedUserNo, Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("groupID", groupID);
        parameters.put("userName", selectedUserName);
        parameters.put("userNo",selectedUserNo);
    }

    public Map<String, String> getParams() {

        return parameters;
    }
}
