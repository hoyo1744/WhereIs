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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hoyo1.whereis.Common.CustomLoadingDialog;
import com.example.hoyo1.whereis.R;
import com.example.hoyo1.whereis.Request.AddGroupContentRequest;
import com.example.hoyo1.whereis.Request.AddGroupMemberRequest;
import com.example.hoyo1.whereis.Request.GroupCreateRequest;
import com.example.hoyo1.whereis.Singleton.SingletonUser;
import com.example.hoyo1.whereis.Common.subGroupAddCategory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class Member{
    public String groupID;
    public String groupCategory;
    public void setMember(String id,String category){
        this.groupID=id;
        this.groupCategory=category;
    }
    public String getGroupID(){
        return this.groupID;
    }
    public String getCategory(){
        return this.groupCategory;
    }

}


public class GroupAddActivity extends AppCompatActivity {

    //핸들러메시지
    static final int AM_GROUP_CATEGORY_CREATE=40000;
    static final int AM_GROUP_MEMBER_ADD=40001;
    static final int AM_GROUP_CONTENT_ADD=40002;



    private ArrayList<subGroupAddCategory> listGroup=new ArrayList<>();

    private CustomLoadingDialog customLoadingDialog;
    private boolean bIsCreatedCategoryView;                          //커스텀뷰가 생성되었는지 확인
    private EditText groupCategoryNumberEditText;
    private EditText groupLeaderNameEditText;
    private EditText groupNameEditText;
    private LinearLayout subGroupAdd;                                //커스텀뷰를 추가할 하위 레이아웃
    private Handler handlerCategory;
    private String groupLeaderID;
    private String createdGroupID;                                  //생성된 그룹아이디
    private AlertDialog dialog;
    private Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("그룹추가");
        setContentView(R.layout.activity_group_add);

        //액티비티초기화
        init();


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
                //빈칸확인 예외처리
                if(CheckEmptyBox())
                    break;



                //그룹생성
                CreateGroup();
                break;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void init(){
        //액션바 및 타이틀바 설정
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_HOME_AS_UP|actionBar.DISPLAY_SHOW_TITLE);
        bIsCreatedCategoryView=false;
        listGroup.clear();

        //객체참조
        groupCategoryNumberEditText=(EditText)findViewById(R.id.groupCategoryEditText);
        groupLeaderNameEditText=(EditText)findViewById(R.id.groupLeaderNameEditText);
        groupNameEditText=(EditText)findViewById(R.id.groupNameEditText);
        subGroupAdd=(LinearLayout)findViewById(R.id.subGroupAdd);

        //리스너참조
        groupCategoryNumberEditText.addTextChangedListener(groupCategoryEditTextWatcher);


        //액티비티객체 초기화
        groupLeaderNameEditText.setText(SingletonUser.getInstance().getUserName());
        groupLeaderNameEditText.setEnabled(false);

        //데이터수신
        GetInfomationAboutIntent();

