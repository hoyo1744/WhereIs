package com.example.hoyo1.whereis.Activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hoyo1.whereis.Common.CustomLoadingDialog;
import com.example.hoyo1.whereis.R;
import com.example.hoyo1.whereis.Request.RegisterRequest;
import com.example.hoyo1.whereis.Request.ValidateRequest;

import org.json.JSONObject;

import static android.widget.Toast.makeText;

public class RegisterActivity extends AppCompatActivity {


    private CustomLoadingDialog customLoadingDialog;
    private boolean validate=false;
    private String userPhoneNumber;
    private String userPassword;
    private String userGenger;
    private String userEmail;
    private String userName;
    private String userID;
    private AlertDialog dialog;
    private EditText idText;
    private EditText passwordText;
    private EditText emailText;
    private EditText nameText;
    private EditText phoneNumberText;
    private RadioGroup genderGroup;
    private Button validateButton;
    private Button registerButton;






    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //액션바와 타이틀바 숨기기
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);


        //객체 초기화 및 참조
        Init();

    }

    public void Init(){
        //객체 참조
        phoneNumberText=(EditText)findViewById(R.id.phoneNumberTextText);
        passwordText=(EditText) findViewById((R.id.passwordText));
        registerButton=(Button)findViewById(R.id.registerButton);
        validateButton=(Button)findViewById(R.id.validateButton);
        genderGroup=(RadioGroup)findViewById((R.id.genderGroup));
        emailText=(EditText) findViewById((R.id.emailText));
        nameText=(EditText)findViewById(R.id.nameText);
        idText=(EditText) findViewById((R.id.idText));

        int genderGroupID=genderGroup.getCheckedRadioButtonId();
        userGenger=((RadioButton)findViewById(genderGroupID)).getText().toString();


        //리스너연결
        genderGroup.setOnCheckedChangeListener(genderGroupListener);
        validateButton.setOnClickListener(validateButtonListener);
        registerButton.setOnClickListener(registerButtonListener);
    }

    //라디오버튼체크리스너
    private RadioGroup.OnCheckedChangeListener genderGroupListener= new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton genderButton=(RadioButton)findViewById(checkedId);
            userGenger=genderButton.getText().toString();
        }
    };

    //벨리데잇리스폰리스너
    private Response.Listener<String> responValidateListener=new Response.Listener<String>() {
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
                    customLoadingDialog.dismiss();

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


    //벨리데잇버튼클릭리스너
    private View.OnClickListener validateButtonListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customLoadingDialog=new CustomLoadingDialog(RegisterActivity.this);
            customLoadingDialog.show();
            final String userID = idText.getText().toString();
            if (validate)
                return;

            if (userID.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                dialog = builder.setMessage("아이디는 빈 칸일 수 없습니다.")
                        .setPositiveButton("확인", null)
                        .create();
                dialog.show();
                return;
            }
            ValidateRequest validateRequest=new ValidateRequest(userID,responValidateListener);
            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
            queue.add(validateRequest);
        }

    };

    //레지스터리스폰리스너
    private Response.Listener<String> responRegisterListener=new Response.Listener<String>() {
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
                    customLoadingDialog.dismiss();
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

    //레지스터버튼클릭리스너
    private View.OnClickListener registerButtonListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customLoadingDialog=new CustomLoadingDialog(RegisterActivity.this);
            customLoadingDialog.show();
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

            RegisterRequest registerRequest=new RegisterRequest(userID,userPassword,userEmail,userName,userGenger,userPhoneNumber,responRegisterListener);
            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
            queue.add(registerRequest);

        }
    };

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
