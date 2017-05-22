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

public class SetTimeActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_time_set;
    private Button btn_update_set;

    private String mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);

        init();

    }

    private void init () {
        tv_title = (TextView) findViewById(R.id.top_view_text);
        tv_title.setText(R.string.set_time);
        iv_back = (ImageView) findViewById(R.id.top_view_back);
        iv_back.setVisibility(View.GONE);

        et_time_set = (EditText) findViewById(R.id.et_time_set);
        btn_update_set = (Button) findViewById(R.id.btn_update_set);
        btn_update_set.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_update_set:
                mTime = et_time_set.getText().toString();
                if (mTime.isEmpty()) {
                    Toast.makeText(SetTimeActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    httpUpdateTime();
                }
                break;
        }

    }

    private void httpUpdateTime(){
        FinalHttp mHttp = new FinalHttp();
        mHttp.configCharset("utf-8");

        AjaxParams params = new AjaxParams();
//        params.put("UserID", Common.getUserId(SetTimeActivity.this));
        params.put("UserID", "1");
        params.put("UserSetTime", mTime);

        mHttp.post(UrlUtils.SET_USERGPS_TIME, params, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String content) {
                Log.e("SET_USERGPS_TIME", content);
                try {
                    JSONObject jsonObj = new JSONObject(content);
                    boolean result = jsonObj.getBoolean("result");
                    if (result) {
                        Toast.makeText(SetTimeActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                Toast.makeText(SetTimeActivity.this, getString(R.string.http_failed), Toast.LENGTH_SHORT).show();
                Log.e("error", errorNo+"");
            }
        });
    }


}
