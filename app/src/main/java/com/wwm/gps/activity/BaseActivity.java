package com.wwm.gps.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.wwm.gps.dialog.LoadingDialog;

public class BaseActivity extends Activity {
	public ImageView iv_back;
	public TextView tv_title;

    public LoadingDialog loadingDialog;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingDialog = new LoadingDialog(this);
    }

    public void initView () {


    }



}
