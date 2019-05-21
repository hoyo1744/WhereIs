package com.example.hoyo1.whereis.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginInfoRequest extends StringRequest {


    final static private String urlUserInfo = "http://106.10.36.131/eodie_UserLoginInpo.php";
    private Map<String, String> parameters;

    public LoginInfoRequest(String userId, String userPassword,Response.Listener<String> listener) {
        super(Method.POST, urlUserInfo, listener, null);
        parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("userPassword", userPassword);
    }


    public Map<String, String> getParams() {

        return parameters;
    }
}
