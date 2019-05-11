package com.example.hoyo1.whereis;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangeContent extends AppCompatActivity {

    public final static int AM_CHANGE_CONTENT_SUCCESS=1905071024;
    public final static int AM_CHANGE_CONTENT_FAIL=1905071025;
    TextView textViewBeforeContent;
    String strGroupID;
    String strUserID;
    Handler handlerChangeContent;
    Spinner spinner;
    ArrayAdapter<String> adapter;
    String strSelectedContent;

    int nCategoryNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("내용바꾸기");
        setContentView(R.layout.activity_change_content);
        //초기화
        Init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuID=item.getItemId();
        switch(menuID){

            case R.id.confirmMenu:
                //확인
                //저장작업시작
                String strBeforeContent=textViewBeforeContent.getText().toString();
                if(strBeforeContent.equals(strSelectedContent))
                {
                    //메시지박스
                    AlertDialog dialog;
                    AlertDialog.Builder builder=new AlertDialog.Builder(ChangeContent.this);
                    dialog=builder.setMessage("이전 내용과 동일합니다.")
                            .setPositiveButton("확인",null)
                            .create();
                    dialog.show();

                    break;
                }
                UpdateContent();

                break;

            case android.R.id.home:
                //취소
                setResult(RESULT_CANCELED);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Init(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_HOME_AS_UP | actionBar.DISPLAY_SHOW_TITLE);


        textViewBeforeContent=(TextView)findViewById(R.id.textViewbeforeContent);



        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        textViewBeforeContent.setText(data);
        strGroupID=intent.getStringExtra("GroupID");
        strUserID=intent.getStringExtra("UserID");
        nCategoryNum=intent.getIntExtra("CategoryNum",-1);
        spinner=(Spinner)findViewById(R.id.spinner);
        String strContent = Group2Activity.listGridContent.get(nCategoryNum);
        String[] items=strContent.split("#");
        //adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,items);
        adapter=new ArrayAdapter<String>(this,R.layout.spinner_item,items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //아이템 선택 이벤트 처리
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSelectedContent=adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                strSelectedContent="";
            }
        });


        if(nCategoryNum==-1)
        {
            //예외처리(제대로 값이 넘어오지 않았다.)
            return ;
        }


        handlerChangeContent=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch(msg.what){

                    case AM_CHANGE_CONTENT_SUCCESS:
                        //그룹리스트 동적생성


                        //저장작업완료
                        setResult(RESULT_OK);
                        finish();


                        break;
                    case AM_CHANGE_CONTENT_FAIL:
                        //예외처리


                        break;
                }

            }
        };
    }


    public void UpdateContent(){

        //스레드시작
        //서브스레드 생성 및 서버와 통신

        Thread threadGroupList=new Thread(new Runnable() {
            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;
                    Response.Listener<String> responseLister2 = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse2 = new JSONObject(response);
                                boolean success = jsonResponse2.getBoolean("success");

                                if (success) {
                                    //메시지보내기
                                    Message msg = handlerChangeContent.obtainMessage();
                                    msg.what = AM_CHANGE_CONTENT_SUCCESS;
                                    handlerChangeContent.sendMessage(msg);

                                }
                                else {
                                    //메시지보내기
                                    Message msg = handlerChangeContent.obtainMessage();
                                    msg.what = AM_CHANGE_CONTENT_FAIL;
                                    handlerChangeContent.sendMessage(msg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    String strCategoryNum=Integer.toString(nCategoryNum);

                    UpdateContentRequest updateContentRequest = new UpdateContentRequest(strUserID,strGroupID,strCategoryNum,strSelectedContent ,responseLister2);
                    RequestQueue queue2 = Volley.newRequestQueue(ChangeContent.this);
                    queue2.add(updateContentRequest);
                }



            }



        });

        threadGroupList.start();

    }


}
