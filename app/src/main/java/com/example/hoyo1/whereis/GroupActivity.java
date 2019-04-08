package com.example.hoyo1.whereis;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.GridAdapter;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.GridTextAdapter;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.SingerProfileItem;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.SingerTextItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

import static com.example.hoyo1.whereis.MainActivity.AM_GROUP_LIST_CREATE;

public class GroupActivity extends AppCompatActivity {

    class UserInfo{
        private int nCategory;
        private String strUserID;
        private ArrayList<String> strArrContent=new ArrayList<>();


        public int getCategory(){
            return this.nCategory;
        }
        public String getUserID(){
            return this.strUserID;
        }
        public String getContent(int pos){
            return this.strArrContent.get(pos);
        }
        public void setCategory(int nParam){
            this.nCategory=nParam;
        }
        public void setUserID(String strParam){
            this.strUserID=strParam;
        }
        public void AddContent(String strParam){
            this.strArrContent.add(strParam);
        }

    }

    //그룹에서 요청은 200대
    public static final int  REQUEST_MEMBER_ADD = 201;

    //메시지모음(그룹: 4만대)
    public static final int AM_GROUP_LIST_INIT=40000;       //헤더 및 컨텐트 리스트 초기화
    public static final int AM_GROUP_USER_INIT=40001;       //그룹에 속한 유저 및 유저컨텐트 초기화

    GridViewWithHeaderAndFooter profileGrid;
    GridViewWithHeaderAndFooter textGrid;
    GridAdapter profileAdapter;
    GridTextAdapter textAdapter;
    ArrayList<String> listGridHead;
    ArrayList<String> listGridContent;
    ArrayList<UserInfo> listUserInfo;