        //핸들러
        handlerCategory=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch(msg.what){
                    case AM_GROUP_CATEGORY_CREATE:
                        AddGroupMember((Member) msg.obj);
                        break;
                    case AM_GROUP_MEMBER_ADD:
                        AddGroupContent((Member)msg.obj);
                        break;
                    case AM_GROUP_CONTENT_ADD:
                        //소켓그룹생성
                        ((LoginActivity)LoginActivity.loginContext).sendRoomMessage("create",createdGroupID);

                        setResult(RESULT_OK);

                        customLoadingDialog.dismiss();

                        finish();
                        break;
                }
            }
        };
    }

    //호용 20190317 : 단어안에 숫자만 있는지 확인하는 함수
    public boolean IsNumberCheckEditText(String strEditText){
        for(int nIdx=0;nIdx<strEditText.length();nIdx++){
            char word=strEditText.charAt(nIdx);
            if(!(word>=48 && word<=57))
                return false;
        }
        return true;
    }

    public void CreateGroup(){

        customLoadingDialog=new CustomLoadingDialog(GroupAddActivity.this);
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
                    String strGroupLeaderID=SingletonUser.getInstance().getUserId();
                    String strGroupCategoryNum=groupCategoryNumberEditText.getText().toString();
                    for(int nIdx=1;nIdx<=Integer.parseInt(strGroupCategoryNum);nIdx++) {
                        categoryHead.add(listGroup.get(nIdx-1).getHead());
                        categoryContent.add(listGroup.get(nIdx-1).getContent());
                    }
                    GroupCreateRequest groupCreateRequest = new GroupCreateRequest(strGroupName,strGroupLeaderID,strGroupLeaderName,groupLeaderID,strGroupCategoryNum,categoryHead,categoryContent,responseCreateGroupListener);
                    RequestQueue queue3 = Volley.newRequestQueue(GroupAddActivity.this);
                    queue3.add(groupCreateRequest);
                }
            }
        });
        thread.start();
    }

    public void AddGroupContent(Member member){

        final String groupID=member.getGroupID();
        final String groupCategory=member.getCategory();
        final String userNo=SingletonUser.getInstance().getUserNumber();
        final String userPriv="1";
        Thread thread=new Thread(new Runnable() {
            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;
                    //그룹리더니까 userPriv=1
                    AddGroupContentRequest groupContentRequest = new AddGroupContentRequest(groupID,userNo,groupCategory,userPriv,responseAddGroupContentListener);
                    RequestQueue queue3 = Volley.newRequestQueue(GroupAddActivity.this);
                    queue3.add(groupContentRequest);
                }
            }
        });
        thread.start();
    }

    public void AddGroupMember(Member member){

        final String groupID=member.getGroupID();
        final String groupCategory=member.getCategory();
        final String userNo=SingletonUser.getInstance().getUserNumber();
        final String userID=SingletonUser.getInstance().getUserId();
        final String userPriv="1";

        //서브스레드 생성 및 서버와 통신
        Thread thread=new Thread(new Runnable() {

            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;

                    Response.Listener<String> responseGroupMemberLister = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject responseLister3 = new JSONObject(response);
                                boolean success = responseLister3.getBoolean("success");
                                if (success) {



                                    Member memberGroup=new Member();
                                    memberGroup.setMember(groupID,groupCategory);
                                    Message msg = handlerCategory.obtainMessage();
                                    msg.obj=memberGroup;
                                    msg.what = AM_GROUP_MEMBER_ADD;
                                    handlerCategory.sendMessage(msg);


                                }
                                else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    AddGroupMemberRequest groupCreateRequest = new AddGroupMemberRequest(groupID,userNo,userID,userPriv,groupCategory,responseGroupMemberLister);
                    RequestQueue queue3 = Volley.newRequestQueue(GroupAddActivity.this);
                    queue3.add(groupCreateRequest);
                }
            }
        });

        thread.start();


    }

    //리스폰그룹컨텐트리스너
    private Response.Listener<String> responseAddGroupContentListener = new Response.Listener<String>(){
        @Override
        public void onResponse(String response) {
            try {
                JSONObject responseLister3 = new JSONObject(response);
                boolean success = responseLister3.getBoolean("success");
                if (success) {





                    Message msg = handlerCategory.obtainMessage();
                    msg.what = AM_GROUP_CONTENT_ADD;
                    handlerCategory.sendMessage(msg);


                }
                else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    //리스폰그룹생성리스너
    private Response.Listener<String> responseCreateGroupListener = new Response.Listener<String>(){
        @Override
        public void onResponse(String response) {
            try {
                JSONObject responseLister3 = new JSONObject(response);
                boolean success = responseLister3.getBoolean("success");
                boolean successAddMember=responseLister3.getBoolean("successAddMember");
                if (success && successAddMember) {

                    String groupID=responseLister3.getString("groupID");
                    String groupCategory=responseLister3.getString("groupCategory");




                    Member memberGroup=new Member();
                    createdGroupID=groupID;
                    memberGroup.setMember(groupID,groupCategory);
                    Message msg = handlerCategory.obtainMessage();
                    msg.obj=memberGroup;
                    msg.what = AM_GROUP_CATEGORY_CREATE;
                    handlerCategory.sendMessage(msg);

                }
                else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
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
                AlertDialog.Builder builder=new AlertDialog.Builder(GroupAddActivity.this);
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
                AlertDialog.Builder builder=new AlertDialog.Builder(GroupAddActivity.this);
                dialog=builder.setMessage("10개 초과는 불가능합니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                groupCategoryNumberEditText.setText("");
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
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupAddActivity.this);
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

    public void GetInfomationAboutIntent(){
        intent = getIntent(); /*데이터 수신*/
        groupLeaderID = (intent.getExtras().getString("groupLeaderID"));
    }


    public boolean CheckEmptyBox(){
        boolean res=false;

        if(groupLeaderNameEditText.getText().toString().equals("")){
            res=true;
            Toast.makeText(GroupAddActivity.this,"그룹이름을 작성해주세요.",Toast.LENGTH_LONG).show();
            return res;
        }
        if(groupCategoryNumberEditText.getText().toString().equals("") || groupCategoryNumberEditText.getText().toString().equals("0") ){
            res=true;
            Toast.makeText(GroupAddActivity.this,"카테고리수를 1개이상 입력해주세요.",Toast.LENGTH_LONG).show();
            return res;
        }
        for(int nIdx=0;nIdx<listGroup.size();nIdx++){
            if(listGroup.get(nIdx).getContent().equals("") || listGroup.get(nIdx).getHead().equals("")){
                res=true;
                Toast.makeText(GroupAddActivity.this,"그룹의 헤더 또는 컨텐트가 1개 이상 비어있습니다.",Toast.LENGTH_LONG).show();
                return res;
            }
        }

        return res;
    }
}



