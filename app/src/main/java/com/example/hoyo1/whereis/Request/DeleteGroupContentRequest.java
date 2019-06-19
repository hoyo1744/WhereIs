package com.example.hoyo1.whereis.Request;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeleteGroupContentRequest extends StringRequest {
    final static private String url = "";
    private Map<String, String> parameters;

    public DeleteGroupContentRequest(String groupID, Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("groupID", groupID);
    }
    public Map<String, String> getParams() {

        return parameters;
    }

}
