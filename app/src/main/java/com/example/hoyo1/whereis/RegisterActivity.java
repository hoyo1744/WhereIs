package com.example.hoyo1.whereis;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.example.hoyo1.whereis.R.color.colorGray;
import static com.example.hoyo1.whereis.R.id.idText;
import static com.example.hoyo1.whereis.R.id.nameText;

public class RegisterActivity extends AppCompatActivity {


    private String userID;
    private String userPassword;
    private String userEmail;
    private String userName;
    private String userGenger;
    private String userPhoneNumber;
    private AlertDialog dialog;
    private boolean validate=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //findViewById는 레이아웃에 정의된 객체를 참조하기 위한 함수
        final EditText idText=(EditText) findViewById((R.id.idText));
        final EditText passwordText=(EditText) findViewById((R.id.passwordText));
        final EditText emailText=(EditText) findViewById((R.id.emailText));
        final EditText nameText=(EditText)findViewById(R.id.nameText);
        final EditText phoneNumberText=(EditText)findViewById(R.id.phoneNumberTextText);

        RadioGroup genderGroup=(RadioGroup)findViewById((R.id.genderGroup));
        int genderGroupID=genderGroup.getCheckedRadioButtonId();
        userGenger=((RadioButton)findViewById(genderGroupID)).getText().toString();


        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton genderButton=(RadioButton)findViewById(checkedId);
                userGenger=genderButton.getText().toString();

            }
        });

        final Button validateButton=(Button)findViewById(R.id.validateButton);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID=idText.getText().toString();
                if(validate)
                    return ;

                if(userID.equals("")) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
                    dialog=builder.setMessage("아이디는 빈 칸일 수 없습니다.")
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
                            boolean success=jsonObject.getBoolean("success");
                            if(success){
                                AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
                                dialog=builder.setMessage("사용할 수 있는 아이디입니다.")
                                        .setPositiveButton("확인",null)
                                        .create();
                                dialog.show();
                                idText.setEnabled(false);
                                validate=true;
                                idText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                validateButton.setBackgroundColor(getResources().getColor(R.color.colorGray));


                            }
                            else{

                                AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
                                dialog=builder.setMessage("사용할 수 없는 아이디입니다.")
                                        .setPositiveButton("확인",null)
                                        .create();
                                dialog.show();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                       }
                    }
                };



                ValidateRequest validateRequest=new ValidateRequest(userID,responListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);
            }
        });


        Button registerButton=(Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID=idText.getText().toString();
                String userPassword=passwordText.getText().toString();
                String userEmail=emailText.getText().toString();
                String userName=nameText.getText().toString();
                String userPhoneNumber=phoneNumberText.getText().toString();

                Toast.makeText(RegisterActivity.this, userGenger, Toast.LENGTH_SHORT).show();

                if(!validate)
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
                    dialog=builder.setMessage("아이디 중복체크를 해야합니다.")
                            .setPositiveButton("확인",null)
                            .create();
                    dialog.show();
                    return ;
                }

                if(userID.equals("") || userPassword.equals("") || userEmail.equals("") ||  userName.equals("") || userPhoneNumber.equals(""))
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
                    dialog=builder.setMessage("빈칸이 남아있습니다.")
                            .setPositiveButton("확인",null)
                            .create();
                    dialog.show();
                    return ;
                }
                Response.Listener<String> responListener=new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            boolean success=jsonObject.getBoolean("success");
                            if(success){
                                    AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
                                    dialog=builder.setMessage("회원가입이 완료되었습니다.")
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            finish();
                                                        }
                                                    }
                                            )
                                            .create();
                                    dialog.show();



                            }
                            else{
                                AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
                                dialog=builder.setMessage("회원등록에 실패했습니다.")
                                        .setPositiveButton("확인",null)
                                        .create();
                                dialog.show();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                RegisterRequest registerRequest=new RegisterRequest(userID,userPassword,userEmail,userName,userGenger,userPhoneNumber,responListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);




            }
        });
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
}
