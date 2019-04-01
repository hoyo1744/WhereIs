package com.example.hoyo1.whereis;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupCreateRequest extends StringRequest {

    final static private String url = "";
    private Map<String, String> parameters;

    public GroupCreateRequest(String groupName, String groupLeader, String groupCategoryNum, ArrayList categoryHead,ArrayList categoryContent, Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("groupName", groupName);
        parameters.put("groupLeader", groupLeader);
        parameters.put("groupCategoryNum", groupCategoryNum);

        for(int nIdx=1;nIdx<=10;nIdx++) {
            parameters.put("categoryHead" + "nIdx", categoryHead.get(nIdx).toString());
            parameters.put("categoryContent" + "nIdx", categoryContent.get(nIdx).toString());
        }
    }


    public Map<String, String> getParams() {

        return parameters;
    }


}
