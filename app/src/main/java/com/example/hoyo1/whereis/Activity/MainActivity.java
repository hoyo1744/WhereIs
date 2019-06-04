package com.example.hoyo1.whereis.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hoyo1.whereis.Common.CustomLoadingDialog;
import com.example.hoyo1.whereis.Common.SaveSharedPreference;
import com.example.hoyo1.whereis.R;
import com.example.hoyo1.whereis.Request.GroupIdRequest;
import com.example.hoyo1.whereis.List.SingerAdapter;
import com.example.hoyo1.whereis.List.SingerItem;
import com.example.hoyo1.whereis.Singleton.SingletonGroupList;
import com.example.hoyo1.whereis.Singleton.SingletonUser;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {


    //요청메시지
    public static final int REQUEST_GROUP_ADD = 201;
    public static final int REQUEST_GROUP = 202;


    //데이터베이스 데이터 확인 상수
    public static final int MAX_GROUP_LIST= 10000;
    public static final int MAX_GROUP_LEADER=30000;
    public static final int MAX_GROUP_CATEGORY=50000;
    public static final int MAX_GROUP_LEADER_NO=70000;
    public static final int MAX_GROUP_LEADER_NAME=90000;

    //메시지모음
    public static final int AM_GROUP_LIST_CREATE=20000;

    //앱 화면 크기
    public static int viewWidth;
    public static int viewHeight;

    //메인액티비티컨텍스트
    public static Context mainContext;


    //객체
    private CustomLoadingDialog customLoadingDialog;
    private TextView groupSubTitleTextView;
    private TextView userNameTextView;
    private Handler handlerGroupList;
    private ImageView userImageView;
    private SingerAdapter adapter;
    private long backButtonPushTime;
    private ListView listView;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("그룹");
        setContentView(R.layout.activity_main);

        //메인액티비티컨텍스트
        mainContext=this;


        //애플리케이션 화면크기 초기화
        GetApplicationWidthAndHeight();

        //초기화
        init();





    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        getMenuInflater().inflate(R.menu.menu_main_sub,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId=item.getItemId();
        switch (menuId)
        {
            case R.id.groupAddMenu:
                //그룹추가액티비티
                //getApplicationContext : 애플리케이션 컨텍스트가져오기
                //GroupAddactivity.class에서 액티비티는 컨텍스트를 상속받았기 때문에
                //GroupAddActivity는 컨텍스트가 된다.
                Intent intent=new Intent(getApplicationContext(),GroupAddActivity.class);
                intent.putExtra("groupLeaderID", SingletonUser.getInstance().getUserNumber());
                startActivityForResult(intent,REQUEST_GROUP_ADD);
                break;
            case R.id.itemLogout:

                //소켓연결끊기
                ((LoginActivity)LoginActivity.loginContext).sendLogoutMessage();

                //자동로그인설정해제
                SaveSharedPreference.clearUserName(MainActivity.this);

                setResult(RESULT_OK);
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_GROUP_ADD){
            //요청받은 메시지처리(그룹추가)
            if(resultCode==RESULT_OK){


                //그룹아이디리퀘스트(그룹리스트싱글톤에 리스트저장), 그룹리스트 리로드
                GetGroupList();


                //생성된 그룹액티비티
                int position=adapter.getCount();
                Intent intent=new Intent(getApplicationContext(),Group2Activity.class);
                intent.putExtra("key", (position+1));
                startActivityForResult(intent,REQUEST_GROUP);

            }
            else if(resultCode==RESULT_CANCELED){
                //그룹추가 취소
            }
        }
        else if(requestCode==REQUEST_GROUP){
            //그룹메인
            if(resultCode==RESULT_OK)
                RestartActivity();

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    public void init(){

        //객체 참조
        groupSubTitleTextView=(TextView)findViewById(R.id.groupSubTitleText);
        userNameTextView=(TextView)findViewById(R.id.profileText);
        userImageView=(ImageView)findViewById(R.id.profileImage);
        listView=(ListView)findViewById(R.id.listView);

        //리스너참조
        listView.setOnItemClickListener(listViewListener);


        //객체 값 채워넣기
        userNameTextView.setText(SingletonUser.getInstance().getUserName());
        adapter = new SingerAdapter(getApplicationContext());

        handlerGroupList=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case AM_GROUP_LIST_CREATE:
                        //그룹리스트생성
                        LoadList();

                        break;
                }

            }
        };

        //전체 그룹리스트 싱글톤저장
        GetGroupList();
    }

    public void GetGroupList(){

        customLoadingDialog=new CustomLoadingDialog(MainActivity.this);
        customLoadingDialog.show();

        Thread thread=new Thread(new Runnable() {
            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;

                    String userNumber = SingletonUser.getInstance().getUserNumber();
                    Response.Listener<String> responseLister2 = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse2 = new JSONObject(response);
                                boolean success = jsonResponse2.getBoolean("success");

                                if (success) {
                                    SingletonGroupList.getInstance().Initialize();
                                    int size = jsonResponse2.getInt("size");
                                    //사이즈만큼 그룹아이디를 담을 수 있는 자료구조를 생각해야함.
                                    for (int nNo = 1; nNo <= size; nNo++) {
                                        if(SingletonGroupList.getInstance().checkExistGroup(nNo))
                                            continue;
                                        String strGroupName = Integer.toString(nNo);
                                        String strGroupLeaderID = Integer.toString(nNo + MAX_GROUP_LIST);
                                        String strGroupID=Integer.toString(nNo+MAX_GROUP_LEADER);
                                        String strGroupCategory=Integer.toString(nNo+MAX_GROUP_CATEGORY);
                                        String strGroupLeaderNo=Integer.toString(nNo+MAX_GROUP_LEADER_NO);
                                        String strGroupLeaderName=Integer.toString(nNo+MAX_GROUP_LEADER_NAME);

                                        String groupName = jsonResponse2.getString(strGroupName);
                                        String groupLeaderID = jsonResponse2.getString(strGroupLeaderID);
                                        String groupID= jsonResponse2.getString(strGroupID);
                                        String groupCategory=jsonResponse2.getString(strGroupCategory);
                                        String groupLeaderNo=jsonResponse2.getString(strGroupLeaderNo);
                                        String groupLeaderName=jsonResponse2.getString(strGroupLeaderName);
                                        //key=그룹리스트1-n까지
                                        SingletonGroupList.getInstance().setGroupList(nNo,groupID, groupName, groupLeaderID,groupLeaderNo,groupLeaderName,groupCategory);
                                    }


                                    Message msg = handlerGroupList.obtainMessage();
                                    msg.what = AM_GROUP_LIST_CREATE;
                                    handlerGroupList.sendMessage(msg);

                                }
                                else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    GroupIdRequest groupIDRequest = new GroupIdRequest(userNumber, responseLister2);
                    RequestQueue queue2 = Volley.newRequestQueue(MainActivity.this);
                    queue2.add(groupIDRequest);
                }
            }
        });

        thread.start();

    }

    public void LoadList(){
        //리스트동적생성 시작
        int nGroupListSize=SingletonGroupList.getInstance().getGroupCount();
        adapter.removeAll();

        for(int nGroupListCnt=1;nGroupListCnt<=nGroupListSize;nGroupListCnt++)
        {
            String groupName,groupLeaderID,groupLeaderName;
            groupName=SingletonGroupList.getInstance().getGroupName(nGroupListCnt);
            groupLeaderID=SingletonGroupList.getInstance().getGroupLeader(nGroupListCnt);
            groupLeaderName=SingletonGroupList.getInstance().getGroupLeaderName(nGroupListCnt);
            adapter.addItem(new SingerItem(groupName,R.drawable.ic_group_black_24dp,groupLeaderID,groupLeaderName,R.drawable.ic_person_black_24dp));
        }

        listView.setAdapter(adapter);
        groupSubTitleTextView.setText("그룹"+"("+adapter.getCount()+")");

        //로딩종료
        customLoadingDialog.dismiss();

    }

    public void RestartActivity(){
        /*
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        */
        this.recreate();
    }
    private AdapterView.OnItemClickListener listViewListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent=new Intent(getApplicationContext(),Group2Activity.class);
            intent.putExtra("key", (position+1));

            //소켓그룹참여
            String groupID=SingletonGroupList.getInstance().getGroupID(position+1);
            ((LoginActivity)LoginActivity.loginContext).sendRoomMessage("join",groupID);
            startActivityForResult(intent,REQUEST_GROUP);


        }
    };

    private void GetApplicationWidthAndHeight(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        viewWidth = size.x;
        viewHeight = size.y;
    }

    public void KillApp(){
        ActivityCompat.finishAffinity(this);
        System.runFinalizersOnExit(true);
        System.exit(0);
    }
    public void ShowErrorMessage(String content){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //대화상자설정
        builder.setTitle("에러");
        builder.setMessage(content);
        builder.setIcon(android.R.drawable.ic_dialog_alert);


        //예 버튼 추가
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                KillApp();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void ShowCloseMessage(String title,String content){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //대화상자설정
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setIcon(android.R.drawable.ic_dialog_alert);


        //예 버튼 추가
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                KillApp();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis()-backButtonPushTime>=2000){
            backButtonPushTime=System.currentTimeMillis();
        }else if(System.currentTimeMillis()-backButtonPushTime<2000){
            ShowCloseMessage("종료하기","종료하시겠습니까?");
        }
    }
}