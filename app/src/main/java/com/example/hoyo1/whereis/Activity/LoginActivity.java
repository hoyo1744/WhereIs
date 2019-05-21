package com.example.hoyo1.whereis.Activity;

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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hoyo1.whereis.R;
import com.example.hoyo1.whereis.Request.LoginInfoRequest;
import com.example.hoyo1.whereis.Request.LoginRequest;
import com.example.hoyo1.whereis.Singleton.SingletonUser;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    //핸들메시지
    private final static int AM_LOGIN_SUCCESS=10000;

    //액티비티요청메시지
    private final static int REQUEST_MAIN=100;


    private TextView registerButton;
    private EditText passwordText;
    private Handler handlerLogin;
    private Button loginButton;
    private AlertDialog dialog;
    private EditText idText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //액티비티관련 초기화
        init();

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


        //액션바 및 타이틀바 설정
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);


        //객체참조
        registerButton=(TextView) findViewById(R.id.registerButton);
        idText=(EditText)findViewById(R.id.idText);
        passwordText=(EditText)findViewById(R.id.passwordText);
        loginButton=(Button)findViewById(R.id.loginButton);


        //리스너연결
        registerButton.setOnClickListener(registerButtonListener);
        loginButton.setOnClickListener(loginButtonListener);


        //핸들러
        handlerLogin=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==AM_LOGIN_SUCCESS){
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    //LoginActivity.this.startActivity(intent);

                    idText.setText("");
                    passwordText.setText("");

                    LoginActivity.this.startActivityForResult(intent,REQUEST_MAIN);
                }
            }
        };

    }




    public void GetInitialSingletonUser(final String userId, final String userPassword){



        //서브스레드 생성 및 서버와 통신

        Thread thread=new Thread(new Runnable() {

            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;

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

                    LoginInfoRequest loginInfoRequest=new LoginInfoRequest(userId,userPassword,responseLister);
                    RequestQueue queueInfo= Volley.newRequestQueue(LoginActivity.this);
                    queueInfo.add(loginInfoRequest);

                }



            }



        });

        thread.start();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_MAIN){
            if(resultCode==RESULT_OK){
            }
        }
    }
    private View.OnClickListener registerButtonListener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent registerIntent=new Intent(LoginActivity.this,RegisterActivity.class);
            LoginActivity.this.startActivity(registerIntent);
        }
    };
    private View.OnClickListener loginButtonListener= new View.OnClickListener() {
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

                                            //싱글톤객체
                                            GetInitialSingletonUser(userId,userPassword);


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
    };

}


