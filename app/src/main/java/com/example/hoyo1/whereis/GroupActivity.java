package com.example.hoyo1.whereis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.GridAdapter;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.GridTextAdapter;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.SingerProfileItem;
import com.example.hoyo1.whereis.GroupActiviy.GridMultiItemView.SingerTextItem;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class GroupActivity extends AppCompatActivity {


    GridViewWithHeaderAndFooter profileGrid;
    GridViewWithHeaderAndFooter textGrid;
    GridAdapter profileAdapter;
    GridTextAdapter textAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("그룹이름");   //호용 20190317 : 임시로 작성(그룹클릭시 그룹이름이 들어가야함.)
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
        textGrid=(GridViewWithHeaderAndFooter)findViewById(R.id.gridView2);
        profileAdapter= new GridAdapter(getApplicationContext());
        textAdapter=new GridTextAdapter(getApplicationContext());



        profileGrid.addHeaderView(GridProfileHeader);
        //리스트초기화
        profileAdapter.addItem(new SingerProfileItem("엄호용",R.drawable.ic_person_black_24dp ));
        profileAdapter.addItem(new SingerProfileItem("엄호용",R.drawable.ic_person_black_24dp ));
        profileAdapter.addItem(new SingerProfileItem("엄호용",R.drawable.ic_person_black_24dp ));
        profileAdapter.addItem(new SingerProfileItem("엄호용",R.drawable.ic_person_black_24dp ));
        profileAdapter.addItem(new SingerProfileItem("엄호용",R.drawable.ic_person_black_24dp ));
        profileGrid.setAdapter(profileAdapter);

        textGrid.addHeaderView(GridProfileHeader);
        textAdapter.addItem(new SingerTextItem("내용"));
        textAdapter.addItem(new SingerTextItem("내용"));
        textAdapter.addItem(new SingerTextItem("내용"));
        textAdapter.addItem(new SingerTextItem("내용"));
        textAdapter.addItem(new SingerTextItem("내용"));
        textGrid.setAdapter(textAdapter);





    }
}
