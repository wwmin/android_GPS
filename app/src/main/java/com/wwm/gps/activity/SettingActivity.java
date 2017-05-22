package com.wwm.gps.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wwm.gps.R;
import com.wwm.gps.bean.LoginInfo;
import com.wwm.gps.constant.Constant;
import com.wwm.gps.dialog.UpdateDialog;
import com.wwm.gps.utils.MySetting;
import com.wwm.gps.utils.SPUtil;
import com.wwm.gps.view.ArrowText;


public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_head_mine;
    private TextView tv_username_mine;
    private ArrowText at_changepassword_mine;
    private ArrowText at_time_mine;
    private ArrowText at_update_mine;
    private Button btn_unlogin_mine;

    private String oldPwd;
    private String newPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();

//        String userData = SPUtil.getData(SettingActivity.this, Constant.SP_USER_INFO, "").toString();
        String companyData = SPUtil.getData(SettingActivity.this, Constant.SP_COMPANY, "").toString();
        String userData = SPUtil.getData(SettingActivity.this, Constant.SP_USER_INFO, "").toString();
//        Gson gson = new Gson();
//        LoginInfo info = gson.fromJson(userData, LoginInfo.class);
        tv_username_mine.setText(userData);

        String versionName = "";
        try {
            PackageInfo packageInfo=getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        at_update_mine.setGreenTitle("当前版本" + versionName);
    }

    private void init () {
        tv_title = (TextView) findViewById(R.id.top_view_text);
        tv_title.setText(R.string.home_13);
        iv_back = (ImageView) findViewById(R.id.top_view_back);
        iv_back.setVisibility(View.GONE);

        iv_head_mine = (ImageView) findViewById(R.id.iv_head_mine);
        tv_username_mine = (TextView) findViewById(R.id.tv_username_mine);
        at_changepassword_mine = (ArrowText) findViewById(R.id.at_changepassword_mine);
        at_changepassword_mine.setOnClickListener(this);
        at_time_mine = (ArrowText) findViewById(R.id.at_time_mine);
        at_time_mine.setOnClickListener(this);
        at_update_mine = (ArrowText) findViewById(R.id.at_update_mine);
        at_update_mine.setOnClickListener(this);
        btn_unlogin_mine = (Button) findViewById(R.id.btn_unlogin_mine);
        btn_unlogin_mine.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.at_changepassword_mine:
                Intent intent = new Intent(SettingActivity.this, UpdatePwdActivity.class);
                startActivity(intent);
                break;
            case R.id.at_time_mine:
                Intent timeIntent = new Intent(SettingActivity.this, SetTimeActivity.class);
                startActivity(timeIntent);
                break;
            case R.id.at_update_mine:
                //检查版本更新
                UpdateDialog updateDialog = new UpdateDialog();
                updateDialog.checkUpdate(SettingActivity.this, "setting");
                break;
            case R.id.btn_unlogin_mine:
                SPUtil.saveData(SettingActivity.this, Constant.SP_COMPANY, "");
                SPUtil.saveData(SettingActivity.this, Constant.SP_USER_INFO, "");
                SPUtil.saveData(SettingActivity.this, MySetting.KEY_SAVE_LOGIN, "");

                Intent loginIntent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                MainActivity.instance.finish();
                finish();
                break;
        }

    }




}
