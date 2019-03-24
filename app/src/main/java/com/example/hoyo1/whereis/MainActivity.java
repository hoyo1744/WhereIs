package com.example.hoyo1.whereis;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {

    // 호용 20190322 : main에서의 요청은 100대
    public static final int REQUEST_GROUP_ADD = 101;
    public static final int REQUEST_GROUP = 102;

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
}