package com.example.hoyo1.whereis;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    final static private String url = "http://.php";
    private Map<String, String> parameters;

    public RegisterRequest(String userId, String userPassword, String userEmail, String userName, String userPhoneNumber, String userGender, Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);

        parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("userPassword", userPassword);
        parameters.put("userEmail", userEmail);
        parameters.put("userName", userName);
        parameters.put("userPhoneNumber", userPhoneNumber);
        parameters.put("userGender", userGender);
    }


    public Map<String, String> getParams() {
        return parameters;
    }
}



