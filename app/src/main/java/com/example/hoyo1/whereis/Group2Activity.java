package com.example.hoyo1.whereis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.GridAdapter;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.GridProfileView;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.GridTextView;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.ListAdapter;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.SingerProfileItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group2Activity extends AppCompatActivity {
    public final static int REQUEST_TEXT_POPUP=100;





    class UserInfo {
        private int nCategory;
        private String strUserID;
        private ArrayList<String> strArrContent = new ArrayList<>();


        public int getCategory() {
            return this.nCategory;
        }

        public String getUserID() {
            return this.strUserID;
        }

        public String getContent(int pos) {
            return this.strArrContent.get(pos);
        }

        public void setCategory(int nParam) {
            this.nCategory = nParam;
        }

        public void setUserID(String strParam) {
            this.strUserID = strParam;
        }

        public void AddContent(String strParam) {
            this.strArrContent.add(strParam);
        }

    }

    //그룹그리드에서 특정 그리드를 선택했을때, 정보를 저장.
    class SelectedUserInfo {
        private int nUserNumber;
        private String strUserID;
        private String strSelectedHead;
        private String strSelectedContent;
        private int nRow;
        private int nCol;


        SelectedUserInfo(int userNum, String userID, String selectedHead, String selectedContent) {
            nUserNumber = userNum;
            strUserID = userID;
            strSelectedHead = selectedHead;
            strSelectedContent = selectedContent;
        }

        SelectedUserInfo(String userID, String selectedHead, String strSelectedContent) {
            strUserID = userID;
            strSelectedHead = selectedHead;
            strSelectedContent = strSelectedContent;
        }

        public void setRow(int row) {
            nRow = row;
        }

        public void setCol(int col) {
            nCol = col;
        }

        public void setUserNumber(int userNum) {
            nUserNumber = userNum;
        }

        public void setUserID(String userID) {
            strUserID = userID;
        }

        public void setSelectedHead(String selectedHead) {
            strSelectedHead = selectedHead;
        }

        public void setSelectedContent(String selectedContent) {
            strSelectedContent = selectedContent;
        }

        public int getRow() {
            return this.nRow;
        }

        public int getCol() {
            return this.nCol;
        }

        public int getUserNumber() {
            return this.nUserNumber;
        }

        public String getUserID() {
            return this.strSelectedHead;
        }

        public String getSelectedHead() {
            return this.strSelectedHead;
        }

        public String getStrSelectedContent() {
            return this.strSelectedContent;
        }

        public void Init() {
            nUserNumber = 0;
            strUserID = "";
            strSelectedHead = "";
            strSelectedContent = "";
        }
    }

    //그룹에서 요청은 200대
    public static final int REQUEST_MEMBER_ADD = 201;

    //메시지모음(그룹: 4만대)
    public static final int AM_GROUP_LIST_INIT = 40000;       //헤더 및 컨텐트 리스트 초기화
    public static final int AM_GROUP_USER_INIT = 40001;       //그룹에 속한 유저 및 유저컨텐트 초기화
    public static final int AM_GROUP_USER_LIST = 40002;       //그룹에 속한 유저 및 유저컨텐트 초기화
    ArrayList<String> listGridHead;
    ArrayList<String> listGridContent;
    ArrayList<Group2Activity.UserInfo> listUserInfo;
    ArrayList<String> listHeadSize;
    ArrayList<String> listContentSize;


    ListAdapter adapterSelectedView;

    ListAdapter listAdapter;
    LinearLayout linearLayout;
    ArrayList<ListAdapter> arrayListAdapterView;
    public static HashMap<GridTextView,ListAdapter> mapSelectedTextView;
    public static HashMap<GridProfileView,ListAdapter> mapSelectedProfileView;




    TextView textViewGroupLeaderName;
    Intent intent;
    Handler handlerGroupList;
    int nCategoryNum;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group2);
        Init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId) {
            case R.id.searchGroupMenu:
                //그룹멤버검색
                break;
            case R.id.memberAddGroupMenu:
                //그룹멤버초대
                Intent intent = new Intent(getApplicationContext(), GroupMemberAddActivity.class);
                startActivityForResult(intent, REQUEST_MEMBER_ADD);
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

    public void Init() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_HOME_AS_UP | actionBar.DISPLAY_SHOW_TITLE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        textViewGroupLeaderName = (TextView) findViewById(R.id.groupLeaderNameTextView);


        intent = getIntent(); /*데이터 수신*/
        int key = (intent.getExtras().getInt("key"));
        String groupName = SingletonGroupList.getInstance().getGroupName(key);
        String groupLeaderName = SingletonGroupList.getInstance().getGroupLeader(key);
        String groupCategory = SingletonGroupList.getInstance().getGroupCategory(key);
        setTitle(groupName);                                            //호용 20190317 : 임시로 작성(그룹클릭시 그룹이름이 들어가야함.)
        nCategoryNum = Integer.parseInt(groupCategory) + 1;
        textViewGroupLeaderName.setText(groupLeaderName);

        mapSelectedTextView=new HashMap<>();
        mapSelectedProfileView=new HashMap<>();
        listGridHead = new ArrayList<>();
        listGridContent = new ArrayList<>();
        listUserInfo = new ArrayList<>();         //유저 정보모음.(유저아이디,컨텐트)
        listHeadSize = new ArrayList<>();         //헤더최대크기모음
        listContentSize = new ArrayList<>();         //컨텐트최대크기모음

        //카테고리수만큼 리스트 생성
        arrayListAdapterView= new ArrayList<ListAdapter>();



        handlerGroupList = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case AM_GROUP_LIST_INIT:
                        //그룹헤더와 그룹컨텐트 리스트 초기화완료-->회원이름가져오기
                        LoadListUserAndUserContent();

                        break;
                    case AM_GROUP_USER_INIT:
                        LoadList();
                        break;
                }

            }
        };
        LoadListHeadAndContent();


    }

    public void LoadListHeadAndContent() {

        //서브스레드 생성 및 서버와 통신

        Thread threadHead = new Thread(new Runnable() {
            boolean isPlaying = false;

            @Override
            public void run() {
                if (isPlaying == false) {
                    isPlaying = true;
                    //해당 그룹 group_db에서 검색하기 위한 키값 얻기.
                    int key = (intent.getExtras().getInt("key"));
                    String strGroupID = SingletonGroupList.getInstance().getGroupID(key);
                    Response.Listener<String> responseLister2 = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if (success) {

                                    //nCategoryNum=jsonResponse.getInt("groupCategory");
                                    //그룹헤드리스트 초기화
                                    String strHead1 = jsonResponse.getString("groupHead1");
                                    listGridHead.add((strHead1));
                                    String strHead2 = jsonResponse.getString("groupHead2");
                                    listGridHead.add((strHead2));
                                    String strHead3 = jsonResponse.getString("groupHead3");
                                    listGridHead.add((strHead3));
                                    String strHead4 = jsonResponse.getString("groupHead4");
                                    listGridHead.add((strHead4));
                                    String strHead5 = jsonResponse.getString("groupHead5");
                                    listGridHead.add((strHead5));
                                    String strHead6 = jsonResponse.getString("groupHead6");
                                    listGridHead.add((strHead6));
                                    String strHead7 = jsonResponse.getString("groupHead7");
                                    listGridHead.add((strHead7));
                                    String strHead8 = jsonResponse.getString("groupHead8");
                                    listGridHead.add((strHead8));
                                    String strHead9 = jsonResponse.getString("groupHead9");
                                    listGridHead.add((strHead9));
                                    String strHead10 = jsonResponse.getString("groupHead10");
                                    listGridHead.add((strHead10));

                                    //그룹컨텐트 초기화(이후에 콤보박스로 구현하기)
                                    String strContent1 = jsonResponse.getString("groupContent1");
                                    listGridContent.add(strContent1);
                                    String strContent2 = jsonResponse.getString("groupContent2");
                                    listGridContent.add(strContent2);
                                    String strContent3 = jsonResponse.getString("groupContent3");
                                    listGridContent.add(strContent3);
                                    String strContent4 = jsonResponse.getString("groupContent4");
                                    listGridContent.add(strContent4);
                                    String strContent5 = jsonResponse.getString("groupContent5");
                                    listGridContent.add(strContent5);
                                    String strContent6 = jsonResponse.getString("groupContent6");
                                    listGridContent.add(strContent6);
                                    String strContent7 = jsonResponse.getString("groupContent7");
                                    listGridContent.add(strContent7);
                                    String strContent8 = jsonResponse.getString("groupContent8");
                                    listGridContent.add(strContent8);
                                    String strContent9 = jsonResponse.getString("groupContent9");
                                    listGridContent.add(strContent9);
                                    String strContent10 = jsonResponse.getString("groupContent10");
                                    listGridContent.add(strContent10);

                                    //여기에서 컨텐트 파싱시작.
                                    GetMaxContent();
                                    //파싱완료


                                    Message msg = handlerGroupList.obtainMessage();
                                    msg.what = AM_GROUP_LIST_INIT;
                                    handlerGroupList.sendMessage(msg);

                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    GroupInfoRequest groupInfoRequest = new GroupInfoRequest(strGroupID, responseLister2);
                    RequestQueue queue = Volley.newRequestQueue(Group2Activity.this);
                    queue.add(groupInfoRequest);
                }
            }
        });
        threadHead.start();
    }

    public void GetMaxContent() {
        int nlistCount = listGridContent.size();
        if (nlistCount != 0) {
            for (int nCount = 0; nCount < nlistCount; nCount++) {
                //1개씩 가져와서 파싱후, 가장 긴것 고르기
                String strContent = listGridContent.get(nCount);
                if (strContent == "null")
                    break;
                //#기준으로파싱하기
                String[] words = strContent.split("#");
                int nLen = -1;
                String strWord = "";
                for (String wd : words) {
                    int wdLen = wd.length();
                    if (wdLen > nLen) {
                        nLen = wdLen;
                        strWord = wd;
                    }
                }
                listContentSize.add(strWord);
            }
        }
    }

    public void LoadListUserAndUserContent() {

        //유저 및 유저컨텐트(프로필+유저컨텐트)
        //서브스레드 생성 및 서버와 통신

        Thread threadUserAndUserContent = new Thread(new Runnable() {

            boolean isPlaying = false;

            @Override
            public void run() {
                if (isPlaying == false) {
                    isPlaying = true;
                    //해당 그룹 group_db에서 검색하기 위한 키값 얻기.
                    int key = (intent.getExtras().getInt("key"));
                    String strGroupID = SingletonGroupList.getInstance().getGroupID(key);
                    Response.Listener<String> responseLister2 = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if (success) {
                                    //유저수
                                    int nUserCount = jsonResponse.getInt("size");
                                    //유저수 만큼 이터레이터
                                    for (int nIdx = 1; nIdx <= nUserCount; nIdx++) {
                                        Group2Activity.UserInfo userInfo = new Group2Activity.UserInfo();

                                        JSONObject obj = jsonResponse.getJSONObject(Integer.toString(nIdx));
                                        boolean successSub = obj.getBoolean("success");
                                        if (!successSub) {
                                            //에러처리
                                        }
                                        userInfo.setCategory(nCategoryNum);
                                        String strUserID = obj.getString("userName");
                                        String strContent1 = obj.getString("content1");
                                        userInfo.AddContent(strContent1);
                                        String strContent2 = obj.getString("content2");
                                        userInfo.AddContent(strContent2);
                                        String strContent3 = obj.getString("content3");
                                        userInfo.AddContent(strContent3);
                                        String strContent4 = obj.getString("content4");
                                        userInfo.AddContent(strContent4);
                                        String strContent5 = obj.getString("content5");
                                        userInfo.AddContent(strContent5);
                                        String strContent6 = obj.getString("content6");
                                        userInfo.AddContent(strContent6);
                                        String strContent7 = obj.getString("content7");
                                        userInfo.AddContent(strContent7);
                                        String strContent8 = obj.getString("content8");
                                        userInfo.AddContent(strContent8);
                                        String strContent9 = obj.getString("content9");
                                        userInfo.AddContent(strContent9);
                                        String strContent10 = obj.getString("content10");
                                        userInfo.AddContent(strContent10);
                                        userInfo.setUserID(strUserID);
                                        listUserInfo.add(userInfo);
                                    }
                                    //유저이름파악
                                    GetMaxProfile();

                                    //유저이름파악완료
                                    Message msg = handlerGroupList.obtainMessage();
                                    msg.what = AM_GROUP_USER_INIT;
                                    handlerGroupList.sendMessage(msg);
                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    GroupUserInfoRequest groupInfoRequest = new GroupUserInfoRequest(strGroupID, responseLister2);
                    RequestQueue queue = Volley.newRequestQueue(Group2Activity.this);
                    queue.add(groupInfoRequest);
                }
            }
        });
        threadUserAndUserContent.start();
    }
    public void GetMaxProfile() {
        int nlistCount = listUserInfo.size();
        if (nlistCount != 0) {
            String strSelectedUserName = listUserInfo.get(0).getUserID();//가장긴유저이름
            int nNameLength = strSelectedUserName.length();//가장긴유저이름의 크기
            for (int nCount = 1; nCount < nlistCount; nCount++) {
                String strName = listUserInfo.get(nCount).getUserID();
                int nLength = strName.length();
                if (nNameLength < nLength) {
                    strSelectedUserName = strName;
                    nNameLength = strName.length();
                }
            }
            //프로필이름중에 가장 긴 이름 완료.
            listHeadSize.add(strSelectedUserName);
        }
    }

    public void LoadList() {
        //listHeadSize와 listContentSize에 가장 긴 이름들이 들어있다.
        //어댑터생성

        int nCategoryCnt = 0;
        for (int nCount = 0; nCount < nCategoryNum; nCount++) {
            int nListHeight = 0;
            listAdapter = new ListAdapter(getApplicationContext(),nCount);      //컨텍스트,카테고리번호(내가 몇번째 어댑터인지)

            int width = 0;
            int height = 0;
            if (nCount == 0) {
                width = (int) textViewGroupLeaderName.getPaint().measureText(listHeadSize.get(0).toString());
                width += 100;
            } else {
                width = (int) textViewGroupLeaderName.getPaint().measureText(listContentSize.get(0).toString());
            }
            height = (int) textViewGroupLeaderName.getHeight();

            nListHeight += height;
            //헤더넣기
            if (nCount == 0) {
                listAdapter.addItem(new SingerProfileItem("프로필", listAdapter.ITEM_VIEW_TEXT, width, height));
            }
            else {
                String strHead = listGridHead.get(nCategoryCnt).toString();
                listAdapter.addItem(new SingerProfileItem(strHead, listAdapter.ITEM_VIEW_TEXT, width, height));
            }


            //유저수 만큼 이터레이터
            //for(int nUserCount=0;nUserCount<listUserInfo.size();nUserCount++){
            for (int nUserCount = 0; nUserCount < listUserInfo.size(); nUserCount++) {
                if (nCount == 0) {
                    //프로필
                    String strUserName = listUserInfo.get(0).getUserID();
                    String strHeadAndContent = listHeadSize.get(0).toString();
                            listAdapter.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, width, height));
                } else {
                    String strUserContent = listUserInfo.get(0).getContent(nCategoryCnt);
                    listAdapter.addItem(new SingerProfileItem(strUserContent, listAdapter.ITEM_VIEW_TEXT, width, height));
                }
                nListHeight += height;
            }
            NoVerticalScrollListView listChild = new NoVerticalScrollListView(getApplicationContext());

            listChild.setVerticalScrollBarEnabled(false);
            listChild.setAdapter(listAdapter);

            linearLayout.addView(listChild);
            listChild.getLayoutParams().width = width;
            listChild.getLayoutParams().height = nListHeight;
            adapterSelectedView=(ListAdapter) listChild.getAdapter();
            registerForContextMenu(listChild);



            if (nCount != 0)
                nCategoryCnt++;
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        //여기서 v는 리스트를 의미한다.
        //selectedUserInfo변수에 정보를 저장한다.
        //저장해야할정보 : 회원아이디,
        //현재 선택된 리스트의 어댑터저장하기.




        //메뉴팝업
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_context_person, menu);




        //super.onCreateContextMenu(menu,v,menuInfo);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //return super.onContextItemSelected(item);


        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        View view=info.targetView; //여기서 view는 gridProfileView또는 TextView이다.
        GridProfileView profileView;
        GridTextView textView;
        ListAdapter selectedAdapt=new ListAdapter(getApplicationContext());
        int id=view.getId();
        if(view.getId()==R.id.gridTextViewLayout){
            textView=(GridTextView)view;
            selectedAdapt=mapSelectedTextView.get(textView);
        }
        else if(view.getId()==R.id.gridProfileViewLayout){
            profileView=(GridProfileView)view;
            selectedAdapt=mapSelectedTextView.get(profileView);
        }


        int index=info.position;
        switch (item.getItemId()) {
            case R.id.itemChange:
                //선택된 뷰의 내용을 바꿈
                return true;
            case R.id.itemContent:
                //선택된 뷰의 내용을 보여줌
                //팝업으로 보여줌

                SingerProfileItem selectedItem = (SingerProfileItem)selectedAdapt.getItem(index);
                if (selectedItem.getType() == ListAdapter.ITEM_VIEW_TEXT) {
                    Toast.makeText(this,selectedItem.getContent(),Toast.LENGTH_LONG).show();
                } else if (selectedItem.getType() == ListAdapter.ITEM_VIEW_PROFILE) {
                    Intent intent=new Intent(getApplicationContext(),dataPopUpActivity.class);
                    startActivityForResult(intent,REQUEST_TEXT_POPUP);
                }
                return true;
        }
        return false;
    }
}
