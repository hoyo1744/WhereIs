package com.example.hoyo1.whereis;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                String userId=idText.getText().toString();
                String userPassword=passwordText.getText().toString();

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

                                                //로그인이 성공한다면 다음씬으로 넘어간다.
                                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                                LoginActivity.this.startActivity(intent);
                                                finish();

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
                RequestQueue queue=Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);


            }
        });
    }
}
