package com.example.hoyo1.whereis;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    final static int AM_LOGIN_SUCCESS=40000;

    private AlertDialog dialog;
    Handler handlerLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        init();





        TextView registerButton=(TextView) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent=new Intent(LoginActivity.this,RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }


        });

        final EditText idText=(EditText)findViewById(R.id.idText);
        final EditText passwordText=(EditText)findViewById(R.id.passwordText);
        final Button loginButton=(Button)findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userId=idText.getText().toString();
                final String userPassword=passwordText.getText().toString();

                Response.Listener<String> responseLister= new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{

                            JSONObject jsonResponse=new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
                            if(success)
                            {
                                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                                dialog=builder.setMessage("로그인에 성공했습니다.")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //싱글톤객체 초기화


                                                GetInitialSingletonUser(userId,userPassword);
                                                //로그인이 성공한다면 다음씬으로 넘어간다.

                                            }
                                        })
                                        .create();
                                dialog.show();


                            }
                            else
                            {
                                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                                dialog=builder.setMessage("로그인에 실패했습니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest=new LoginRequest(userId,userPassword,responseLister);
                RequestQueue queue= Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);


            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(dialog!=null)
        {
            dialog.dismiss();
            dialog=null;
        }
    }

    public void init(){
        //액션바와 타이틀바 숨기기
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        handlerLogin=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case AM_LOGIN_SUCCESS:
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        LoginActivity.this.startActivity(intent);
                        finish();
                        break;
                }
            }
        };
    }


    public void GetInitialSingletonUser(final String userId, final String userPassword){



        //서브스레드 생성 및 서버와 통신

        Thread threadGroupList=new Thread(new Runnable() {

            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;


                    //새로운 Request를 통해서 값을 가져와서 싱글톤객체를 생성함.
                    Response.Listener<String> responseLister= new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonResponse=new JSONObject(response);
                                boolean success=jsonResponse.getBoolean("success");

                                if(success)
                                {
                                    String number,id,name,email,phone,level;

                                    number=jsonResponse.getString("idx");
                                    id=jsonResponse.getString("id");
                                    name=jsonResponse.getString("name");
                                    email=jsonResponse.getString("email");
                                    phone=jsonResponse.getString("phone");
                                    level=jsonResponse.getString("level");
                                    SingletonUser.getInstance().Initialize();
                                    SingletonUser.getInstance().setUserInfo(number,id,name,email,phone,level);

                                    Message msg = handlerLogin.obtainMessage();
                                    msg.what = AM_LOGIN_SUCCESS;
                                    handlerLogin.sendMessage(msg);

                                }
                                else
                                {

                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //LoginInfoRequest로 새로 만들자.
                    LoginInfoRequest loginInfoRequest=new LoginInfoRequest(userId,userPassword,responseLister);
                    RequestQueue queueInfo= Volley.newRequestQueue(LoginActivity.this);
                    queueInfo.add(loginInfoRequest);

                }



            }



        });

        threadGroupList.start();

















    }

}
