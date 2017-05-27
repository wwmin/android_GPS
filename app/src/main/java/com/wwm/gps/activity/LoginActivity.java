package com.wwm.gps.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wwm.gps.R;
import com.wwm.gps.bean.UserInfo;
import com.wwm.gps.bean.UserInfos;
import com.wwm.gps.constant.Constant;
import com.wwm.gps.constant.UrlUtils;
import com.wwm.gps.utils.MySetting;
import com.wwm.gps.utils.SPUtil;
import com.wwm.gps.view.DropEditText;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wwmin on 2017/5/22.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private DropEditText dep_name;
    private ArrayAdapter<String> dropAdapter;
    private EditText et_pwd;
    private CheckBox cb_show_pwd, cb_save_pwd;
    private Button btn_login;
    private TextView tv_login_title;

    private UserInfos userInfos = new UserInfos();
    private List<UserInfo> userList = new ArrayList<UserInfo>();
    private static final int BAIDU_READ_PHONE_STATE = 100;

    private ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        initData();

//        askPermission();
        httpGetSystemTitle();
    }


    public void init() {
        tv_title = (TextView) findViewById(R.id.top_view_text);
        tv_title.setText(R.string.login);
        iv_back = (ImageView) findViewById(R.id.top_view_back);
        iv_back.setVisibility(View.GONE);
        dep_name = (DropEditText) findViewById(R.id.atv_login_username);
        et_pwd = (EditText) findViewById(R.id.et_login_pwd);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        tv_login_title = (TextView) findViewById(R.id.tv_login_title);

        cb_show_pwd = (CheckBox) findViewById(R.id.cb_login_show_pwd);
        cb_save_pwd = (CheckBox) findViewById(R.id.cb_login_save_pwd);
    }

    private void initData() {

//        userInfos = MySetting.getSaveLogin(this);

        cb_show_pwd.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb_show_pwd.isChecked()) {
                    et_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    et_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
                et_pwd.postInvalidate();
                et_pwd.setSelection(et_pwd.getText().length());

            }
        });

        cb_save_pwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        String str = SPUtil.getData(LoginActivity.this, MySetting.KEY_SAVE_LOGIN, "").toString();
        //{"infos":[{"LoginPass":"","LoginName":"分局","CompanyId":0,"UserID":0,"CompLevel":0}]}
        JSONObject loginInfo = null;
        JSONArray infoArr;
        String[] strings = null;
        try {
            loginInfo = new JSONObject(str);
            infoArr = loginInfo.getJSONArray("infos");
            strings = new String[infoArr.length()];
            for (int i = 0; i < infoArr.length(); i++) {
                Gson gson = new Gson();
                UserInfo userInfo = gson.fromJson(infoArr.getString(i), UserInfo.class);
                userList.add(userInfo);
                strings[i] = userInfo.LoginName;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (strings != null) {
            dropAdapter = new ArrayAdapter<String>(this, R.layout.drop_list_item, strings);
            dep_name.setAdapter(dropAdapter);
        }
        dep_name.mPopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dep_name.setText(userList.get(position).LoginName);
                et_pwd.setText(userList.get(position).LoginPass);
                cb_save_pwd.setChecked(true);
                dep_name.mPopupWindow.dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                UserInfo userInfo = new UserInfo();
                String name = dep_name.getText().toString();
                String pwd = et_pwd.getText().toString();
                userInfo.LoginName = name;
                userInfo.LoginPass = pwd;
                httpLogin(userInfo);

//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
                break;
        }
    }

    private void httpLogin(final UserInfo userInfo) {
        showProgressDialog();
        FinalHttp mHttp = new FinalHttp();
        mHttp.configCharset("utf-8");

        AjaxParams params = new AjaxParams();
        params.put("name", userInfo.LoginName);
        params.put("password", userInfo.LoginPass);
//        params.put("UserName", userInfo.LoginName);
//        params.put("UserPassword", userInfo.LoginPass);

        String url = mHttp.getUrlWithQueryString(UrlUtils.LOGIN, params);

        mHttp.post(UrlUtils.LOGIN, params, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String content) {
                Log.i("LOGIN", content);
                try {
                    JSONObject jsonObj = new JSONObject(content);
                    boolean result = jsonObj.getBoolean("success");
                    if (result) {
                        JSONObject data = jsonObj.getJSONObject("_currentUser");
                        String company = data.getString("truename");
                        String user = data.getString("name");

                        SPUtil.saveData(LoginActivity.this, Constant.SP_COMPANY, company);
                        SPUtil.saveData(LoginActivity.this, Constant.SP_USER_INFO, user);

                        // 保存登录信息
                        if (!cb_save_pwd.isChecked()) {
                            userInfo.LoginPass = "";
                        }
                        MySetting.saveLogin(LoginActivity.this, userInfos, userInfo);

                        hideProgressDialog();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        String error = jsonObj.getString("error");
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }

                } catch (JSONException e) {
                    hideProgressDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                Toast.makeText(LoginActivity.this, "登录失败后的回调" + getString(R.string.http_failed), Toast.LENGTH_SHORT).show();
                Log.e("error", errorNo + "");
                hideProgressDialog();
            }
        });
    }

    private void httpGetSystemTitle() {
        FinalHttp mHttp = new FinalHttp();
        mHttp.configCharset("utf-8");

        AjaxParams params = new AjaxParams();
        params.put("", "1");
        SPUtil.saveData(LoginActivity.this, Constant.SP_SYS_TITLE, "GPS定位系统");
        tv_login_title.setText("GPS定位系统");
//        mHttp.post(UrlUtils.GET_SYSTEM_TITLE, params, new AjaxCallBack<String>() {
//            @Override
//            public void onSuccess(String content) {
//                Log.i("GET_SYSTEM_TITLE", content);
//                try {
//                    JSONObject jsonObj = new JSONObject(content);
//                    boolean result = jsonObj.getBoolean("result");
//                    if (result) {
////                        JSONObject data = jsonObj.getJSONObject("data");
//                        String title = jsonObj.getString("data");
//                        String mTitle = title.replace("\\n", "\n");
//                        SPUtil.saveData(LoginActivity.this, Constant.SP_SYS_TITLE, mTitle);
//                        tv_login_title.setText(mTitle);
//
//                    } else {
//                        String error = jsonObj.getString("error");
//                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(Throwable t, int errorNo, String strMsg) {
//                Toast.makeText(LoginActivity.this, "获取app标题信息失败:"+getString(R.string.http_failed), Toast.LENGTH_SHORT).show();
//                Log.e("error", errorNo+"");
//            }
//        });
    }

    private void showProgressDialog() {
        if (pd != null) {
            pd.cancel();
        }
        pd = new ProgressDialog(this);
        pd.setTitle("登录提示");
        pd.setMessage("正在登录中...");
        pd.show();
    }

    private void hideProgressDialog() {
        if (pd != null) {
            pd.dismiss();
        }
        pd = null;
    }
}
