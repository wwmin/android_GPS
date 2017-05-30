package com.wwm.gps.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.wwm.gps.R;
import com.wwm.gps.adapter.MainMenuAdapter;
import com.wwm.gps.bean.HomeMenu;
import com.wwm.gps.constant.Constant;
import com.wwm.gps.data.LocalData;
import com.wwm.gps.dialog.TwoBtnDialog;
import com.wwm.gps.dialog.UpdateDialog;
import com.wwm.gps.loader.GlideImageLoader;
import com.wwm.gps.loader.GlidePauseOnScrollListener;
import com.wwm.gps.service.XCService;
import com.wwm.gps.utils.SPUtil;
import com.yixia.camera.VCamera;
import com.yixia.camera.util.DeviceUtils;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;

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

    private LocationManager locationManager;
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


        /*获得locationManager服务*/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gps) {
            openGPS();
        }

        initImageLoader();

        initGalleryFinal();

        initVideo();
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
                    case 4:
                        Intent cameraIntent = new Intent(MainActivity.this, CameraActivity.class);
                        startActivity(cameraIntent);
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

    /**
     * 6.0权限申请返回码
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted

                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "权限申请失败,您的图片上传功能将无法使用,请您通过权限后重新登录", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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

    private void openGPS() {
        Toast.makeText(MainActivity.this, "请打开GPS", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("请打开GPS");
        dialog.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    /*转到手机设置界面,用户设置GPS*/
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 0);//设置完成后返回到原来的界面
            }
        });
        dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    /**
     * ImageLoader创建及初始化
     *
     */
    private void initImageLoader() {
        // 创建默认的ImageLoader配置参数
        DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.icon_stub) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);

    }
    public void initGalleryFinal(){

        //设置主题
        ThemeConfig theme = ThemeConfig.CYAN;
//        ThemeConfig theme = new ThemeConfig.Builder()
//                .build();

        String imgPath = Environment.getExternalStorageDirectory() + "/DCIM" + "/images/";
        File mFile = new File(imgPath);
        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(false)
                .setEnableEdit(false)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .build();
        CoreConfig coreConfig = new CoreConfig.Builder(this, new GlideImageLoader(), theme)
                .setFunctionConfig(functionConfig)
                .setAnimation(0)
                .setTakePhotoFolder(mFile)
                .setPauseOnScrollListener(new GlidePauseOnScrollListener(false, true))
                .build();
        GalleryFinal.init(coreConfig);
    }
    private void initVideo () {
        //设置拍摄视频缓存路径
        File dcim = Environment.getExternalStorageDirectory();
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                VCamera.setVideoCachePath(dcim + "/DCIM/record/");
            } else {
                VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                        "/sdcard-ext/")
                        + "/recoder/");
            }
        } else {
            VCamera.setVideoCachePath(dcim + "/DCIM/record/");
        }

//		VCamera.setVideoCachePath(FileUtils.getRecorderPath());
        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(true);
        // 初始化拍摄SDK，必须
        VCamera.initialize(this);
    }
}
