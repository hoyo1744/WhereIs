package com.example.hoyo1.whereis.Common;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

public class CustomLoadingDialog extends ProgressDialog {

    private Context context;
    public CustomLoadingDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        this.context=context;
        this.setMessage("로딩중입니다..");
        this.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
