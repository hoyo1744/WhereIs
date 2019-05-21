package com.example.hoyo1.whereis.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddGroupContentRequest extends StringRequest {

    final static private String url = "http://106.10.36.131/eodie_AddGroupContent.php";
    private Map<String, String> parameters;

    public AddGroupContentRequest(String groupID, String userID, String groupCategory,String userPriv ,Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("groupID", groupID);
        parameters.put("userID", userID);
        parameters.put("userPriv", userPriv);
        parameters.put("groupCategory", groupCategory);
    }


    public Map<String, String> getParams() {

        return parameters;
    }
}
