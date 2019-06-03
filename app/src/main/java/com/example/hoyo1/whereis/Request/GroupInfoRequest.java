package com.example.hoyo1.whereis.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class GroupInfoRequest extends StringRequest {
    final static private String url = "";
    private Map<String, String> parameters;

    public GroupInfoRequest(String groupNumber,Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("groupNumber", groupNumber);
    }


    public Map<String, String> getParams() {

        return parameters;
    }
}
