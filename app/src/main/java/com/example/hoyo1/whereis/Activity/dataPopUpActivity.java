package com.example.hoyo1.whereis.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.hoyo1.whereis.R;

import org.w3c.dom.Text;

public class dataPopUpActivity extends Activity {

    private TextView textView;
    private Button okButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_data_pop_up);

        //초기화
        Init();
    }
    public void Init(){
        //객체참조
        textView=(TextView)findViewById(R.id.txtText);
        okButton=(Button)findViewById(R.id.okButton);

        //리스너달기
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickOkButton();
            }
        });

        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        textView.setText(data);
    }

    public void onClickOkButton(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }



}
