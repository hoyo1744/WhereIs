package com.example.hoyo1.whereis.Request;

import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import android.widget.Toast;

public class RegisterRequest extends StringRequest {
    final static private String url = "";
    private Map<String, String> parameters;

    public RegisterRequest(String userId, String userPassword, String userEmail, String userName, String userGender, String userPhoneNumber, Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);

        String genderSeperator;
        parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("userPassword", userPassword);
        parameters.put("userEmail", userEmail);
        parameters.put("userName", userName);
        if(userGender.equals("여성"))
            parameters.put("userGender", "1");
        else
            parameters.put("userGender", "0");
        parameters.put("userPhoneNumber", userPhoneNumber);



    }


    public Map<String, String> getParams() {

        return parameters;
    }
}



