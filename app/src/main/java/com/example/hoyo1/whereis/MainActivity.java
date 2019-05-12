package com.example.hoyo1.whereis;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntRange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {


    // 호용 20190322 : main에서의 요청은 100대
    public static final int REQUEST_GROUP_ADD = 101;
    public static final int REQUEST_GROUP = 102;
    public static final int MAX_GROUP_LIST= 10000;
    public static final int MAX_GROUP_LEADER=30000;
    public static final int MAX_GROUP_CATEGORY=50000;
    public static final int MAX_GROUP_LEADER_NO=70000;

    //메시지모음
    public static final int AM_GROUP_LIST_CREATE=20000;
    public static int viewWidth;
    public static int viewHeight;

    boolean bIsResponseCheck=false;
    ImageView userImageView;
    TextView userNameTextView;
    ListView listView;
    SingerAdapter adapter;
    TextView groupSubTitleTextView;
    Handler handlerGroupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("그룹");
        setContentView(R.layout.activity_main);

        init();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //SingerItem item=(SingerItem)adapter.getItem(position);
                //리스트순서는 0부터 시작이다.
                //Intent intent=new Intent(getApplicationContext(),GroupActivity.class);
                Intent intent=new Intent(getApplicationContext(),Group2Activity.class);
                intent.putExtra("key", (position+1));


                startActivityForResult(intent,REQUEST_GROUP);
            }
        });


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
            case R.id.searchMenu:
                //검색액티비티
                break;
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
                //그룹추가 완료


                Toast.makeText(MainActivity.this,"테스트",Toast.LENGTH_LONG).show();
                //그룹아이디리퀘스트(그룹리스트싱글톤에 리스트저장), 그룹리스트 리로드
                GetGroupList();




            }
            else if(resultCode==RESULT_CANCELED){
                //그룹추가 취소
            }
        }
        else if(requestCode==REQUEST_GROUP){
            //그룹메인
            if(resultCode==RESULT_OK)
                GetGroupList();

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    public void init(){
        groupSubTitleTextView=(TextView)findViewById(R.id.groupSubTitleText);
        listView=(ListView)findViewById(R.id.listView);
        userImageView=(ImageView)findViewById(R.id.profileImage);
        userNameTextView=(TextView)findViewById(R.id.profileText);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        viewWidth = size.x;
        viewHeight = size.y;

        //개인프로필이름 표시
        userNameTextView.setText(SingletonUser.getInstance().getUserId());
        //개인프로필사진 표시
        adapter = new SingerAdapter(getApplicationContext());

        handlerGroupList=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch(msg.what){

                    case AM_GROUP_LIST_CREATE:
                        //그룹리스트 동적생성

                        LoadList();


                        break;
                }

            }
        };

        //그룹아이디리퀘스트(그룹리스트싱글톤에 리스트저장)
        GetGroupList();










        /////////////////////////////////////////////////////////////////////////////////////
        //초기화시작
        /////////////////////////////////////////////////////////////////////////////////////



        //리스트동적생성 완료
        //adapter = new SingerAdapter(getApplicationContext());

        /*
        int nGroupListSize=SingletonGroupList.getInstance().getGroupCount();

        //리스트동적생성 시작
        for(int nGroupListCnt=0;nGroupListCnt<nGroupListSize;nGroupListCnt++)
        {
            String groupName,groupLeaderName;
            groupName=SingletonGroupList.getInstance().getGroupName(nGroupListCnt);
            groupLeaderName=SingletonGroupList.getInstance().getGroupLeader(nGroupListCnt);
            adapter.addItem(new SingerItem(groupName,R.drawable.ic_group_black_24dp,groupLeaderName,R.drawable.ic_person_black_24dp));

        }


        // 호용 20190328 : 서버로부터 응답이 늦게 와서 리스트가 안만들어진다... 스레드를 써야할것같다.
        */
        /*
        //리스트초기화
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp,"그룹리더",R.drawable.ic_person_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp,"그룹리더",R.drawable.ic_person_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp,"그룹리더",R.drawable.ic_person_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp,"그룹리더",R.drawable.ic_person_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp,"그룹리더",R.drawable.ic_person_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp,"그룹리더",R.drawable.ic_person_black_24dp));
        listView.setAdapter(adapter);
        groupSubTitleTextView.setText("그룹"+"("+adapter.getCount()+")");
        */

        //그룹소제목초기화

        /////////////////////////////////////////////////////////////////////////////////////
        //초기화끝
        /////////////////////////////////////////////////////////////////////////////////////
    }

    public void GetGroupList(){


        //서브스레드 생성 및 서버와 통신

        Thread threadGroupList=new Thread(new Runnable() {

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
                                        String strGroupName = Integer.toString(nNo);
                                        String strGroupLeaderName = Integer.toString(nNo + MAX_GROUP_LIST);
                                        String strGroupID=Integer.toString(nNo+MAX_GROUP_LEADER);
                                        String strGroupCategory=Integer.toString(nNo+MAX_GROUP_CATEGORY);
                                        String strGroupLeaderNo=Integer.toString(nNo+MAX_GROUP_LEADER_NO);

                                        String groupName = jsonResponse2.getString(strGroupName);
                                        String groupLeaderName = jsonResponse2.getString(strGroupLeaderName);
                                        String groupID= jsonResponse2.getString(strGroupID);
                                        String groupCategory=jsonResponse2.getString(strGroupCategory);
                                        String groupLeaderNo=jsonResponse2.getString(strGroupLeaderNo);
                                        //key=그룹리스트1-n까지
                                        SingletonGroupList.getInstance().setGroupList(nNo,groupID, groupName, groupLeaderName,groupLeaderNo,groupCategory);
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

        threadGroupList.start();


    }

    public void LoadList(){
        //리스트동적생성 시작
        int nGroupListSize=SingletonGroupList.getInstance().getGroupCount();
        adapter.removeAll();

        for(int nGroupListCnt=1;nGroupListCnt<=nGroupListSize;nGroupListCnt++)
        {
            String groupName,groupLeaderName;
            groupName=SingletonGroupList.getInstance().getGroupName(nGroupListCnt);
            groupLeaderName=SingletonGroupList.getInstance().getGroupLeader(nGroupListCnt);
            adapter.addItem(new SingerItem(groupName,R.drawable.ic_group_black_24dp,groupLeaderName,R.drawable.ic_person_black_24dp));

        }





        listView.setAdapter(adapter);
        groupSubTitleTextView.setText("그룹"+"("+adapter.getCount()+")");

    }
}