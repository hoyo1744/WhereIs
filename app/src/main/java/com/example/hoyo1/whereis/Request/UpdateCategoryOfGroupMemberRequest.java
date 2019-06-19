package com.example.hoyo1.whereis.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateCategoryOfGroupMemberRequest extends StringRequest {
    final static private String url = "";
    private Map<String, String> parameters;

    public UpdateCategoryOfGroupMemberRequest(String groupID,String categoryNum ,Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("groupID", groupID);
        parameters.put("categoryNum", categoryNum);
    }
    public Map<String, String> getParams() {

        return parameters;
    }
}
