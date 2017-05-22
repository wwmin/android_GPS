package com.wwm.gps.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.wwm.gps.R;
import com.wwm.gps.adapter.MainMenuAdapter;
import com.wwm.gps.bean.Company;
import com.wwm.gps.bean.HomeMenu;
import com.wwm.gps.bean.LoginInfo;
import com.wwm.gps.constant.Constant;
import com.wwm.gps.data.LocalData;
import com.wwm.gps.dialog.TwoBtnDialog;
import com.wwm.gps.dialog.UpdateDialog;
import com.wwm.gps.service.XCService;
import com.wwm.gps.utils.SPUtil;

//import org.apache.http.Header;


/**
 * Created by wwm on 2016/8/11.
 */
public class MainActivity extends BaseActivity{
    private GridView gv_menu;
    private MainMenuAdapter menuAdapter;
    private List<HomeMenu> menuList = new ArrayList<HomeMenu>();

    private TextView tv_info;

    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;
    public static MainActivity instance = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        init();

        //检查版本更新
        UpdateDialog updateDialog = new UpdateDialog();
        updateDialog.checkUpdate(MainActivity.this, "main");

        String companyData = SPUtil.getData(MainActivity.this, Constant.SP_COMPANY, "").toString();
        String userData = SPUtil.getData(MainActivity.this, Constant.SP_USER_INFO, "").toString();
        Gson gson = new Gson();
//        Company company = gson.fromJson(companyData, Company.class);
//        LoginInfo info = gson.fromJson(userData, LoginInfo.class);

//        tv_info.setText(company.getCompName() + "—" + info.getUserRealName());
        tv_info.setText("GPS定位应用");
        menuList = LocalData.getHomeMenu();
//        if (company.getCompLevel() >= 3) {
//            menuList.remove(0);
//        }
        menuAdapter = new MainMenuAdapter(MainActivity.this, menuList);
        gv_menu.setAdapter(menuAdapter);

        getPermission();

//        Intent intent = new Intent(MainActivity.this, DWService.class);
//        intent.putExtra("userId", Common.getUserId(MainActivity.this));
//        startService(intent);



//        setContentView(R.layout.activity_main);
//        /*获得locationManager服务*/
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        if (gps) {
//            getLocation();
//        } else {
//            openGPS();
//            toggleGPS();
//            new Handler() {
//            }.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    getLocation();
//                }
//            }, 2000);
//        }
//        /*得到布局中的所有对象*/
//        findView();
//        setListener();
////        login();
//        btn_location_base.setOnClickListener(this);
//        btn_map_base.setOnClickListener(this);
//        btn_base_map.setOnClickListener(this);
    }
    private void init() {
        tv_title = (TextView) findViewById(R.id.top_view_text);
        String title = SPUtil.getData(MainActivity.this, Constant.SP_SYS_TITLE, "定位系统").toString();
        tv_title.setText(title);
        iv_back = (ImageView) findViewById(R.id.top_view_back);
        iv_back.setVisibility(View.GONE);

        tv_info = (TextView) findViewById(R.id.tv_home_user_info);


        gv_menu = (GridView) findViewById(R.id.gv_main_menu);
        gv_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (menuList.get(position).getId()) {
                    case 1:
                        Intent testIntent = new Intent(MainActivity.this, TestActivity.class);
                        startActivity(testIntent);
                        break;
                    case 2:
                        Intent mapIntent = new Intent(MainActivity.this, mapActivity.class);
                        startActivity(mapIntent);
                        break;
                    case 3:
                        Intent advanceMapIntent = new Intent(MainActivity.this, baseMapActivity.class);
                        startActivity(advanceMapIntent);
                        break;
                    case 13:
                        Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(settingIntent);
                        break;
                }
            }
        });

    }

    @TargetApi(23)
    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    @Override
    protected void onDestroy() {
//        Intent intent = new Intent(MainActivity.this, DWService.class);
//        stopService(intent);
        super.onDestroy();
    }

    private List<String> getData(){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            String str = "";
            list.add(str);
        }

        return list;

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            TwoBtnDialog btnDialog = new TwoBtnDialog();
            btnDialog.showdialog(this, "确定退出吗?", "退出", "取消");
            btnDialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, XCService.class);
                    stopService(intent);
                    finish();
                }
            });
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
