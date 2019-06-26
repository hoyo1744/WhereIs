package com.example.hoyo1.whereis.Singleton;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.hoyo1.whereis.Activity.Group2Activity;
import com.example.hoyo1.whereis.Activity.LoginActivity;
import com.example.hoyo1.whereis.Activity.Main2Activity;
import com.example.hoyo1.whereis.Activity.MainActivity;
import com.example.hoyo1.whereis.Common.SplashScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

public class SingletonSocket {


    //소켓
    private io.socket.client.Socket mSocket;
    private Activity activity;
    private boolean check;

    public SingletonSocket(){
        try {
            if(check==false) {
                check = true;
                mSocket = IO.socket("http://106.10.36.131:3000");
                mSocket.connect();

            }
        } catch(URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setActivity(Activity activity){this.activity=activity;}
    public void emit(String event, JSONObject data){
        mSocket.emit(event,data);
    }
    public void on(String event, Emitter.Listener listener){
        mSocket.on(event,listener);
    }

    public void sendLoginMessage(){
        JSONObject data=new JSONObject();

        try{
            data.put("No",SingletonUser.getInstance().getUserNumber());
            data.put("id",SingletonUser.getInstance().getUserId());
            SingletonSocket.getInstance().emit("login",data);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void sendLogoutMessage(){

        JSONObject data=new JSONObject();

        try{
            data.put("No",SingletonUser.getInstance().getUserNumber());
            data.put("id",SingletonUser.getInstance().getUserId());
            SingletonSocket.getInstance().emit("logout",data);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void sendInviteGroupMemberMessage(String userNo){
        JSONObject data=new JSONObject();
        try {
            data.put("sender",SingletonUser.getInstance().getUserNumber());
            data.put("recepient",userNo);
            data.put("command","individual");
            data.put("data","individual");
            SingletonSocket.getInstance().emit("message",data);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    public void sendGroupUpdateMessage(String groupID,int nCategoryNum){
        JSONObject data=new JSONObject();
        try {
            data.put("sender",SingletonUser.getInstance().getUserNumber());
            data.put("recepient",groupID);
            data.put("command","update");
            data.put("data",nCategoryNum);
            SingletonSocket.getInstance().emit("message",data);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
    public void sendGroupLeaderChangeMessage(String groupID,String leaderID){
        JSONObject data=new JSONObject();
        try {
            data.put("sender",SingletonUser.getInstance().getUserNumber());
            data.put("recepient",groupID);
            data.put("command","groupLeader");
            data.put("data",leaderID);
            SingletonSocket.getInstance().emit("message",data);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void sendGroupDeleteMessage(String groupID){
        JSONObject data=new JSONObject();
        try {
            data.put("sender",SingletonUser.getInstance().getUserNumber());
            data.put("recepient",groupID);
            data.put("command","delete");
            data.put("data","delete");
            SingletonSocket.getInstance().emit("message",data);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void sendDataChangeMessage(String groupID){
        JSONObject data=new JSONObject();
        try {
            data.put("sender",SingletonUser.getInstance().getUserNumber());
            data.put("recepient",groupID);
            data.put("command","group");
            data.put("data","group");
            SingletonSocket.getInstance().emit("message",data);
        }catch (JSONException e){
            e.printStackTrace();
        }


    }
    public void sendDeleteAlarmMessage(String groupID,String strParam){
        JSONObject data=new JSONObject();
        try {
            String param="";
            if(strParam=="groupUpdate"){
                param="이/가 수정되었습니다.";
            }else if(strParam=="delete"){
                param="이/가 삭제되었습니다";
            }else if(strParam=="leaderUpdate"){
                param="이/가 리더가 변경되었습니다.";
            }
            data.put("sender",SingletonUser.getInstance().getUserNumber());
            data.put("recepient",groupID);
            data.put("command","groupMemberAboutDelete");
            data.put("data",param);
            SingletonSocket.getInstance().emit("message",data);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void sendGroupUpdateAlarmMessage(String groupID,String strParam){
        JSONObject data=new JSONObject();
        try {
            String param="";
            if(strParam=="groupUpdate"){
                param="이/가 수정되었습니다.";
            }else if(strParam=="delete"){
                param="이/가 삭제되었습니다";
            }else if(strParam=="leaderUpdate"){
                param="이/가 리더가 변경되었습니다.";
            }
            data.put("sender",SingletonUser.getInstance().getUserNumber());
            data.put("recepient",groupID);
            data.put("command","groupMemberAboutGroupUpdate");
            data.put("data",param);
            SingletonSocket.getInstance().emit("message",data);
        }catch (JSONException e){
            e.printStackTrace();
        }


    }
    public void sendLeaderUpdateAlarmMessage(String groupID,String strParam){
        JSONObject data=new JSONObject();
        try {
            String param=strParam;

            data.put("sender",SingletonUser.getInstance().getUserNumber());
            data.put("recepient",groupID);
            data.put("command","groupMemberAboutLeaderUpdate");
            data.put("data",param);
            SingletonSocket.getInstance().emit("message",data);
        }catch (JSONException e){
            e.printStackTrace();
        }


    }
    public void sendRoomMessage(String strParam,String groupID){
        JSONObject data=new JSONObject();
        try{
            data.put("roomId",groupID);
            data.put("command",strParam);
            SingletonSocket.getInstance().emit("room",data);

        }catch(JSONException e){
            e.printStackTrace();
        }

    }





    public Emitter.Listener onResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject dataJson=(JSONObject)args[0];
            String code,message,command;

            try {
                command=dataJson.getString("command");
                code=dataJson.getString("code");
                message=dataJson.getString("message");



                //로그인응답처리
                if(command.equals("login")){
                    if(code.equals("200")){

                        if(activity.equals(LoginActivity.loginContext)){
                            //일반로그인
                            Message msg1=((LoginActivity)(LoginActivity.loginContext)).handlerLogin.obtainMessage();
                            msg1.what=((LoginActivity)(LoginActivity.loginContext)).AM_LOGIN_ALARM;
                            ((LoginActivity)(LoginActivity.loginContext)).handlerLogin.sendMessage(msg1);
                        }else if(activity.equals(SplashScreen.splashContext)){
                            //자동로그인
                            Message msg2=((SplashScreen)(SplashScreen.splashContext)).handlerSplash.obtainMessage();
                            msg2.what=((SplashScreen)(SplashScreen.splashContext)).AM_SPLASH_LOGIN_SUCCESS;
                            ((SplashScreen)(SplashScreen.splashContext)).handlerSplash.sendMessage(msg2);

                        }

                    }
                }

                //code에 따른 처리
                if(code.equals("404")) {
                    KillApp(activity);
                }
                else if(code.equals("403")){
                    //심각하지 않은 문제.
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    };


    public Emitter.Listener onExecute = new Emitter.Listener() {
        @Override
        public void call(Object... args) {


            JSONObject dataJson=(JSONObject)args[0];


            try {
                final String userNo=dataJson.getString("sender");
                final String groupNo=dataJson.getString("recepient");
                final String data=dataJson.getString("data");
                final String command=dataJson.getString("command");

                //message에 따른 처리
                if(command.equals("group")) {
                    activity.runOnUiThread(new Runnable() {
                        boolean isPlaying=false;
                        @Override
                        public void run() {
                            if(isPlaying==false){
                                isPlaying=true;
                                // 이벤트 수신 시 실행할 내용들
                                //if(userNo.equals(SingletonGroupList.getInstance()))
                                ((Group2Activity) Group2Activity.groupContext).LoadListUserAndUserContent();
                            }

                        }
                    });

                }
                else if(command.equals("individual")){
                    activity.runOnUiThread(new Runnable() {
                        boolean isPlaying=false;
                        @Override
                        public void run() {
                            if(isPlaying==false){
                                isPlaying=true;
                                // 이벤트 수신 시 실행할 내용들
                                ((Main2Activity)Main2Activity.mainContext).GetGroupList();
                            }

                        }
                    });


                }
                else if(command.equals("delete")){
                    activity.runOnUiThread(new Runnable() {
                        boolean isPlaying=false;
                        @Override
                        public void run() {
                            if(isPlaying==false){
                                isPlaying=true;
                                // 이벤트 수신 시 실행할 내용들
                                ((Group2Activity) Group2Activity.groupContext).GetOutOfDeleteGroup();
                            }

                        }
                    });
                }
                else if(command.equals("update")){
                    activity.runOnUiThread(new Runnable() {
                        boolean isPlaying=false;
                        @Override
                        public void run() {
                            if(isPlaying==false){
                                isPlaying=true;
                                // 이벤트 수신 시 실행할 내용들
                                Integer nTemp=Integer.parseInt(data);
                                ((Group2Activity) Group2Activity.groupContext).UpdateGroup(nTemp);
                            }

                        }
                    });
                }
                else if(command.equals("groupLeader")){
                    activity.runOnUiThread(new Runnable() {
                        boolean isPlaying=false;
                        @Override
                        public void run() {
                            if(isPlaying==false){
                                isPlaying=true;
                                // 이벤트 수신 시 실행할 내용들
                                ((Group2Activity) Group2Activity.groupContext).UpdateGroupLeader(data);
                            }

                        }
                    });
                }
                else if(command.equals("groupMemberAboutDelete")){
                    activity.runOnUiThread(new Runnable() {
                        boolean isPlaying=false;
                        @Override
                        public void run() {
                            if(isPlaying==false){
                                isPlaying=true;
                                // 이벤트 수신 시 실행할 내용들
                                //1.알림처리(보류)
                                ((Main2Activity) (Main2Activity.mainContext)).SaveAlarm(data, userNo, groupNo,"groupDelete");
                            }

                        }
                    });
                }
                else if(command.equals("groupMemberAboutGroupUpdate")){
                    activity.runOnUiThread(new Runnable() {
                        boolean isPlaying=false;
                        @Override
                        public void run() {
                            if(isPlaying==false){
                                isPlaying=true;
                                // 이벤트 수신 시 실행할 내용들
                                //1.알림처리(보류)
                                ((Main2Activity) (Main2Activity.mainContext)).SaveAlarm(data, userNo, groupNo,"groupUpdate");
                            }

                        }
                    });
                }
                else if(command.equals("groupMemberAboutLeaderUpdate")){
                    activity.runOnUiThread(new Runnable() {
                        boolean isPlaying=false;
                        @Override
                        public void run() {
                            if(isPlaying==false){
                                isPlaying=true;
                                // 이벤트 수신 시 실행할 내용들
                                //1.알림처리(보류)
                                ((Main2Activity) (Main2Activity.mainContext)).SaveAlarm(data, userNo, groupNo,"groupLeaderUpdate");
                            }

                        }
                    });
                }





            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    };


    public void KillApp(Activity activity){
        ActivityCompat.finishAffinity(activity);
        System.runFinalizersOnExit(true);
        System.exit(0);
    }


    private static class SingletonSocketHolder{
        public static final SingletonSocket instance=new SingletonSocket();
    }

    public static SingletonSocket getInstance(){
        return  SingletonSocketHolder.instance;
    }
}
