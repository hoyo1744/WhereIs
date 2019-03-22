package com.example.hoyo1.whereis;

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

import org.json.JSONObject;
import org.w3c.dom.Text;

public class GroupMemberAddActivity extends AppCompatActivity {

    EditText memberNameEditText;
    Button memberNameValidateButton;
    private boolean validate=false;
    private AlertDialog dialog;

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
                            boolean success=jsonObject.getBoolean("success");
                            if(success==false){
                                AlertDialog.Builder builder=new AlertDialog.Builder(GroupMemberAddActivity.this);
                                dialog=builder.setMessage("아이디를 찾았습니다.")
                                        .setPositiveButton("확인",null)
                                        .create();
                                dialog.show();
                                //idText.setEnabled(false);
                                validate=true;
                                //validateButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                //idText.setBackgroundColor(getResources().getColor(R.color.colorGray));


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



                ValidateRequest validateRequest=new ValidateRequest(userID,responListener);
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
                    setResult(RESULT_OK);
                    finish();
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
