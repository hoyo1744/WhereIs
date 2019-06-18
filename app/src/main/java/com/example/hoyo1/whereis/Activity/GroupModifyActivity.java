package com.example.hoyo1.whereis.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hoyo1.whereis.Common.CustomLoadingDialog;
import com.example.hoyo1.whereis.Common.GroupInfo;
import com.example.hoyo1.whereis.Common.subGroupAddCategory;
import com.example.hoyo1.whereis.R;
import com.example.hoyo1.whereis.Request.DeleteGroupContentRequest;
import com.example.hoyo1.whereis.Request.GroupCreateRequest;
import com.example.hoyo1.whereis.Request.GroupModifyRequest;
import com.example.hoyo1.whereis.Request.UpdateCategoryOfGroupMemberRequest;
import com.example.hoyo1.whereis.Singleton.SingletonSocket;
import com.example.hoyo1.whereis.Singleton.SingletonUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GroupModifyActivity extends AppCompatActivity {


    //핸들메시지
    public static final int AM_UPDATE_GROUP=50000;
    public static final int AM_DELETE_CONTENT=50001;
    public static final int AM_UPDATE_SUCCESS=50002;






    private ArrayList<subGroupAddCategory> listGroup=new ArrayList<>();
    private CustomLoadingDialog customLoadingDialog;
    private boolean bIsCreatedCategoryView;                          //커스텀뷰가 생성되었는지 확인
    private LinearLayout subGroupAdd;                                //커스텀뷰를 추가할 하위 레이아웃
    private Handler handler;
    private EditText groupLeaderNameEditText;
    private EditText groupCategoryEditText;
    private EditText groupNameEditText;
    private ImageButton minus;
    private ImageButton plus;


    private String groupName;
    private String groupLeaderName;
    private String groupCategory;
    private ArrayList<String> listHead;
    private ArrayList<String> listContent;
    private String groupLeaderID;
    private String groupID;
    private AlertDialog dialog;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("그룹수정");
        setContentView(R.layout.activity_group_modify);

        //현재 보고 있는 액티비티 표시.
        SingletonSocket.getInstance().setActivity(this);


        //초기화
        Init();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_groupadd,menu);
        return true;

    }
    public void Init(){
        //액션바 및 타이틀바 설정
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_HOME_AS_UP|actionBar.DISPLAY_SHOW_TITLE);

        bIsCreatedCategoryView=false;
        listGroup.clear();

        //객체참조
        groupCategoryEditText=(EditText)findViewById(R.id.groupCategoryEditTextForModify);
        groupLeaderNameEditText=(EditText) findViewById(R.id.groupLeaderNameEditTextForModify);
        groupNameEditText=(EditText)findViewById(R.id.groupNameEditTextForModify);
        subGroupAdd=(LinearLayout)findViewById(R.id.subForModify);
        minus=(ImageButton)findViewById(R.id.minusButton);
        plus=(ImageButton)findViewById(R.id.plusButton);
        listContent=new ArrayList<String>();
        listHead=new ArrayList<String>();


        //리스너참조
        //groupCategoryEditText.addTextChangedListener(groupCategoryEditTextWatcher);
        plus.setOnClickListener(plusButtonListener);
        minus.setOnClickListener(minusButtonListener);

        //데이터수신
        GetInfomationAboutIntent();

        //초기설정
        LoadSub();

        //그룹데이터로 설정하기
        SetGroupData();




        //핸들러
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch(msg.what){
                    case AM_UPDATE_GROUP:
                        //그룹수정으로 인한 데이터 초기화
                        InitialGroupContent();
                        break;
                    case AM_DELETE_CONTENT:
                        //group_member의 category변경
                        UpdateCategoryOfGroupMember();
                        break;
                    case AM_UPDATE_SUCCESS:
                        Intent resultIntent = new Intent();
                        int nCategory=Integer.parseInt(groupCategoryEditText.getText().toString());
                        resultIntent.putExtra("result",nCategory);
                        customLoadingDialog.dismiss();
                        setResult(RESULT_OK,resultIntent);
                        finish();
                        break;

                }
            }
        };



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId=item.getItemId();
        switch (menuId)
        {
            case R.id.confirmMenu:
                //빈칸확인 예외처리
                if(CheckEmptyBox())
                    break;
                //그룹수정
                UpdateGroup();
                break;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //빈칸체크 및 예외처리 함수
    public boolean CheckEmptyBox(){
        boolean res=false;

        if(groupLeaderNameEditText.getText().toString().equals("")){
            res=true;
            Toast.makeText(GroupModifyActivity.this,"그룹이름을 작성해주세요.",Toast.LENGTH_LONG).show();
            return res;
        }
        if(groupCategoryEditText.getText().toString().equals("") || groupCategoryEditText.getText().toString().equals("0") ){
            res=true;
            Toast.makeText(GroupModifyActivity.this,"카테고리수를 1개이상 입력해주세요.",Toast.LENGTH_LONG).show();
            return res;
        }
        for(int nIdx=0;nIdx<listGroup.size();nIdx++){
            if(listGroup.get(nIdx).getContent().equals("") || listGroup.get(nIdx).getHead().equals("")){
                res=true;
                Toast.makeText(GroupModifyActivity.this,"그룹의 헤더 또는 컨텐트가 1개 이상 비어있습니다.",Toast.LENGTH_LONG).show();
                return res;
            }
        }

        return res;
    }

    public void GetInfomationAboutIntent(){
        intent = getIntent();

        //기본 인텐트
        GroupInfo groupInfo=intent.getParcelableExtra("groupInfo");
        groupLeaderID = (intent.getExtras().getString("groupLeaderID"));
        groupID = (intent.getExtras().getString("groupID"));

        //직렬값
        groupName=groupInfo.groupName;
        groupLeaderName=groupInfo.groupLeaderName;
        groupCategory=groupInfo.groupCategoryNum;
        listHead= groupInfo.listHead;
        listContent=groupInfo.listContent;

        //데이터설정
        groupNameEditText.setText(groupName);
        groupLeaderNameEditText.setText(groupLeaderName);
        groupCategoryEditText.setText(groupCategory);


    }
    private TextWatcher groupCategoryEditTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //입력되는 텍스트에 변화가 생겼을 때

            String strEditText=s.toString();

            if(!IsNumberCheckEditText(strEditText))
            {
                AlertDialog.Builder builder=new AlertDialog.Builder(GroupModifyActivity.this);
                dialog=builder.setMessage("숫자만 입력할 수 있습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create();
                dialog.show();
                return ;
            }
            //공백일 경우 예외처리
            if(strEditText.equals(""))
            {
                if(bIsCreatedCategoryView) {
                    subGroupAdd.removeAllViews();
                    listGroup.clear();
                    bIsCreatedCategoryView = false;
                }
                return ;
            }
            if(Integer.parseInt(strEditText)>10){
                AlertDialog.Builder builder=new AlertDialog.Builder(GroupModifyActivity.this);
                dialog=builder.setMessage("10개 초과는 불가능합니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                groupCategoryEditText.setText("");
                            }
                        })
                        .create();
                dialog.show();
                return ;
            }


        }

        @Override
        public void afterTextChanged(Editable s) {
            //입력된 후에, 개수만큼 동적으로 텍스트뷰+에디트텍스트생성하기
            String strEditText = s.toString();
            //숫자가 아닌 경우 예외 처리
            if (!IsNumberCheckEditText(strEditText)) {
                //예외처리
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupModifyActivity.this);
                dialog = builder.setMessage("숫자만 입력할 수 있습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                dialog.show();
                return;

            }

            //공백일 경우 예외처리
            if(strEditText.equals(""))
            {
                //호용 20190317 : 여기도 예외처리가 필요할거같은데.. 뷰가 생성된 경우만!
                if(bIsCreatedCategoryView) {
                    subGroupAdd.removeAllViews();
                    listGroup.clear();
                    bIsCreatedCategoryView = false;
                }
                return ;
            }

            int nCategoryNum=Integer.parseInt(strEditText);
            bIsCreatedCategoryView=true;
            subGroupAdd.removeAllViews();
            listGroup.clear();
            for(int cnt=0;cnt<nCategoryNum;cnt++){
                subGroupAddCategory subGroupAddCategoryView=new subGroupAddCategory(getApplicationContext());
                listGroup.add(subGroupAddCategoryView);
                subGroupAdd.addView(subGroupAddCategoryView);
            }
        }
    };

    //호용 20190317 : 단어안에 숫자만 있는지 확인하는 함수
    public boolean IsNumberCheckEditText(String strEditText){
        for(int nIdx=0;nIdx<strEditText.length();nIdx++){
            char word=strEditText.charAt(nIdx);
            if(!(word>=48 && word<=57))
                return false;
        }
        return true;
    }
    //업데이트전 그룹정보로 채워넣기
    public void SetGroupData(){

        subGroupAdd.removeAllViews();
        int len=listGroup.size();


        for(int nIdx=0;nIdx<len;nIdx++){
            listGroup.get(nIdx).setHead(listHead.get(nIdx));
            listGroup.get(nIdx).setContent(listContent.get(nIdx));
            subGroupAdd.addView(listGroup.get(nIdx));
        }

    }



    public void UpdateGroup(){
        customLoadingDialog=new CustomLoadingDialog(GroupModifyActivity.this);
        customLoadingDialog.show();

        Thread thread=new Thread(new Runnable() {
            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;
                    ArrayList<String> categoryHead=new ArrayList<>();
                    ArrayList<String> categoryContent=new ArrayList<>();
                    String strGroupName=groupNameEditText.getText().toString();
                    String strGroupLeaderName=groupLeaderNameEditText.getText().toString();
                    String strGroupLeaderID= SingletonUser.getInstance().getUserId();
                    String strGroupCategoryNum=groupCategoryEditText.getText().toString();
                    for(int nIdx=1;nIdx<=Integer.parseInt(strGroupCategoryNum);nIdx++) {
                        categoryHead.add(listGroup.get(nIdx-1).getHead());
                        categoryContent.add(listGroup.get(nIdx-1).getContent());
                    }
                    GroupModifyRequest groupModifyRequest= new GroupModifyRequest(groupID,strGroupName,strGroupLeaderID,strGroupLeaderName,groupLeaderID,strGroupCategoryNum,categoryHead,categoryContent,responseModifyGroupListener);
                    RequestQueue queue3 = Volley.newRequestQueue(GroupModifyActivity.this);
                    queue3.add(groupModifyRequest);
                }
            }
        });
        thread.start();

    }
    //리스폰그룹멤버카테고리업데이트리스너
    private Response.Listener<String> responseUpdateCategoryOfGroupMemberListener= new Response.Listener<String>(){
        @Override
        public void onResponse(String response) {
            try {
                JSONObject responseLister3 = new JSONObject(response);
                boolean success = responseLister3.getBoolean("success");
                if (success ) {
                    Message msg = handler.obtainMessage();
                    msg.what = AM_UPDATE_SUCCESS;
                    handler.sendMessage(msg);
                }
                else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    //리스폰그룹컨텐트삭제리스너
    private Response.Listener<String> responseDeleteGroupContentListener = new Response.Listener<String>(){
        @Override
        public void onResponse(String response) {
            try {
                JSONObject responseLister3 = new JSONObject(response);
                boolean success = responseLister3.getBoolean("success");
                if (success ) {
                    Message msg = handler.obtainMessage();
                    msg.what = AM_DELETE_CONTENT;
                    handler.sendMessage(msg);
                }
                else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    //리스폰그룹생성리스너
    private Response.Listener<String> responseModifyGroupListener = new Response.Listener<String>(){
        @Override
        public void onResponse(String response) {
            try {
                JSONObject responseLister3 = new JSONObject(response);
                boolean success = responseLister3.getBoolean("success");
                if (success ) {
                    Message msg = handler.obtainMessage();
                    msg.what = AM_UPDATE_GROUP;
                    handler.sendMessage(msg);
                }
                else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    //플러스버튼리스너
    private View.OnClickListener plusButtonListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                String strCategory=groupCategoryEditText.getText().toString();
                int nCategory=Integer.parseInt(strCategory);
                nCategory+=1;
                if(nCategory>10){
                    nCategory=10;
                    AlertDialog.Builder builder=new AlertDialog.Builder(GroupModifyActivity.this);
                    dialog=builder.setMessage("10개 초과는 불가능합니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    dialog.show();
                    groupCategoryEditText.setText(Integer.toString(nCategory));
                    return ;
                }
                subGroupAddCategory subGroupAddCategoryView=new subGroupAddCategory(getApplicationContext());
                listGroup.add(subGroupAddCategoryView);
                subGroupAdd.addView(subGroupAddCategoryView);
                groupCategoryEditText.setText(Integer.toString(nCategory));
        }
    };
    //마이너스버튼리스너
    private View.OnClickListener minusButtonListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String strCategory=groupCategoryEditText.getText().toString();
            int nCategory=Integer.parseInt(strCategory);
            nCategory-=1;
            if(nCategory<0){
                nCategory=0;
                groupCategoryEditText.setText(Integer.toString(nCategory));
                return ;
            }
            listGroup.remove(nCategory);
            subGroupAdd.removeViewAt(nCategory);
            groupCategoryEditText.setText(Integer.toString(nCategory));
        }
    };


    public void LoadSub(){
        listGroup.clear();
        int nCategoryNum=Integer.parseInt(groupCategory);
        for(int cnt=0;cnt<nCategoryNum;cnt++){
            subGroupAddCategory subGroupAddCategoryView=new subGroupAddCategory(getApplicationContext());
            listGroup.add(subGroupAddCategoryView);
        }
    }

    public void InitialGroupContent(){
        Thread thread=new Thread(new Runnable() {
            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;

                    DeleteGroupContentRequest deleteGroupContentRequest= new DeleteGroupContentRequest(groupID,responseDeleteGroupContentListener);
                    RequestQueue queue3 = Volley.newRequestQueue(GroupModifyActivity.this);
                    queue3.add(deleteGroupContentRequest);
                }
            }
        });
        thread.start();

    }
    public void UpdateCategoryOfGroupMember(){
        Thread thread=new Thread(new Runnable() {
            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;

                    UpdateCategoryOfGroupMemberRequest updateCategryOfGroupMemberRequest= new UpdateCategoryOfGroupMemberRequest(groupID,groupCategoryEditText.getText().toString(),responseUpdateCategoryOfGroupMemberListener);
                    RequestQueue queue3 = Volley.newRequestQueue(GroupModifyActivity.this);
                    queue3.add(updateCategryOfGroupMemberRequest);
                }
            }
        });
        thread.start();
    }
}
