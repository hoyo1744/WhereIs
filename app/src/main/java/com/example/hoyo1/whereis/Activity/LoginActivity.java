package com.example.hoyo1.whereis.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hoyo1.whereis.Common.CustomLoadingDialog;
import com.example.hoyo1.whereis.Common.SaveSharedPreference;
import com.example.hoyo1.whereis.R;
import com.example.hoyo1.whereis.Request.LoginInfoRequest;
import com.example.hoyo1.whereis.Request.LoginRequest;
import com.example.hoyo1.whereis.Singleton.SingletonSocket;
import com.example.hoyo1.whereis.Singleton.SingletonUser;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;


public class LoginActivity extends AppCompatActivity {

    //핸들메시지
    private final static int AM_LOGIN_SUCCESS=10000;


    //로그인액티비티컨텍스트
    public static Context loginContext;



    private CustomLoadingDialog customLoadingDialog;
    private TextView informationTextView;
    private CheckBox autoLoginCheckBox;
    private TextView registerButton;
    private SharedPreferences auto;
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

        //로그인컨텍스트
        loginContext=this;

        //액션바 및 타이틀바 설정
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        //객체참조
        registerButton=(TextView) findViewById(R.id.registerButton);
        idText=(EditText)findViewById(R.id.idText);
        passwordText=(EditText)findViewById(R.id.passwordText);
        loginButton=(Button)findViewById(R.id.loginButton);
        informationTextView=(TextView)findViewById(R.id.information);
        autoLoginCheckBox=(CheckBox)findViewById(R.id.autoLogin);

        //리스너연결
        registerButton.setOnClickListener(registerButtonListener);
        loginButton.setOnClickListener(loginButtonListener);
        informationTextView.setOnClickListener(informationListener);


        //핸들러
        handlerLogin=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==AM_LOGIN_SUCCESS){

                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    idText.setText("");
                    passwordText.setText("");


                    //소켓연결 및 이벤트 연결
                    SingletonSocket.getInstance().on("response",SingletonSocket.getInstance().onResponse);
                    SingletonSocket.getInstance().on("message",SingletonSocket.getInstance().onExecute);




                    //로그인메시지
                    SingletonSocket.getInstance().sendLoginMessage();

                    //로딩완료
                    customLoadingDialog.dismiss();

                    //startActivityForResult(intent,REQUEST_MAIN);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }




    public void GetInitialSingletonUser(final String userId, final String userPassword){
        customLoadingDialog = new CustomLoadingDialog(LoginActivity.this);
        customLoadingDialog.show();


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


    }

    private View.OnClickListener informationListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(getApplicationContext(),InformationActivity.class);
            startActivity(intent);
        }
    };
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
            customLoadingDialog=new CustomLoadingDialog(LoginActivity.this);
            customLoadingDialog.show();
            final String userId=idText.getText().toString();
            final String userPassword=passwordText.getText().toString();
            if(autoLoginCheckBox.isChecked()){

                SaveSharedPreference.setUserInfo(LoginActivity.this,userId,userPassword);
            }





            Response.Listener<String> responseLister= new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonResponse=new JSONObject(response);
                        boolean success=jsonResponse.getBoolean("success");
                        if(success)
                        {
                            customLoadingDialog.dismiss();
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


    public void KillApp(){
        ActivityCompat.finishAffinity(this);
        System.runFinalizersOnExit(true);
        System.exit(0);
    }
    public void ShowErrorMessage(String content){
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

        //대화상자설정
        builder.setTitle("에러");
        builder.setMessage(content);
        builder.setIcon(android.R.drawable.ic_dialog_alert);


        //예 버튼 추가
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                KillApp();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}


