package com.wwm.gps.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wwm.gps.R;
import com.wwm.gps.constant.UrlUtils;
import com.wwm.gps.utils.Common;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdatePwdActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_old_pwd;
    private EditText et_new_pwd;
    private EditText et_confirm_pwd;
    private Button btn_update_pwd;

    private String oldPwd;
    private String newPwd;
    private String confirmPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);

        init();

    }

    private void init () {
        tv_title = (TextView) findViewById(R.id.top_view_text);
        tv_title.setText(R.string.update_pwd);
        iv_back = (ImageView) findViewById(R.id.top_view_back);
        iv_back.setVisibility(View.GONE);

        et_old_pwd = (EditText) findViewById(R.id.et_old_pwd);
        et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
        et_confirm_pwd = (EditText) findViewById(R.id.et_confirm_pwd);
        btn_update_pwd = (Button) findViewById(R.id.btn_update_pwd);
        btn_update_pwd.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_update_pwd:
                oldPwd = et_old_pwd.getText().toString();
                newPwd = et_new_pwd.getText().toString();
                confirmPwd = et_confirm_pwd.getText().toString();
                if (oldPwd.isEmpty()) {
                    Toast.makeText(UpdatePwdActivity.this, "原密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (newPwd.isEmpty()) {
                    Toast.makeText(UpdatePwdActivity.this, "新密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (!newPwd.equals(confirmPwd)) {
                    Toast.makeText(UpdatePwdActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                } else {
                    httpUpdatePassword();
                }
                break;
        }

    }

    private void httpUpdatePassword(){
        FinalHttp mHttp = new FinalHttp();
        mHttp.configCharset("utf-8");

        AjaxParams params = new AjaxParams();
        params.put("UserID", Common.getUserId(UpdatePwdActivity.this));
        params.put("UserPassword", newPwd);
        Log.e("pUserID", Common.getUserId(UpdatePwdActivity.this));

        mHttp.post(UrlUtils.UPDATE_PASS, params, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String content) {
                Log.e("UPDATE_PASS", content);
                try {
                    JSONObject jsonObj = new JSONObject(content);
                    boolean result = jsonObj.getBoolean("result");
                    if (result) {
                        Toast.makeText(UpdatePwdActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                Toast.makeText(UpdatePwdActivity.this, getString(R.string.http_failed), Toast.LENGTH_SHORT).show();
                Log.e("error", errorNo+"");
            }
        });
    }


}
