package com.example.hoyo1.whereis.Singleton;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

public class SingletonSocket {


    //소켓
    private io.socket.client.Socket mSocket;

    public SingletonSocket(){
        try {
            mSocket = IO.socket("http://106.10.36.131:3000");
            mSocket.connect();
        } catch(URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public void emit(String event, JSONObject data){
        mSocket.emit(event,data);
    }
    public void on(String event, Emitter.Listener listener){
        mSocket.on(event,listener);
    }



    private static class SingletonSocketHolder{
        public static final SingletonSocket instance=new SingletonSocket();
    }

    public static SingletonSocket getInstance(){
        return  SingletonSocketHolder.instance;
    }
}
