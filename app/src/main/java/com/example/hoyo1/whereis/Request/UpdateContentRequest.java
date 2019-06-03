package com.example.hoyo1.whereis.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateContentRequest extends StringRequest {
    final static private String url = "";
    private Map<String, String> parameters;

    public UpdateContentRequest(String userId, String groupId, String strCategoryNum,String strContent,Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("groupId", groupId);
        parameters.put("categoryNum",strCategoryNum);
        parameters.put("Content",strContent);
    }

        public Map<String, String> getParams() {

        return parameters;
    }

}
