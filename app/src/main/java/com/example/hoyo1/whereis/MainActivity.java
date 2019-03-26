package com.example.hoyo1.whereis;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


    ImageView userImageView;
    TextView userNameTextView;
    ListView listView;
    SingerAdapter adapter;
    TextView groupSubTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("그룹");
        setContentView(R.layout.activity_main);



        init();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingerItem item=(SingerItem)adapter.getItem(position);
                Intent intent=new Intent(getApplicationContext(),GroupActivity.class);
                startActivityForResult(intent,REQUEST_GROUP);
            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
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
                startActivityForResult(intent,REQUEST_GROUP_ADD);
                break;
            case R.id.settingMenu:
                //설정메뉴
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
            }
            else if(resultCode==RESULT_CANCELED){
                //그룹추가 취소
            }
        }
        else if(requestCode==REQUEST_GROUP){
            //그룹메인

        }
    }

    public void init(){
        groupSubTitleTextView=(TextView)findViewById(R.id.groupSubTitleText);
        listView=(ListView)findViewById(R.id.listView);
        userImageView=(ImageView)findViewById(R.id.profileImage);
        userNameTextView=(TextView)findViewById(R.id.profileText);


        //개인프로필이름 표시
        userNameTextView.setText(SingletonUser.getInstance().getUserId());
        //개인프로필사진 표시


        //그룹아이디리퀘스트
        GetGroupList();






        /////////////////////////////////////////////////////////////////////////////////////
        //초기화시작
        /////////////////////////////////////////////////////////////////////////////////////

        adapter = new SingerAdapter(getApplicationContext());
        //리스트초기화
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp,"그룹리더",R.drawable.ic_person_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp,"그룹리더",R.drawable.ic_person_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp,"그룹리더",R.drawable.ic_person_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp,"그룹리더",R.drawable.ic_person_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp,"그룹리더",R.drawable.ic_person_black_24dp));
        adapter.addItem(new SingerItem("그룹이름",R.drawable.ic_group_black_24dp,"그룹리더",R.drawable.ic_person_black_24dp));
        listView.setAdapter(adapter);

        //그룹소제목초기화
        groupSubTitleTextView.setText("그룹"+"("+adapter.getCount()+")");
        /////////////////////////////////////////////////////////////////////////////////////
        //초기화끝
        /////////////////////////////////////////////////////////////////////////////////////
    }

    public void GetGroupList(){

        String userNumber=SingletonUser.getInstance().getUserNumber();
        Response.Listener<String> responseLister= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse=new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");

                    if(success)
                    {
                        SingletonGroupList.getInstance().Initialize();
                        int size=jsonResponse.getInt("size");
                        //사이즈만큼 그룹아이디를 담을 수 있는 자료구조를 생각해야함.
                        for(int nNo=1;nNo<=size;nNo++)
                        {
                            String strGroupName= Integer.toString(nNo);
                            String strGroupLeaderName=Integer.toString(nNo+MAX_GROUP_LIST);
                            String groupName=jsonResponse.getString(strGroupName);
                            String groupLeaderName=jsonResponse.getString(strGroupLeaderName);
                            SingletonGroupList.getInstance().setGroupList(nNo,groupName,groupLeaderName);
                        }
                    }
                    else
                    {

                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        //LoginInfoRequest로 새로 만들자.
        GroupIdRequest groupIDRequest=new GroupIdRequest(userNumber,responseLister);
        RequestQueue queueInfo= Volley.newRequestQueue(MainActivity.this);
        queueInfo.add(groupIDRequest);


    }
}