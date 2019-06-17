package com.example.hoyo1.whereis.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hoyo1.whereis.Common.CustomLoadingDialog;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.GridProfileView;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.GridTextView;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.ListAdapter;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.SingerProfileItem;
import com.example.hoyo1.whereis.Common.NoVerticalScrollListView;
import com.example.hoyo1.whereis.R;
import com.example.hoyo1.whereis.Request.GroupInfoRequest;
import com.example.hoyo1.whereis.Request.GroupUserInfoRequest;
import com.example.hoyo1.whereis.Request.OutOfGroupLeaderRequest;
import com.example.hoyo1.whereis.Request.OutOfGroupRequest;
import com.example.hoyo1.whereis.Singleton.SingletonGroupList;
import com.example.hoyo1.whereis.Singleton.SingletonSocket;
import com.example.hoyo1.whereis.Singleton.SingletonUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Group2Activity extends AppCompatActivity {



    //유저인포 자료형
    class UserInfo implements Comparable<UserInfo> {
        private int nCategory;
        private String strUserID;
        private String strUserNo;
        private String strUserName;
        private int nUserPriv;
        private ArrayList<String> strArrContent = new ArrayList<>();


        public String getUserNo(){return this.strUserNo;}
        public int getCategory() {
            return this.nCategory;
        }
        public int getUserPriv(){return this.nUserPriv;
        }


        public String getUserID() {
            return this.strUserID;
        }
        public String getContent(int pos) {
            return this.strArrContent.get(pos);
        }
        public String getUserName(){return this.strUserName;}

        public void setUserPriv(int nPriv){
            this.nUserPriv=nPriv;
        }
        public void setCategory(int nParam) {
            this.nCategory = nParam;
        }
        public void setUserNo(String strParam){this.strUserNo=strParam;}
        public void setUserID(String strParam) {
            this.strUserID = strParam;
        }
        public void setUserName(String strParam){this.strUserName=strParam;}
        public void AddContent(String strParam) {
            this.strArrContent.add(strParam);
        }
        @Override
        public int compareTo(UserInfo other){
            if(nUserPriv<other.nUserPriv)
                return -1;
            else if(nUserPriv==other.nUserPriv)
                return 0;
            else
                return 1;
        }
    }

    //요청메시지
    public final static int REQUEST_TEXT_POPUP=300;
    public final static int REQUEST_NAME_POPUP=301;
    public final static int REQUEST_CHANGE_CONTENT=302;
    public static final int REQUEST_MEMBER_ADD = 303;

    //핸들러메시지
    public static final int AM_GROUP_LIST_INIT = 30000;
    public static final int AM_GROUP_LIST_ERROR = 30003;
    public static final int AM_GROUP_USER_INIT = 30001;
    public static final int AM_OUT_OF_GROUP    = 30002;

    //컨텍스트
    public static Context groupContext;



    public static HashMap<GridProfileView,ListAdapter> mapSelectedProfileView;
    public static HashMap<GridTextView,ListAdapter> mapSelectedTextView;
    public static ArrayList<String> listGridContent;
    public static ArrayList<String> listGridHead;



    private ArrayList<Group2Activity.UserInfo> listUserInfo;
    private CustomLoadingDialog customLoadingDialog;
    private ArrayList<ListAdapter> listUser;
    private ArrayList<String> listContentSize;
    private ArrayList<String> listHeadSize;
    private ArrayList<String> listProfileSize;
    private LinearLayout linearLayout;
    private LinearLayout linearLayoutUser;
    private ListAdapter listAdapterUser;
    private ListAdapter listAdapter;



    private TextView textViewGroupLeaderName;
    private Handler handlerGroupList;
    private String groupLeaderNo;
    private int nCategoryNum;
    public String groupID;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group2);

        SingletonSocket.getInstance().setActivity(this);

        //초기화
        Init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        if(groupLeaderNo.equals(SingletonUser.getInstance().getUserNumber()))
            getMenuInflater().inflate(R.menu.menu_group_sub_for_leader, menu);
        else
            getMenuInflater().inflate(R.menu.menu_group_sub, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_TEXT_POPUP){
            if(resultCode==RESULT_OK){

            }
        }
        else if(requestCode==REQUEST_NAME_POPUP){
            if(resultCode==RESULT_OK){

            }
        }
        else if(requestCode==REQUEST_CHANGE_CONTENT){
            if(resultCode==RESULT_OK){
                //MessageEvent(데이터변경메시지)
                SingletonSocket.getInstance().sendDataChangeMessage(groupID);


                //유저리로드(위에서 메시지로 처리)
                LoadListUserAndUserContent();
            }
            else {


            }
        }
        else if(requestCode==REQUEST_MEMBER_ADD){
            if(resultCode==RESULT_OK){

                //유저리로드
                LoadListUserAndUserContent();
            }
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId) {
            case R.id.memberAddGroupMenu:
                //그룹멤버초대
                Intent intent = new Intent(getApplicationContext(), GroupMemberAddActivity.class);
                intent.putExtra("groupID",groupID);
                intent.putExtra("groupCategory",nCategoryNum);
                startActivityForResult(intent, REQUEST_MEMBER_ADD);
                break;
            case R.id.itemOutofGroup:
                if(!groupLeaderNo.equals(SingletonUser.getInstance().getUserNumber())) {
                    //그룹리더!=사용자
                    AlertDialog.Builder builder = new AlertDialog.Builder(Group2Activity.this);
                    builder.setTitle("안내");
                    builder.setMessage("그룹을 탈퇴하시겠습니까?");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            //그룹탈퇴스레드시작
                            ExecutePersonOutOfGroup();

                        }
                    }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    //그룹리더==사용자
                    AlertDialog.Builder builder = new AlertDialog.Builder(Group2Activity.this);
                    builder.setTitle("안내");
                    builder.setMessage("그룹리더입니다. 그룹을 삭제하시겠습니까?");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            //그룹탈퇴스레드시작
                            ExecuteLeaderOutOfGroup();
                        }
                    }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;
            case android.R.id.home:
                //소켓그룹나가기
                SingletonSocket.getInstance().sendRoomMessage("leave",groupID);
                setResult(RESULT_OK);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void Init() {



        //컨텍스트설정
        groupContext=this;

        //액션바 메뉴설정
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_HOME_AS_UP | actionBar.DISPLAY_SHOW_TITLE);

        //객체참조
        textViewGroupLeaderName = (TextView) findViewById(R.id.groupLeaderNameTextView);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        linearLayoutUser= (LinearLayout) findViewById(R.id.linearLayoutUser);



        mapSelectedTextView=new HashMap<>();
        mapSelectedProfileView=new HashMap<>();
        listGridHead = new ArrayList<>();           //그룹리스트헤더모음
        listGridContent = new ArrayList<>();        //그룹리스트컨텐트모음
        listUserInfo = new ArrayList<>();           //유저 정보모음.(유저아이디,컨텐트)
        listUser=new ArrayList<>();                 //본인데이터 모음.
        listProfileSize = new ArrayList<>();        //프로필최대크기모음
        listHeadSize=new ArrayList<>();             //헤더크기모음
        listContentSize = new ArrayList<>();        //컨텐트최대크기모음
        //listNoVerticalView=new ArrayList<>();

        //인텐트정보를 통한 액티비티세팅
        GetInfomationAndSettingGroup();


        //핸들러
        handlerGroupList = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case AM_GROUP_LIST_ERROR:
                        //삭제되거나 존재하지 않는 그룹을 선택한 경우.
                        Intent intentInText=new Intent(getApplicationContext(),dataPopUpActivity.class);
                        //인텐트를 통해서 객체넣기
                        intentInText.putExtra("data","삭제되었거나 존재하지 않는 그룹입니다.");
                        startActivityForResult(intentInText,REQUEST_TEXT_POPUP);
                        setResult(RESULT_OK);
                        finish();
                        break;
                    case AM_GROUP_LIST_INIT:
                        //회원정보가져오기
                        LoadListUserAndUserContent();
                        break;
                    case AM_GROUP_USER_INIT:
                        LoadList();
                        //LoadUser();
                        customLoadingDialog.dismiss();
                        break;
                    case AM_OUT_OF_GROUP:
                        //소켓그룹나가기
                        //리더일때는 delete

                        if(groupLeaderNo.equals(SingletonUser.getInstance().getUserNumber())) {
                            SingletonSocket.getInstance().sendGroupDeleteMessage(groupID);
                            SingletonSocket.getInstance().sendRoomMessage("delete", groupID);
                        }else
                            //회원일때는 leave
                            SingletonSocket.getInstance().sendRoomMessage("leave",groupID);

                        customLoadingDialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                        break;
                }
            }
        };



        //그룹헤더와 컨텐트가져오기
        LoadListHeadAndContent();
    }

    public void LoadListHeadAndContent() {

        customLoadingDialog=new CustomLoadingDialog(Group2Activity.this);
        customLoadingDialog.show();

        Thread thread= new Thread(new Runnable() {
            boolean isPlaying = false;

            @Override
            public void run() {
                if (isPlaying == false) {
                    isPlaying = true;

                    //해당 그룹 group_db에서 검색하기 위한 키값 얻기.
                    int key = (intent.getExtras().getInt("key"));
                    String strGroupID = SingletonGroupList.getInstance().getGroupID(key);


                    GroupInfoRequest groupInfoRequest = new GroupInfoRequest(strGroupID, responseGroupInfoListener);
                    RequestQueue queue = Volley.newRequestQueue(Group2Activity.this);
                    queue.add(groupInfoRequest);
                }
            }
        });
        thread.start();
    }

    public void GetMaxHead(){
        listHeadSize.clear();
        int nListCount=listGridHead.size();
        if(nListCount!=0){
            for(int nCount=0;nCount<nListCount;nCount++){
                String strHead=listGridHead.get(nCount);
                if (strHead=="null" || strHead.equals("null") || strHead ==null || strHead.equals(null))
                    break;
                listHeadSize.add(listGridHead.get(nCount));
            }
        }

    }

    public void GetMaxContent() {
        listContentSize.clear();
        int nlistCount = listGridContent.size();
        if (nlistCount != 0) {
            for (int nCount = 0; nCount < nlistCount; nCount++) {
                //1개씩 가져와서 파싱후, 가장 긴것 고르기
                String strContent = listGridContent.get(nCount);
                if (strContent=="null" || strContent.equals("null") || strContent==null || strContent.equals(null))
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

        listUserInfo.clear();
        Thread thread = new Thread(new Runnable() {
            boolean isPlaying = false;
            @Override
            public void run() {
                if (isPlaying == false) {
                    isPlaying = true;
                    //해당 그룹 group_db에서 검색하기 위한 키값 얻기.
                    int key = (intent.getExtras().getInt("key"));
                    String strGroupID = SingletonGroupList.getInstance().getGroupID(key);

                    GroupUserInfoRequest groupInfoRequest = new GroupUserInfoRequest(strGroupID, responseGroupUserInfoListener);
                    RequestQueue queue = Volley.newRequestQueue(Group2Activity.this);
                    queue.add(groupInfoRequest);
                }
            }
        });
        thread.start();
    }
    public void GetMaxProfile() {
        listProfileSize.clear();
        int nlistCount = listUserInfo.size();
        if (nlistCount != 0) {
            String strSelectedUserName = listUserInfo.get(0).getUserName();//가장긴유저이름
            int nNameLength = strSelectedUserName.length();//가장긴유저이름의 크기
            for (int nCount = 1; nCount < nlistCount; nCount++) {
                String strName = listUserInfo.get(nCount).getUserName();
                int nLength = strName.length();
                if (nNameLength < nLength) {
                    strSelectedUserName = strName;
                    nNameLength = strName.length();
                }
            }
            //프로필이름중에 가장 긴 이름 완료.
            listProfileSize.add(strSelectedUserName);
        }
    }

    public void LoadList() {
        //리스트 클리어

        linearLayout.removeAllViews();
        linearLayoutUser.removeAllViews();
        listUser.clear();
        //어댑터를 이용한 내용초기화
        ExecuteListClear();


        //call-by-reference를 위함.
        int[] ArrTotalWidth={0};
        int[] ArrPieceWidth={0};

        //미리 width계산하기
        CaculateWidth(ArrTotalWidth,ArrPieceWidth);

        int TotalWidth=ArrTotalWidth[0];
        int PieceWidth=ArrPieceWidth[0];
        boolean bIsSmallthanTotalWidth=false;

        //리스트의 넓이가 전체뷰의 넓이 보다 작다면. 맞춰줘야한다.
        if(TotalWidth<MainActivity.viewWidth){
            PieceWidth=(MainActivity.viewWidth/nCategoryNum);
            bIsSmallthanTotalWidth=true;
        }

        //리더가 맨위에 오도록 정렬하기.
        Collections.sort(listUserInfo);

        //헤더와 리스트그리기
        int nCategoryCnt = 0;
        for (int nCount = 0; nCount < nCategoryNum; nCount++) {
            int nListHeight = 0;


            listAdapter = new ListAdapter(getApplicationContext(),nCount);      //컨텍스트,카테고리번호(내가 몇번째 어댑터인지)
            listAdapterUser=new ListAdapter(getApplicationContext(),nCount);

            int width = 0;
            int height = 0;
            if (nCount == 0) {
                width = (int) textViewGroupLeaderName.getPaint().measureText(listProfileSize.get(0).toString());
                width += 100;
            } else {
                width = (int) textViewGroupLeaderName.getPaint().measureText(listContentSize.get(nCategoryCnt).toString()) > (int) textViewGroupLeaderName.getPaint().measureText(listHeadSize.get(nCategoryCnt).toString()) ? (int) textViewGroupLeaderName.getPaint().measureText(listContentSize.get(nCategoryCnt).toString())  : (int) textViewGroupLeaderName.getPaint().measureText(listHeadSize.get(nCategoryCnt).toString());
                width+=20;
            }
            height = (int) textViewGroupLeaderName.getHeight();

            nListHeight += height;
            //헤더넣기
            if (nCount == 0) {
                listAdapter.addItem(new SingerProfileItem("프로필", listAdapter.ITEM_VIEW_TEXT, width, height));
                /*
                if(!bIsSmallthanTotalWidth) {
                    listAdapter.addItem(new SingerProfileItem("프로필", listAdapter.ITEM_VIEW_TEXT, width, height));

                }

                else {
                    listAdapter.addItem(new SingerProfileItem("프로필", listAdapter.ITEM_VIEW_TEXT, PieceWidth, height));
                }
                */
            }
            else {
                String strHead = listGridHead.get(nCategoryCnt).toString();
                if(width<PieceWidth)
                    listAdapter.addItem(new SingerProfileItem(strHead, listAdapter.ITEM_VIEW_TEXT, PieceWidth, height));
                else
                    listAdapter.addItem(new SingerProfileItem(strHead, listAdapter.ITEM_VIEW_TEXT, width, height));
                /*
                if(!bIsSmallthanTotalWidth)
                    listAdapter.addItem(new SingerProfileItem(strHead, listAdapter.ITEM_VIEW_TEXT, width, height));
                else
                    listAdapter.addItem(new SingerProfileItem(strHead, listAdapter.ITEM_VIEW_TEXT, PieceWidth, height));
                */
            }

            //유저수 만큼 이터레이터
            for (int nUserCount = 0; nUserCount < listUserInfo.size(); nUserCount++) {
                if (nCount == 0) {
                    //프로필
                    String strUserName = listUserInfo.get(nUserCount).getUserName();
                    String strUserNo=listUserInfo.get(nUserCount).getUserNo();
                    if(strUserNo.equals(groupLeaderNo)) {
                        /*
                        if(!bIsSmallthanTotalWidth)
                            listAdapter.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, width, height, getResources().getColor(R.color.colorLeader)));
                        else
                            listAdapter.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, PieceWidth, height, getResources().getColor(R.color.colorLeader)));
                         */
                        listAdapter.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, width, height, getResources().getColor(R.color.colorLeader)));


                        //상단 본인 표시
                        if(strUserNo.equals(SingletonUser.getInstance().getUserNumber())){
                            /*
                            if(!bIsSmallthanTotalWidth)
                                listAdapterUser.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, width, height, getResources().getColor(R.color.colorLeader)));
                            else
                                listAdapterUser.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, PieceWidth, height, getResources().getColor(R.color.colorLeader)));
                            */
                            listAdapterUser.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, width, height, getResources().getColor(R.color.colorLeader)));
                        }

                    }
                    else if(strUserNo.equals(SingletonUser.getInstance().getUserNumber())) {
                        listAdapter.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, width, height, getResources().getColor(R.color.colorOwner)));
                        listAdapterUser.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, width, height, getResources().getColor(R.color.colorOwner)));
                        /*
                        if(!bIsSmallthanTotalWidth) {
                            listAdapter.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, width, height, getResources().getColor(R.color.colorOwner)));
                            listAdapterUser.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, width, height, getResources().getColor(R.color.colorOwner)));

                        } else {
                            listAdapter.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, PieceWidth, height, getResources().getColor(R.color.colorOwner)));
                            listAdapterUser.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, PieceWidth, height, getResources().getColor(R.color.colorOwner)));
                        }
                        */
                    }
                    else {
                        listAdapter.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, width, height, getResources().getColor(R.color.colorDefault)));
                        /*
                        if(!bIsSmallthanTotalWidth)
                            listAdapter.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, width, height, getResources().getColor(R.color.colorDefault)));
                        else
                            listAdapter.addItem(new SingerProfileItem(strUserName, R.drawable.ic_person_black_24dp, listAdapter.ITEM_VIEW_PROFILE, PieceWidth, height, getResources().getColor(R.color.colorDefault)));
                        */
                    }
                } else {
                    String strUserContent = listUserInfo.get(nUserCount).getContent(nCategoryCnt);
                    String strUserNo=listUserInfo.get(nUserCount).getUserNo();
                    if(strUserContent.equals("null"))
                        strUserContent="-";
                    /*
                    if(!bIsSmallthanTotalWidth) {
                        listAdapter.addItem(new SingerProfileItem(strUserContent, listAdapter.ITEM_VIEW_TEXT, width, height));
                        if(strUserNo.equals(SingletonUser.getInstance().getUserNumber()))
                            listAdapterUser.addItem(new SingerProfileItem(strUserContent, listAdapter.ITEM_VIEW_TEXT, width, height));
                    } else {
                        listAdapter.addItem(new SingerProfileItem(strUserContent, listAdapter.ITEM_VIEW_TEXT, PieceWidth, height));
                        if(strUserNo.equals(SingletonUser.getInstance().getUserNumber()))
                            listAdapterUser.addItem(new SingerProfileItem(strUserContent, listAdapter.ITEM_VIEW_TEXT, PieceWidth, height));
                    }
                    */
                    if(width<PieceWidth){
                        listAdapter.addItem(new SingerProfileItem(strUserContent, listAdapter.ITEM_VIEW_TEXT, PieceWidth, height));
                        if(strUserNo.equals(SingletonUser.getInstance().getUserNumber()))
                            listAdapterUser.addItem(new SingerProfileItem(strUserContent, listAdapter.ITEM_VIEW_TEXT, PieceWidth, height));
                    }else{
                        listAdapter.addItem(new SingerProfileItem(strUserContent, listAdapter.ITEM_VIEW_TEXT, width, height));
                        if(strUserNo.equals(SingletonUser.getInstance().getUserNumber()))
                            listAdapterUser.addItem(new SingerProfileItem(strUserContent, listAdapter.ITEM_VIEW_TEXT, width, height));
                    }
                }
                nListHeight += height;
            }
            NoVerticalScrollListView listChild = new NoVerticalScrollListView(getApplicationContext());
            NoVerticalScrollListView listUserChild= new NoVerticalScrollListView(getApplicationContext());

            listChild.setVerticalScrollBarEnabled(false);
            listChild.setAdapter(listAdapter);
            listUserChild.setVerticalScrollBarEnabled(false);
            listUserChild.setAdapter(listAdapterUser);


            linearLayoutUser.addView(listUserChild);
            linearLayout.addView(listChild);
            listChild.getLayoutParams().height = nListHeight+(10*listUserInfo.size());
            listUserChild.getLayoutParams().height = nListHeight+(10*listUserInfo.size());
            /*
            if(bIsSmallthanTotalWidth) {
                listChild.getLayoutParams().width = PieceWidth;
                listUserChild.getLayoutParams().width = PieceWidth;
            } else {
                listChild.getLayoutParams().width = width;
                listUserChild.getLayoutParams().width = width;
            }
            */
            if(width<PieceWidth){
                listChild.getLayoutParams().width = PieceWidth;
                listUserChild.getLayoutParams().width = PieceWidth;
            }else {
                listChild.getLayoutParams().width = width;
                listUserChild.getLayoutParams().width = width;
            }
            registerForContextMenu(listChild);
            registerForContextMenu(listUserChild);

            if (nCount != 0)
                nCategoryCnt++;
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //여기서 v는 리스트를 의미한다.
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)menuInfo;
        int pos=info.position;

        //헤더는 제외하기
        if(pos==0)
            return ;


        //자신의 아이디가 일치하지 않으면 제외하기.
        String strUserIdSource=listUserInfo.get(pos-1).getUserID().toString();
        String strUserIdTarget=SingletonUser.getInstance().getUserId().toString();
        if(!strUserIdSource.equals(strUserIdTarget))
            return ;

        //메뉴팝업
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_context_person, menu);
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
            selectedAdapt.getNumCategory();
        }
        else if(view.getId()==R.id.gridProfileViewLayout){
            profileView=(GridProfileView)view;
            selectedAdapt=mapSelectedProfileView.get(profileView);
        }


        int index=info.position;
        //헤더는 예외처리
        if(index==0)
            return false;


        SingerProfileItem selectedItem = (SingerProfileItem)selectedAdapt.getItem(index);
        switch (item.getItemId()) {
            case R.id.itemChange:
                //선택된 뷰의 내용을 바꿈
                Intent intentInChangeContent=new Intent(getApplicationContext(),ChangeContent.class);

                if (selectedItem.getType() == ListAdapter.ITEM_VIEW_TEXT) {

                    //인텐트를 통해서 객체넣기기
                    String beforeContent=selectedItem.getContent();
                    intentInChangeContent.putExtra("data",beforeContent);

                    //인텐트로 객체넘기기(그룹아이디,유저아이디,카테고리넘버(몇번째객체인지)
                    intentInChangeContent.putExtra("GroupID",groupID);
                    intentInChangeContent.putExtra("UserID",SingletonUser.getInstance().getUserNumber());
                    intentInChangeContent.putExtra("CategoryNum",selectedAdapt.getNumCategory());

                    //인텐트에 객체담기
                    startActivityForResult(intentInChangeContent,REQUEST_CHANGE_CONTENT);
                }
                else
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(Group2Activity.this);
                    builder.setTitle("안내");
                    builder.setMessage("프로필은 변경이 불가능합니다.");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setPositiveButton("예",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int whichButton){
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            case R.id.itemContent:
                //선택된 뷰의 내용을 보여줌
                //팝업으로 보여줌

                Intent intentInText=new Intent(getApplicationContext(),dataPopUpActivity.class);
                if (selectedItem.getType() == ListAdapter.ITEM_VIEW_TEXT) {

                    //인텐트를 통해서 객체넣기기
                    String content=selectedItem.getContent();
                    intentInText.putExtra("data",content);
                    startActivityForResult(intentInText,REQUEST_TEXT_POPUP);
                } else if (selectedItem.getType() == ListAdapter.ITEM_VIEW_PROFILE) {
                    String content=selectedItem.getName();
                    intentInText.putExtra("data",content);
                    startActivityForResult(intentInText,REQUEST_NAME_POPUP);
                }
                return true;
        }
        return false;
    }

    public void ExecutePersonOutOfGroup(){
            customLoadingDialog=new CustomLoadingDialog(Group2Activity.this);
            customLoadingDialog.show();
           Thread thread = new Thread(new Runnable() {
            boolean isPlaying = false;
            @Override
            public void run() {
                if (isPlaying == false) {
                    isPlaying = true;
                    OutOfGroupRequest outOfGroupRequest = new OutOfGroupRequest(groupID,SingletonUser.getInstance().getUserNumber(), responsePersonOutOfGroupListener);
                    RequestQueue queue = Volley.newRequestQueue(Group2Activity.this);
                    queue.add(outOfGroupRequest);
                }
            }
        });
        thread.start();
    }

    public void ExecuteLeaderOutOfGroup(){
        customLoadingDialog=new CustomLoadingDialog(Group2Activity.this);
        customLoadingDialog.show();
        Thread thread = new Thread(new Runnable() {
            boolean isPlaying = false;
            @Override
            public void run() {
                if (isPlaying == false) {
                    isPlaying = true;
                    OutOfGroupLeaderRequest outOfGroupLeaderRequest = new OutOfGroupLeaderRequest(groupID,SingletonUser.getInstance().getUserNumber(), responseLeaderOutOfGroupListener);
                    RequestQueue queue = Volley.newRequestQueue(Group2Activity.this);
                    queue.add(outOfGroupLeaderRequest);
                }
            }
        });
        thread.start();
    }

    public void GetInfomationAndSettingGroup(){

        intent = getIntent(); /*데이터 수신*/
        int key = (intent.getExtras().getInt("key"));
        String groupName = SingletonGroupList.getInstance().getGroupName(key);
        String groupLeaderName = SingletonGroupList.getInstance().getGroupLeaderName(key);
        String groupCategory = SingletonGroupList.getInstance().getGroupCategory(key);
        groupID=SingletonGroupList.getInstance().getGroupID(key);
        groupLeaderNo=SingletonGroupList.getInstance().getGroupLeaderNo(key);


        setTitle(groupName);
        nCategoryNum = Integer.parseInt(groupCategory) + 1;
        textViewGroupLeaderName.setText(groupLeaderName);
    }
    //리스폰리더그룹탈퇴리스너
    private Response.Listener<String> responseLeaderOutOfGroupListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success1 = jsonResponse.getBoolean("success1");
                boolean success2 = jsonResponse.getBoolean("success2");
                boolean success3 = jsonResponse.getBoolean("success3");

                if (success1 && success2 && success3) {

                    Message msg = handlerGroupList.obtainMessage();
                    msg.what = AM_OUT_OF_GROUP;
                    handlerGroupList.sendMessage(msg);
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    //리스폰일반유저탈퇴리스너
    private Response.Listener<String> responsePersonOutOfGroupListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success1 = jsonResponse.getBoolean("success1");
                boolean success2 = jsonResponse.getBoolean("success2");

                if (success1 && success2) {

                    Message msg = handlerGroupList.obtainMessage();
                    msg.what = AM_OUT_OF_GROUP;
                    handlerGroupList.sendMessage(msg);
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    //그룹에 대한 헤더와 컨텐트정보 얻기
    private Response.Listener<String> responseGroupInfoListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");

                if (success) {
                    listGridHead.clear();
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

                    //여기에서 컨텐트와 헤더파싱시작.
                    GetMaxContent();
                    GetMaxHead();

                    Message msg = handlerGroupList.obtainMessage();
                    msg.what = AM_GROUP_LIST_INIT;
                    handlerGroupList.sendMessage(msg);

                } else {
                    Message msg = handlerGroupList.obtainMessage();
                    msg.what = AM_GROUP_LIST_ERROR;
                    handlerGroupList.sendMessage(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    //그룹에 포함된 유저정보얻기.
    private Response.Listener<String> responseGroupUserInfoListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");

                if (success) {
                    //유저수
                    int nUserCount = jsonResponse.getInt("size");
                    listUserInfo.clear();
                    //유저수 만큼 이터레이터
                    for (int nIdx = 1; nIdx <= nUserCount; nIdx++) {
                        Group2Activity.UserInfo userInfo = new Group2Activity.UserInfo();

                        JSONObject obj = jsonResponse.getJSONObject(Integer.toString(nIdx));
                        boolean successSub = obj.getBoolean("success");
                        if (!successSub) {
                            //에러처리
                        }
                        userInfo.setCategory(nCategoryNum);
                        int nUserPriv=obj.getInt("userPriv");
                        userInfo.setUserPriv(nUserPriv);
                        String strUserNo=obj.getString("userNo");
                        userInfo.setUserNo(strUserNo);
                        String strUserID = obj.getString("userName");
                        String strUserName = obj.getString("userName2");
                        userInfo.setUserName(strUserName);
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

    public void ExecuteListClear(){
        if(mapSelectedTextView.size()!=0){
            for(int nIdx=0;nIdx<mapSelectedTextView.size();nIdx++){
                ListAdapter adapter=new ListAdapter(getApplicationContext());
                Set set=mapSelectedTextView.keySet();
                Iterator iter=set.iterator();
                while(iter.hasNext()){
                    GridTextView key=(GridTextView)iter.next();
                    adapter=(ListAdapter) mapSelectedTextView.get(key);
                    adapter.removeAll();
                    adapter.notifyDataSetChanged();
                }
            }
        }
        if(mapSelectedProfileView.size()!=0){
            for(int nIdx=0;nIdx<mapSelectedProfileView.size();nIdx++){
                ListAdapter adapter=new ListAdapter(getApplicationContext());
                Set set=mapSelectedProfileView.keySet();
                Iterator iter=set.iterator();
                while(iter.hasNext()){
                    GridProfileView key=(GridProfileView)iter.next();
                    adapter=(ListAdapter) mapSelectedProfileView.get(key);
                    adapter.removeAll();
                    adapter.notifyDataSetChanged();
                }
            }
        }

    }

    public void CaculateWidth(int[] total,int[] piece){

        int nContentCount=0;
        for(int nCount=0;nCount<nCategoryNum;nCount++){

            if(nCount==0){
                total[0]+=(int)textViewGroupLeaderName.getPaint().measureText(listProfileSize.get(0).toString());
                total[0]+=100;
            } else {
                if((int) textViewGroupLeaderName.getPaint().measureText(listContentSize.get(nContentCount).toString())>(int) textViewGroupLeaderName.getPaint().measureText(listHeadSize.get(nContentCount).toString()))
                    total[0] += (int) textViewGroupLeaderName.getPaint().measureText(listContentSize.get(nContentCount).toString());
                else
                    total[0]+=(int) textViewGroupLeaderName.getPaint().measureText(listHeadSize.get(nContentCount).toString());
                nContentCount++;
            }
        }

    }

    public void GetOutOfDeleteGroup(){
        if(!groupLeaderNo.equals(SingletonUser.getInstance().getUserNumber())) {
            Intent intentInText=new Intent(getApplicationContext(),dataPopUpActivity.class);
            //인텐트를 통해서 객체넣기
            intentInText.putExtra("data","그룹이 삭제되었습니다.");
            startActivityForResult(intentInText,REQUEST_TEXT_POPUP);
            setResult(RESULT_OK);
            finish();
        }
    }

}
