package com.example.hoyo1.whereis.Common;

import android.app.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hoyo1.whereis.Activity.ChangeContent;
import com.example.hoyo1.whereis.Activity.Group2Activity;
import com.example.hoyo1.whereis.Activity.LoginActivity;
import com.example.hoyo1.whereis.Activity.Main2Activity;
import com.example.hoyo1.whereis.Activity.MainActivity;
import com.example.hoyo1.whereis.R;
import com.example.hoyo1.whereis.Request.LoginInfoRequest;
import com.example.hoyo1.whereis.Singleton.SingletonSocket;
import com.example.hoyo1.whereis.Singleton.SingletonUser;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;


public class SplashScreen extends Activity {



    //핸들러메시지
    public final static int AM_SPLASH_LOGIN_SUCCESS=100000;
    public final static int AM_SPLASH_LOGIN_FAIL=100001;


    //액티비티요청메시지
    private final static int REQUEST_MAIN=100;


    public CustomLoadingDialog customLoadingDialog;
    public static Context splashContext;
    public Handler handlerSplash;
    public Thread splashTread;
    public Thread timerThread;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        Init();

        splashContext=this;
        SingletonSocket.getInstance().setActivity(this);
        StartAnimations();
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l =(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash);
        iv.clearAnimation();
        iv.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }

                    if(SaveSharedPreference.getUserName(SplashScreen.this)==null  || SaveSharedPreference.getUserPassword(SplashScreen.this)==null  ){
                        Intent intent = new Intent(SplashScreen.this,
                                LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        SplashScreen.this.finish();
                    }else{
                        GetInitialSingletonUser(SaveSharedPreference.getUserName(SplashScreen.this),SaveSharedPreference.getUserPassword(SplashScreen.this));
                    }



                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        };
        splashTread.start();
    }


    public void GetInitialSingletonUser(final String userID, final String userPassword){


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



                                    //소켓연결 및 이벤트 연결
                                    SingletonSocket.getInstance().on("response",SingletonSocket.getInstance().onResponse);
                                    SingletonSocket.getInstance().on("message",SingletonSocket.getInstance().onExecute);

                                    //Intent intent=new Intent(getApplicationContext(),MainActivity.class);


                                    customLoadingDialog=new CustomLoadingDialog(SplashScreen.this);
                                    customLoadingDialog.show();




                                    //로그인메시지
                                    SingletonSocket.getInstance().sendLoginMessage();

                                    //타이머스레드시작
                                    startTimer();


                                    //startActivityForResult(intent,REQUEST_MAIN);
                                    //SplashScreen.this.finish();

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

                    LoginInfoRequest loginInfoRequest=new LoginInfoRequest(userID,userPassword,responseLister);
                    RequestQueue queueInfo= Volley.newRequestQueue(SplashScreen.this);
                    queueInfo.add(loginInfoRequest);

                }



            }



        });

        thread.start();



    }

    public void KillApp(){
        ActivityCompat.finishAffinity(this);
        System.runFinalizersOnExit(true);
        System.exit(0);
    }

    public void Init(){
        //핸들러
        handlerSplash=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (handlerSplash.obtainMessage().what){

                    case AM_SPLASH_LOGIN_SUCCESS:
                        timerThread.stop();
                        Intent intent=new Intent(getApplicationContext(),Main2Activity.class);
                        customLoadingDialog.dismiss();
                        startActivityForResult(intent,REQUEST_MAIN);
                        SplashScreen.this.finish();
                        break;

                    case AM_SPLASH_LOGIN_FAIL:
                        AlertDialog dialog;
                        AlertDialog.Builder builder=new AlertDialog.Builder(SplashScreen.this);
                        dialog=builder.setMessage("서버와 연결에 실패했습니다.")
                                .setPositiveButton("확인",null)
                                .create();
                        dialog.show();
                        break;

                }

            }
        };
    }

    public void startTimer(){

        timerThread= new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3000) {
                        sleep(100);
                        waited += 100;
                    }

                    Message msg=handlerSplash.obtainMessage();
                    msg.what=SplashScreen.AM_SPLASH_LOGIN_FAIL;
                    handlerSplash.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timerThread.start();


    }

}
