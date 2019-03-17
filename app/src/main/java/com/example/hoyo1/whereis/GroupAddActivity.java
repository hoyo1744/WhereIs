package com.example.hoyo1.whereis;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.security.acl.Group;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupAddActivity extends AppCompatActivity {

    //호용 20190317 : 그룹카테고리 추가전,후,나누기(아직 작업x)
    private enum GroupAddState{

    };

    private boolean bIsCreatedCategoryView; //커스텀뷰가 생성되었는지 확인
    private AlertDialog dialog;
    EditText groupNameEditText;
    EditText groupLeaderNameEditText;
    EditText groupCategoryNumberEditText;
    LinearLayout subGroupAdd;               //커스텀뷰를 추가할 하위 레이아웃





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
                            bIsCreatedCategoryView = false;
                        }
                        return ;


                    }

                    int nCategoryNum=Integer.parseInt(strEditText);
                    bIsCreatedCategoryView=true;
                    for(int cnt=0;cnt<nCategoryNum;cnt++){
                        subGroupAddCategory subGroupAddCategoryView=new subGroupAddCategory(getApplicationContext());
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
                //그룹이름,그룹리더이름,그룹헤더 및 카테고리값을 저장
                Toast.makeText(this,groupLeaderNameEditText.getText().toString(),Toast.LENGTH_LONG).show();



                setResult(RESULT_OK);
                finish();
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
}



