package com.example.hoyo1.whereis.Activity;

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
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hoyo1.whereis.Common.CustomLoadingDialog;
import com.example.hoyo1.whereis.R;
import com.example.hoyo1.whereis.Request.UpdateContentRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangeContent extends AppCompatActivity {


    //핸들러메시지
    public final static int AM_CHANGE_CONTENT_SUCCESS=40001;
    public final static int AM_CHANGE_CONTENT_FAIL=40002;


    private CustomLoadingDialog customLoadingDialog;
    private TextView textViewBeforeContent;
    private ArrayAdapter<String> adapter;
    private Handler handlerChangeContent;
    private String strSelectedContent;
    private String strGroupID;
    private String strUserID;
    private int nCategoryNum;
    private Spinner spinner;





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
                String strBeforeContent=textViewBeforeContent.getText().toString();
                if(strBeforeContent.equals(strSelectedContent))
                {
                    AlertDialog dialog;
                    AlertDialog.Builder builder=new AlertDialog.Builder(ChangeContent.this);
                    dialog=builder.setMessage("이전 내용과 동일합니다.")
                            .setPositiveButton("확인",null)
                            .create();
                    dialog.show();
                    break;
                }

                customLoadingDialog=new CustomLoadingDialog(ChangeContent.this);
                customLoadingDialog.show();
                UpdateContent();
                customLoadingDialog.dismiss();
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

        //액션바 및 타이틀바 설정
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_HOME_AS_UP | actionBar.DISPLAY_SHOW_TITLE);

        //객체참조
        textViewBeforeContent=(TextView)findViewById(R.id.textViewbeforeContent);
        spinner=(Spinner)findViewById(R.id.spinner);

        //인텐트데이터참조
        GetInfomationAboutIntent();

        //nCategoryNum-1은 프로필빼야하기때문이다.
        String strContent = Group2Activity.listGridContent.get(nCategoryNum-1);
        String[] items=strContent.split("#");


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
            ((MainActivity)MainActivity.mainContext).ShowErrorMessage("컨텐트변경에러입니다.");
            return ;
        }

        //핸들러
        handlerChangeContent=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch(msg.what){

                    case AM_CHANGE_CONTENT_SUCCESS:
                        //저장작업완료
                        setResult(RESULT_OK);
                        finish();
                        break;
                    case AM_CHANGE_CONTENT_FAIL:
                        ((MainActivity)MainActivity.mainContext).ShowErrorMessage("컨텐트변경에 실패했습니다.");
                        break;
                }

            }
        };
    }
    public void UpdateContent(){
        Thread thread=new Thread(new Runnable() {
            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;
                    String strCategoryNum=Integer.toString(nCategoryNum);
                    UpdateContentRequest updateContentRequest = new UpdateContentRequest(strUserID,strGroupID,strCategoryNum,strSelectedContent ,responseUpdateContentLister);
                    RequestQueue queue2 = Volley.newRequestQueue(ChangeContent.this);
                    queue2.add(updateContentRequest);
                }
            }
        });
        thread.start();

    }
    public void GetInfomationAboutIntent(){
        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        textViewBeforeContent.setText(data);
        strGroupID=intent.getStringExtra("GroupID");
        strUserID=intent.getStringExtra("UserID");
        nCategoryNum=intent.getIntExtra("CategoryNum",-1);
    }
    //리스폰업데이트컨텐트리스너
    private Response.Listener<String> responseUpdateContentLister = new Response.Listener<String>(){
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

}
