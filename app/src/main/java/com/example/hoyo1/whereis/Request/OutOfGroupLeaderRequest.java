package com.example.hoyo1.whereis.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class OutOfGroupLeaderRequest extends StringRequest {
    final static private String url = "http://106.10.36.131/eodie_OutOfGroupLeader.php";
    private Map<String, String> parameters;

    public OutOfGroupLeaderRequest(String strGroupID,String strUserID, Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("groupID", strGroupID);
        parameters.put("userID", strUserID);

    }


    public Map<String, String> getParams() {

        return parameters;
    }

}
