package com.example.hoyo1.whereis.Common;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.hoyo1.whereis.R;

public class CustomLoadingDialog extends ProgressDialog {

    private Context context;
    private ImageView imageView;

    public CustomLoadingDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);

        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_loading);

        imageView = (ImageView) findViewById(R.id.imageViewLoading);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.loading);
        imageView.setAnimation(anim);
    }




    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
    }
}
