package com.example.hoyo1.whereis;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class GroupUserInfoRequest extends StringRequest {
    final static private String url = "";
    private Map<String, String> parameters;

    public GroupUserInfoRequest(String groupNumber,Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("groupNumber", groupNumber);
    }


    public Map<String, String> getParams() {

        return parameters;
    }
}
