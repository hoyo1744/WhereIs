package com.example.hoyo1.whereis;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class GroupMemberAddActivity extends AppCompatActivity {


    public static final int AM_GET_USERINFO=19051200;
    public static final int AM_GROUP_ADD_MEMBER=19051201;
    public static final int AM_GROUP_ADD_CONTENT=19051202;
    public static final int AM_GROUP_ADD_SUCCESS=19051203;
    EditText memberNameEditText;
    Button memberNameValidateButton;
    private boolean validate=false;
    private AlertDialog dialog;
    String groupID;
    String userNo;          //group_member에서 가져온다.
    String userPriv;        //group_member에서 가져온다.
    String strCategoryNum;
    Intent intent;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("멤버추가");
        setContentView(R.layout.activity_group_member_add);

        init();
        //Toast.makeText(this,"테스트",Toast.LENGTH_LONG).show();
        //자신의 아이디를 찾을 수 없도록 예외처리해야함.-->유저정보 싱글톤으로 유지한 후에!

        memberNameValidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID=memberNameEditText.getText().toString();
                //if(validate)
                // return ;

                if(userID.equals("")) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(GroupMemberAddActivity.this);
                    dialog=builder.setMessage("아이디가 빈칸입니다.")
                            .setPositiveButton("확인",null)
                            .create();
                    dialog.show();
                    return ;

                }
                //response 리스너 구현
                Response.Listener<String> responListener=new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            boolean successGroupMember=jsonObject.getBoolean("successGroupMember");
                            boolean successUser=jsonObject.getBoolean("successUser");

                            if(successGroupMember){
                                AlertDialog.Builder builder=new AlertDialog.Builder(GroupMemberAddActivity.this);
                                dialog=builder.setMessage("이미 존재합니다.")
                                        .setPositiveButton("확인",null)
                                        .create();
                                dialog.show();
                            }
                            else if(successUser){
                                AlertDialog.Builder builder=new AlertDialog.Builder(GroupMemberAddActivity.this);
                                dialog=builder.setMessage("아이디를 찾았습니다.")
                                        .setPositiveButton("확인",null)
                                        .create();
                                dialog.show();
                                //idText.setEnabled(false);
                                validate=true;



                            }
                            else{

                                AlertDialog.Builder builder=new AlertDialog.Builder(GroupMemberAddActivity.this);
                                dialog=builder.setMessage("아이디를 찾을 수 없습니다.")
                                        .setPositiveButton("확인",null)
                                        .create();
                                dialog.show();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };



                ValidateGroupMemberAndUser validateRequest=new ValidateGroupMemberAndUser(userID,groupID,responListener);
                RequestQueue queue = Volley.newRequestQueue(GroupMemberAddActivity.this);
                queue.add(validateRequest);
            }
        });




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
                //해당아이디의 그룹멤버를추가한다.
                //1.찾기를 먼저 진행해야한다.
                if(!validate){
                    //"찾기"먼저 하세요.
                    AlertDialog.Builder builder=new AlertDialog.Builder(GroupMemberAddActivity.this);
                    dialog=builder.setMessage("아이디를 확인해주세요.")
                            .setPositiveButton("확인",null)
                            .create();
                    dialog.show();
                }
                else{
                    //메시지르 통해서 group_content도 추가해야함.
                    //유저이름파악완료
                    Message msg = handler.obtainMessage();
                    msg.what = AM_GET_USERINFO;
                    handler.sendMessage(msg);
                }




                break;
            case android.R.id.home:
                //뒤로가기
                setResult(RESULT_CANCELED);
                finish();
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    public void init(){
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_HOME_AS_UP|actionBar.DISPLAY_SHOW_TITLE);

        memberNameEditText=(EditText)findViewById(R.id.memberNameEditText);
        memberNameValidateButton=(Button)findViewById(R.id.memberValidateButton);

        intent = getIntent(); /*데이터 수신*/
        groupID=intent.getExtras().getString("groupID");
        int nCategoryNum=intent.getExtras().getInt("groupCategory");
        nCategoryNum-=1;
        strCategoryNum=Integer.toString(nCategoryNum);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {

                    case AM_GET_USERINFO:
                        GetUserNo();
                        break;
                    case AM_GROUP_ADD_MEMBER:
                        AddGroupMember();
                        break;
                    case AM_GROUP_ADD_CONTENT:
                        AddGroupContent();
                        break;
                    case AM_GROUP_ADD_SUCCESS:
                        setResult(RESULT_OK);
                        finish();
                        break;

                }

            }
        };
    }
    protected  void onStop()
    {
        super.onStop();
        if(dialog!=null)
        {
            dialog.dismiss();
            dialog=null;
        }
    }

    public void GetUserNo(){

            Thread thread = new Thread(new Runnable() {
            String userID=memberNameEditText.getText().toString();
            boolean isPlaying = false;

            @Override
            public void run() {
                if (isPlaying == false) {
                    isPlaying = true;
                    Response.Listener<String> responseLister2 = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    userNo = jsonResponse.getString("userNo");

                                    //유저이름파악완료
                                    Message msg = handler.obtainMessage();
                                    msg.what = AM_GROUP_ADD_MEMBER;
                                    handler.sendMessage(msg);
                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    GetUserInfoAboutGroupRequest userInfoRequest= new GetUserInfoAboutGroupRequest(userID, responseLister2);
                    RequestQueue queue = Volley.newRequestQueue(GroupMemberAddActivity.this);
                    queue.add(userInfoRequest);
                }
            }
        });
        thread.start();



    }

    public void AddGroupContent(){

        Thread thread = new Thread(new Runnable() {

            boolean isPlaying = false;

            @Override
            public void run() {
                if (isPlaying == false) {
                    isPlaying = true;
                    Response.Listener<String> responseLister2 = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if (success) {
                                    //유저이름파악완료
                                    Message msg = handler.obtainMessage();
                                    msg.what = AM_GROUP_ADD_SUCCESS;
                                    handler.sendMessage(msg);
                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    AddGroupContentRequest addGroupContentRequest = new AddGroupContentRequest(groupID,userNo,strCategoryNum,"1",responseLister2);
                    RequestQueue queue = Volley.newRequestQueue(GroupMemberAddActivity.this);
                    queue.add(addGroupContentRequest);
                }
            }
        });
        thread.start();



    }

    public void AddGroupMember(){
        Thread thread = new Thread(new Runnable() {

            String userID=memberNameEditText.getText().toString();
            boolean isPlaying = false;

            @Override
            public void run() {
                if (isPlaying == false) {
                    isPlaying = true;
                    Response.Listener<String> responseLister2 = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if (success) {
                                    //유저이름파악완료
                                    Message msg = handler.obtainMessage();
                                    msg.what = AM_GROUP_ADD_CONTENT;
                                    handler.sendMessage(msg);
                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    AddGroupMemberRequest addGroupMemberRequest= new AddGroupMemberRequest(groupID,userNo,userID,strCategoryNum,responseLister2);
                    RequestQueue queue = Volley.newRequestQueue(GroupMemberAddActivity.this);
                    queue.add(addGroupMemberRequest);
                }
            }
        });
        thread.start();


    }
}
