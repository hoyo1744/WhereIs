package com.example.hoyo1.whereis.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hoyo1.whereis.Common.CustomLoadingDialog;
import com.example.hoyo1.whereis.Common.UserInfo;
import com.example.hoyo1.whereis.R;
import com.example.hoyo1.whereis.Request.UpdateLeaderGroupContentRequest;
import com.example.hoyo1.whereis.Request.UpdateLeaderGroupDBRequest;
import com.example.hoyo1.whereis.Request.UpdateLeaderGroupMemberRequest;
import com.example.hoyo1.whereis.Singleton.SingletonSocket;
import com.example.hoyo1.whereis.Singleton.SingletonUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ChangeLeaderActivity extends AppCompatActivity {

    //핸들러메시지
    public static final int AM_UPDATE_GROUP_DB=60000;
    public static final int AM_UPDATE_GROUP_MEMBER=60001;
    public static final int AM_UPDATE_SUCCESS=60002;



    //멤버변수
    private CustomLoadingDialog customLoadingDialog;
    private HashMap<String,String> nameToNohashMap;
    private HashMap<String,String> nameToIDhashMap;
    private ArrayList<UserInfo> userInfoList;
    private ArrayAdapter<String> adapter;
    private String selectedUserName;
    private TextView currentLeader;
    private Spinner spinner;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("리더변경");
        setContentView(R.layout.activity_change_leader);

        //현재 보고 있는 액티비티 설정
        SingletonSocket.getInstance().setActivity(this);

        //초기화
        Init();

        //인텐트데이터수신
        SetSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_groupadd,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId=item.getItemId();
        switch (menuId)
        {
            case R.id.confirmMenu:
                //변경사항확인처리
                if(SingletonUser.getInstance().getUserName().equals(selectedUserName)){
                    AlertDialog dialog;
                    AlertDialog.Builder builder=new AlertDialog.Builder(ChangeLeaderActivity.this);
                    dialog=builder.setMessage("이전과 동일합니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    break;
                }

                //로딩
                customLoadingDialog=new CustomLoadingDialog(ChangeLeaderActivity.this);
                customLoadingDialog.show();

                //디비변경
                UpdateGroupDB();

                break;
            case android.R.id.home:
                //뒤로가기
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void Init(){
        //액션바 및 타이틀바 설정
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_HOME_AS_UP|actionBar.DISPLAY_SHOW_TITLE);

        //객체참조
        spinner=(Spinner)findViewById(R.id.spinner);
        currentLeader=(TextView)findViewById(R.id.currentLeader);
        userInfoList=new ArrayList<UserInfo>();
        nameToIDhashMap=new HashMap<>();
        nameToNohashMap=new HashMap<>();

        //스피너리스너
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUserName=adapter.getItem(position);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedUserName="";
            }
        });

        //핸들러
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case AM_UPDATE_GROUP_DB:
                        UpdateGroupMember();
                        break;

                    case AM_UPDATE_GROUP_MEMBER:
                        UpdateGroupContent();
                        break;
                    case AM_UPDATE_SUCCESS:
                        //로딩완료
                        customLoadingDialog.dismiss();

                        //끝
                        Intent result=new Intent();

                        String selectedUserID=nameToIDhashMap.get(selectedUserName).toString();
                        String selectedUserNo=nameToNohashMap.get(selectedUserName).toString();
                        result.putExtra("groupLeaderName",selectedUserName);
                        result.putExtra("groupLeaderNo",selectedUserNo);
                        result.putExtra("groupLeaderID",selectedUserID);


                        setResult(RESULT_OK,result);
                        finish();
                        break;
                }

            }
        };
    }

    public void SetSpinner(){

        //인텐트수신
        Intent intent=getIntent();
        userInfoList=intent.getParcelableArrayListExtra("userInfo");
        String[] userNames = new String[userInfoList.size()];
        for(int idx=0;idx<userInfoList.size();idx++){
            userNames[idx]=userInfoList.get(idx).strUserName;
            nameToNohashMap.put(userInfoList.get(idx).strUserName,userInfoList.get(idx).strUserNo);
            nameToIDhashMap.put(userInfoList.get(idx).strUserName,userInfoList.get(idx).strUserID);
        }

        //스피너설정
        adapter=new ArrayAdapter<String>(this,R.layout.spinner_item,userNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //현재리더설정
        currentLeader.setText(SingletonUser.getInstance().getUserName());
    }


    public void UpdateGroupDB(){
        String groupID=getIntent().getStringExtra("groupID");
        String selectedUserNo=nameToNohashMap.get(selectedUserName);


        UpdateLeaderGroupDBRequest request = new UpdateLeaderGroupDBRequest(groupID,selectedUserName,selectedUserNo, responseGroupDBListener);
        RequestQueue queue = Volley.newRequestQueue(ChangeLeaderActivity.this);
        queue.add(request);
    }
    public void UpdateGroupMember(){
        String groupID=getIntent().getStringExtra("groupID");
        String selectedUserNo=nameToNohashMap.get(selectedUserName);
        String currentUserNo=SingletonUser.getInstance().getUserNumber();

        UpdateLeaderGroupMemberRequest request=new UpdateLeaderGroupMemberRequest(groupID,selectedUserNo,currentUserNo,responseGroupMemberListener);
        RequestQueue queue = Volley.newRequestQueue(ChangeLeaderActivity.this);
        queue.add(request);

    }
    public void UpdateGroupContent(){
        String groupID=getIntent().getStringExtra("groupID");
        String selectedUserNo=nameToNohashMap.get(selectedUserName);
        String currentUserNo=SingletonUser.getInstance().getUserNumber();


        UpdateLeaderGroupContentRequest request=new UpdateLeaderGroupContentRequest(groupID,selectedUserNo,currentUserNo,responseGroupContentListener);
        RequestQueue queue = Volley.newRequestQueue(ChangeLeaderActivity.this);
        queue.add(request);
    }
    //리스폰그룹디비리더리스너
    private Response.Listener<String> responseGroupContentListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");

                if(success){
                    //핸들러처리
                    Message msg = handler.obtainMessage();
                    msg.what = AM_UPDATE_SUCCESS;
                    handler.sendMessage(msg);
                }else{
                    customLoadingDialog.dismiss();
                    AlertDialog dialog;
                    AlertDialog.Builder builder=new AlertDialog.Builder(ChangeLeaderActivity.this);
                    dialog=builder.setMessage("서버와 연결이 실패했습니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    //리스폰그룹디비리더리스너
    private Response.Listener<String> responseGroupMemberListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");

                if(success){
                  //핸들러처리
                    Message msg = handler.obtainMessage();
                    msg.what = AM_UPDATE_GROUP_MEMBER;
                    handler.sendMessage(msg);
                }else{
                    customLoadingDialog.dismiss();
                    AlertDialog dialog;
                    AlertDialog.Builder builder=new AlertDialog.Builder(ChangeLeaderActivity.this);
                    dialog=builder.setMessage("서버와 연결이 실패했습니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    //리스폰그룹디비리더리스너
    private Response.Listener<String> responseGroupDBListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");
               if(success){
                //핸들러처리
                   Message msg = handler.obtainMessage();
                   msg.what = AM_UPDATE_GROUP_DB;
                   handler.sendMessage(msg);
                }else{
                   customLoadingDialog.dismiss();
                   AlertDialog dialog;
                   AlertDialog.Builder builder=new AlertDialog.Builder(ChangeLeaderActivity.this);
                   dialog=builder.setMessage("서버와 연결이 실패했습니다.")
                           .setPositiveButton("확인", null)
                           .create();
                   dialog.show();
               }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}
