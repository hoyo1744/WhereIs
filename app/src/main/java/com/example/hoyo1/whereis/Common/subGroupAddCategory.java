package com.example.hoyo1.whereis.Common;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.hoyo1.whereis.R;

public class subGroupAddCategory extends LinearLayout {


    private EditText content;
    private EditText head;
    public subGroupAddCategory(Context context) {
        super(context);

        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sub_group_category,this,true);

        head=(EditText)findViewById(R.id.subGroupCategoryHeaderEditText);
        content=(EditText)findViewById(R.id.subGroupCategoryEditText);
    }

    public void setContent(String content){
        this.content.setText(content);
    }
    public void setHead(String head){
        this.head.setText(head);
    }
    public String getContent(){
        return this.content.getText().toString();
    }
    public String getHead(){
        return this.head.getText().toString();
    }


}

