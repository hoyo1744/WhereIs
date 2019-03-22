package com.example.hoyo1.whereis;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginInfoRequest extends StringRequest {


    final static private String url = "";
    private Map<String, String> parameters;

    public LoginInfoRequest(String userId, String userPassword,Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("userPassword", userPassword);
    }


    public Map<String, String> getParams() {

        return parameters;
    }
}