    Intent intent;
    Handler handlerGroupList;
    int nCategoryNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTitle("그룹이름");   //호용 20190317 : 임시로 작성(그룹클릭시 그룹이름이 들어가야함.)
        setContentView(R.layout.activity_group);
        init();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group,menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId=item.getItemId();
        switch (menuId)
        {
            case R.id.searchGroupMenu:
                //그룹멤버검색
                break;
            case R.id.memberAddGroupMenu:
                //그룹멤버초대
                Intent intent=new Intent(getApplicationContext(),GroupMemberAddActivity.class);
                startActivityForResult(intent,REQUEST_MEMBER_ADD);
                break;
            case R.id.settingGroupMenu:
                //그룹방 세팅(정렬)
                break;
            case android.R.id.home:
                //뒤로가기(나가기)
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public void init(){

        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_HOME_AS_UP|actionBar.DISPLAY_SHOW_TITLE);
        LayoutInflater inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View GridProfileHeader=inflater.inflate(R.layout.header_item,null);
        profileGrid=(GridViewWithHeaderAndFooter )findViewById(R.id.gridView);
        listGridHead=new ArrayList<>();
        listGridContent=new ArrayList<>();
        listUserInfo=new ArrayList<>();         //유저 정보모음.(유저아이디,컨텐트)

        intent= getIntent(); /*데이터 수신*/
        int key=(intent.getExtras().getInt("key"));
        String groupName=SingletonGroupList.getInstance().getGroupName(key);
        setTitle(groupName);   //호용 20190317 : 임시로 작성(그룹클릭시 그룹이름이 들어가야함.)

        profileAdapter= new GridAdapter(getApplicationContext());
        handlerGroupList=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch(msg.what){

                    case AM_GROUP_LIST_INIT:
                        //그룹헤더와 그룹컨텐트 리스트 초기화완료-->헤더생성
                        LoadGridHeader();




                        break;
                    case AM_GROUP_USER_INIT:


                        LoadGrid();
                        break;
                }

            }
        };
        LoadGridHeadAndContent();
        LoadGridUserAndUserContent();

    }
    public void LoadGridHeadAndContent(){



        //서브스레드 생성 및 서버와 통신

        Thread threadGroupList=new Thread(new Runnable() {

            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;
                    //해당 그룹 group_db에서 검색하기 위한 키값 얻기.
                    int key=(intent.getExtras().getInt("key"));
                    String strGroupID=SingletonGroupList.getInstance().getGroupID(key);
                    Response.Listener<String> responseLister2 = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if (success) {

                                    nCategoryNum=jsonResponse.getInt("groupCategory");
                                    //그룹헤드리스트 초기화
                                    String strHead1=jsonResponse.getString("groupHead1");
                                    listGridHead.add((strHead1));
                                    String strHead2=jsonResponse.getString("groupHead2");
                                    listGridHead.add((strHead2));
                                    String strHead3=jsonResponse.getString("groupHead3");
                                    listGridHead.add((strHead3));
                                    String strHead4=jsonResponse.getString("groupHead4");
                                    listGridHead.add((strHead4));
                                    String strHead5=jsonResponse.getString("groupHead5");
                                    listGridHead.add((strHead5));
                                    String strHead6=jsonResponse.getString("groupHead6");
                                    listGridHead.add((strHead6));
                                    String strHead7=jsonResponse.getString("groupHead7");
                                    listGridHead.add((strHead7));
                                    String strHead8=jsonResponse.getString("groupHead8");
                                    listGridHead.add((strHead8));
                                    String strHead9=jsonResponse.getString("groupHead9");
                                    listGridHead.add((strHead9));
                                    String strHead10=jsonResponse.getString("groupHead10");
                                    listGridHead.add((strHead10));

                                    //그룹컨텐트 초기화(이후에 콤보박스로 구현하기)
                                    String strContent1=jsonResponse.getString("groupContent1");
                                    listGridContent.add(strContent1);
                                    String strContent2=jsonResponse.getString("groupContent2");
                                    listGridContent.add(strContent2);
                                    String strContent3=jsonResponse.getString("groupContent3");
                                    listGridContent.add(strContent3);
                                    String strContent4=jsonResponse.getString("groupContent4");
                                    listGridContent.add(strContent4);
                                    String strContent5=jsonResponse.getString("groupContent5");
                                    listGridContent.add(strContent5);
                                    String strContent6=jsonResponse.getString("groupContent6");
                                    listGridContent.add(strContent6);
                                    String strContent7=jsonResponse.getString("groupContent7");
                                    listGridContent.add(strContent7);
                                    String strContent8=jsonResponse.getString("groupContent8");
                                    listGridContent.add(strContent8);
                                    String strContent9=jsonResponse.getString("groupContent9");
                                    listGridContent.add(strContent9);
                                    String strContent10=jsonResponse.getString("groupContent10");
                                    listGridContent.add(strContent10);





                                    Message msg = handlerGroupList.obtainMessage();
                                    msg.what = AM_GROUP_LIST_INIT;
                                    handlerGroupList.sendMessage(msg);

                                }
                                else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    GroupInfoRequest groupInfoRequest= new GroupInfoRequest(strGroupID, responseLister2);
                    RequestQueue queue = Volley.newRequestQueue(GroupActivity.this);
                    queue .add(groupInfoRequest);
                }



            }



        });

        threadGroupList.start();



    }
    public void LoadGrid(){
        //그리드를 동적으로 생성.
        //조건 : 그룹에 속한 유저 검색 group_member에서 유저확인
        //확인된 유저ID를 통해서 group_content를 검색해서 Content를 가져온다.
        //따라서 유저리스트,유저컨텐트리스트가 필요하다.





        /*
        profileAdapter.addItem(new SingerProfileItem("엄호용",R.drawable.ic_person_black_24dp,profileAdapter.ITEM_VIEW_PROFILE));
        profileAdapter.addItem(new SingerProfileItem("엄호용",profileAdapter.ITEM_VIEW_TEXT));
        profileAdapter.addItem(new SingerProfileItem("엄호용",profileAdapter.ITEM_VIEW_TEXT));


        profileAdapter.addItem(new SingerProfileItem("엄호용",R.drawable.ic_person_black_24dp,profileAdapter.ITEM_VIEW_PROFILE));
        profileAdapter.addItem(new SingerProfileItem("엄호용",profileAdapter.ITEM_VIEW_TEXT));
        profileAdapter.addItem(new SingerProfileItem("엄호용",profileAdapter.ITEM_VIEW_TEXT));
        profileAdapter.addItem(new SingerProfileItem("엄호용",R.drawable.ic_person_black_24dp,profileAdapter.ITEM_VIEW_PROFILE));
        profileAdapter.addItem(new SingerProfileItem("엄호용",profileAdapter.ITEM_VIEW_TEXT));
        profileAdapter.addItem(new SingerProfileItem("엄호용",profileAdapter.ITEM_VIEW_TEXT));
        profileAdapter.addItem(new SingerProfileItem("엄호용",R.drawable.ic_person_black_24dp,profileAdapter.ITEM_VIEW_PROFILE));
        profileAdapter.addItem(new SingerProfileItem("엄호용",profileAdapter.ITEM_VIEW_TEXT));
        profileAdapter.addItem(new SingerProfileItem("엄호용",profileAdapter.ITEM_VIEW_TEXT));
        profileAdapter.addItem(new SingerProfileItem("엄호용",R.drawable.ic_person_black_24dp,profileAdapter.ITEM_VIEW_PROFILE));
        profileAdapter.addItem(new SingerProfileItem("엄호용",profileAdapter.ITEM_VIEW_TEXT));
        profileAdapter.addItem(new SingerProfileItem("엄호용",profileAdapter.ITEM_VIEW_TEXT));
        profileAdapter.addItem(new SingerProfileItem("엄호용",R.drawable.ic_person_black_24dp,profileAdapter.ITEM_VIEW_PROFILE));
        profileAdapter.addItem(new SingerProfileItem("엄호용",profileAdapter.ITEM_VIEW_TEXT));
        profileAdapter.addItem(new SingerProfileItem("엄호용",profileAdapter.ITEM_VIEW_TEXT));
        profileAdapter.addItem(new SingerProfileItem("엄호용",R.drawable.ic_person_black_24dp,profileAdapter.ITEM_VIEW_PROFILE));
        profileAdapter.addItem(new SingerProfileItem("엄호용",profileAdapter.ITEM_VIEW_TEXT));
        profileAdapter.addItem(new SingerProfileItem("엄호용",profileAdapter.ITEM_VIEW_TEXT));
        */


        profileGrid.setAdapter(profileAdapter);


    }

    public void LoadGridUserAndUserContent(){
        //유저 및 유저컨텐트(프로필+유저컨텐트)
        //서브스레드 생성 및 서버와 통신

        Thread threadGroupList=new Thread(new Runnable() {

            boolean isPlaying=false;
            @Override
            public void run() {
                if(isPlaying==false) {
                    isPlaying=true;
                    //해당 그룹 group_db에서 검색하기 위한 키값 얻기.
                    int key=(intent.getExtras().getInt("key"));
                    String strGroupID=SingletonGroupList.getInstance().getGroupID(key);
                    Response.Listener<String> responseLister2 = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if (success) {
                                    //유저수
                                    int nUserCount=jsonResponse.getInt("size");
                                    //유저수 만큼 이터레이터
                                    for(int nIdx=1;nIdx<=nUserCount;nIdx++){
                                        UserInfo userInfo=new UserInfo();

                                        JSONObject obj=jsonResponse.getJSONObject(Integer.toString(nIdx));
                                        boolean successSub=obj.getBoolean("success");
                                        if(!successSub) {
                                            //에러처리
                                        }
                                        String strUserID=obj.getString("userID");
                                        String strContent1=obj.getString("content1");
                                        userInfo.AddContent(strContent1);
                                        String strContent2=obj.getString("content2");
                                        userInfo.AddContent(strContent2);
                                        String strContent3=obj.getString("content3");
                                        userInfo.AddContent(strContent3);
                                        String strContent4=obj.getString("content4");
                                        userInfo.AddContent(strContent4);
                                        String strContent5=obj.getString("content5");
                                        userInfo.AddContent(strContent5);
                                        String strContent6=obj.getString("content6");
                                        userInfo.AddContent(strContent6);
                                        String strContent7=obj.getString("content7");
                                        userInfo.AddContent(strContent7);
                                        String strContent8=obj.getString("content8");
                                        userInfo.AddContent(strContent8);
                                        String strContent9=obj.getString("content9");
                                        userInfo.AddContent(strContent9);
                                        String strContent10=obj.getString("content10");
                                        userInfo.AddContent(strContent10);

                                        userInfo.setUserID(strUserID);

                                        listUserInfo.add(userInfo);
                                    }



                                    Message msg = handlerGroupList.obtainMessage();
                                    msg.what = AM_GROUP_USER_INIT;
                                    handlerGroupList.sendMessage(msg);

                                }
                                else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    GroupInfoRequest groupInfoRequest= new GroupInfoRequest(strGroupID, responseLister2);
                    RequestQueue queue = Volley.newRequestQueue(GroupActivity.this);
                    queue .add(groupInfoRequest);
                }



            }



        });

        threadGroupList.start();



    }
    public void LoadGridHeader(){



        //카테고리수+1(프로필)=전체 헤더길이
        // /리스트초기화(헤더)
        profileAdapter.addItem(new SingerProfileItem("프로필", profileAdapter.ITEM_VIEW_TEXT));
        for(int nCount=0;nCount<nCategoryNum;nCount++) {
            String strHead=profileAdapter.getItem(nCount).toString();
            profileAdapter.addItem(new SingerProfileItem(strHead, profileAdapter.ITEM_VIEW_TEXT));
            //profileAdapter.addItem(new SingerProfileItem("내용", profileAdapter.ITEM_VIEW_TEXT));
        }

    }
}
