package com.example.hoyo1.whereis;

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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

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


    static final int AM_GROUP_CATEGORY_CREATE=30000;
    static final int AM_GROUP_MEMBER_ADD=30001;
    static final int AM_GROUP_CONTENT_ADD=30002;

    //호용 20190317 : 그룹카테고리 추가전,후,나누기(아직 작업x)
    private enum GroupAddState{

    };
    ArrayList<subGroupAddCategory> listGroup=new ArrayList<>();

    private boolean bIsCreatedCategoryView; //커스텀뷰가 생성되었는지 확인
    private AlertDialog dialog;
    EditText groupNameEditText;
    EditText groupLeaderNameEditText;
    EditText groupCategoryNumberEditText;
    LinearLayout subGroupAdd;               //커스텀뷰를 추가할 하위 레이아웃
    Handler handlerCategory;
    String groupLeaderID;
    Intent intent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("그룹추가");
        setContentView(R.layout.activity_group_add);

        //액티비티초기화
        init();

        groupCategoryNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력되기 전에

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력되는 텍스트에 변화가 생겼을 때

                String strEditText=s.toString();
                if(!IsNumberCheckEditText(strEditText))
                {
                    //예외처리
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
                    /*
                    //예외처리
                    AlertDialog.Builder builder=new AlertDialog.Builder(GroupAddActivity.this);
                    dialog=builder.setMessage("공백은 불가능합니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    dialog.show();
                    */

                    //호용 20190317 : 여기도 예외처리가 필요할거같은데.. 뷰가 생성된 경우만!
                    if(bIsCreatedCategoryView) {
                        subGroupAdd.removeAllViews();
                        listGroup.clear();
                        bIsCreatedCategoryView = false;
                    }
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
                        /*
                        //예외처리
                        AlertDialog.Builder builder=new AlertDialog.Builder(GroupAddActivity.this);
                        dialog=builder.setMessage("공백은 불가능합니다.")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .create();
                        dialog.show();
                        */

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
                    for(int cnt=0;cnt<nCategoryNum;cnt++){
                        subGroupAddCategory subGroupAddCategoryView=new subGroupAddCategory(getApplicationContext());
                        listGroup.add(subGroupAddCategoryView);
                        subGroupAdd.addView(subGroupAddCategoryView);


                    }







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
                //예외처리 빈칸 없이 입력해주세요.
                //return ;



                //서브쓰레드생성 후,그룹생성
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
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_HOME_AS_UP|actionBar.DISPLAY_SHOW_TITLE);
        bIsCreatedCategoryView=false;
        groupCategoryNumberEditText=(EditText)findViewById(R.id.groupCategoryEditText);
        groupLeaderNameEditText=(EditText)findViewById(R.id.groupLeaderNameEditText);
        groupNameEditText=(EditText)findViewById(R.id.groupNameEditText);
        subGroupAdd=(LinearLayout)findViewById(R.id.subGroupAdd);

        groupLeaderNameEditText.setText(SingletonUser.getInstance().getUserId());
        //호용 : 수정불가하도록 추가해야함.

        intent = getIntent(); /*데이터 수신*/
        groupLeaderID = (intent.getExtras().getString("groupLeaderID"));

        listGroup.clear();



        handlerCategory=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch(msg.what){

                    case AM_GROUP_CATEGORY_CREATE:
                        //카테고리생성완료

                        //서브쓰레드생성 후,group_member추가
                        AddGroupMember((Member) msg.obj);
                        break;
                    case AM_GROUP_MEMBER_ADD:
                        //group_content추가(리더)
                        AddGroupContent((Member)msg.obj);


                        break;
                    case AM_GROUP_CONTENT_ADD:

                        setResult(RESULT_OK);
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

    //새로운 그룹생성
    public void CreateGroup(){
        //서브스레드 생성 및 서버와 통신
        Thread threadGroupList=new Thread(new Runnable() {

            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;
                    ArrayList<String> categoryHead=new ArrayList<>();
                    ArrayList<String> categoryContent=new ArrayList<>();
                    String strGroupName=groupNameEditText.getText().toString();
                    String strGroupLeaderName=groupLeaderNameEditText.getText().toString();
                    String strGroupCategoryNum=groupCategoryNumberEditText.getText().toString();


                    for(int nIdx=1;nIdx<=Integer.parseInt(strGroupCategoryNum);nIdx++) {
                        categoryHead.add(listGroup.get(nIdx-1).getHead());
                        categoryContent.add(listGroup.get(nIdx-1).getContent());
                    }


                    //서버와 통신
                    Response.Listener<String> responseLister3 = new Response.Listener<String>(){
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
                    GroupCreateRequest groupCreateRequest = new GroupCreateRequest(strGroupName,strGroupLeaderName,groupLeaderID,strGroupCategoryNum,categoryHead,categoryContent,responseLister3);
                    RequestQueue queue3 = Volley.newRequestQueue(GroupAddActivity.this);
                    queue3.add(groupCreateRequest);
                }
            }
        });

        threadGroupList.start();

    }

    public void AddGroupContent(Member member){

        final String groupID=member.getGroupID();
        final String groupCategory=member.getCategory();
        final String userNo=SingletonUser.getInstance().getUserNumber();
        final String userPriv="1";

        //서브스레드 생성 및 서버와 통신
        Thread threadGroupContent=new Thread(new Runnable() {

            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;

                    //서버와 통신
                    Response.Listener<String> responseLister3 = new Response.Listener<String>(){
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
                    //그룹리더니까 userPriv=1
                    AddGroupContentRequest groupContentRequest = new AddGroupContentRequest(groupID,userNo,groupCategory,userPriv,responseLister3);
                    RequestQueue queue3 = Volley.newRequestQueue(GroupAddActivity.this);
                    queue3.add(groupContentRequest);
                }
            }
        });

        threadGroupContent.start();
    }

    public void AddGroupMember(Member member){

        final String groupID=member.getGroupID();
        final String groupCategory=member.getCategory();
        final String userNo=SingletonUser.getInstance().getUserNumber();
        final String userID=SingletonUser.getInstance().getUserId();
        final String userPriv="1";

        //서브스레드 생성 및 서버와 통신
        Thread threadGroupMember=new Thread(new Runnable() {

            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;

                    //서버와 통신
                    Response.Listener<String> responseLister3 = new Response.Listener<String>(){
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
                    AddGroupMemberRequest groupCreateRequest = new AddGroupMemberRequest(groupID,userNo,userID,userPriv,groupCategory,responseLister3);
                    RequestQueue queue3 = Volley.newRequestQueue(GroupAddActivity.this);
                    queue3.add(groupCreateRequest);
                }
            }
        });

        threadGroupMember.start();


    }
}



