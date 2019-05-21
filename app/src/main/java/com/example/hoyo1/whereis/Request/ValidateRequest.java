package com.example.hoyo1.whereis.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateRequest extends StringRequest {
    final static private String url="http://106.10.36.131/eodie_UserValidate.php";
    public Map<String,String> parameters;

    public ValidateRequest(String userId, Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);

        parameters = new HashMap<>();
        parameters.put("userId", userId);

    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }

}



