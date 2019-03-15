package com.example.hoyo1.whereis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicInteger;

public class GroupAddActivity extends AppCompatActivity {

    EditText groupNameEditText;
    EditText groupLeaderNameEditText;
    EditText groupCategoryNumberEditText;
    LinearLayout subGroupAdd;





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



            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력된 후에, 개수만큼 동적으로 텍스트뷰+에디트텍스트생성하기




                /*
                for(int nCount=categoryNum;nCount<nCount;nCount++)
                {
                    LayoutInflater inflater=(LayoutInflater)getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
                    inflater.inflate(R.layout.sub_group_category,subGroupAdd,true);

                }
                */




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

        groupCategoryNumberEditText=(EditText)findViewById(R.id.groupCategoryEditText);
        groupLeaderNameEditText=(EditText)findViewById(R.id.groupLeaderNameEditText);
        groupNameEditText=(EditText)findViewById(R.id.groupNameEditText);
        subGroupAdd=(LinearLayout)findViewById(R.id.subGroupAdd);
    }
}



