package com.example.hoyo1.whereis;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class NoVerticalScrollListView extends ListView {
    public NoVerticalScrollListView(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return false;
    }
    @Override      //스크롤 막음

    public void onScrollChanged(int x, int y, int x2, int y2){

        if(x < this.getWidth()){

            scrollTo(0, 0);

            return;

        }

    }
    /*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if(ev.getAction()==MotionEvent.ACTION_MOVE)
            return true;
        return super.dispatchTouchEvent(ev);
    }
    */

}
