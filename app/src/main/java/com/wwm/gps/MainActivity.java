package com.wwm.gps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;

import com.baidu.mapapi.SDKInitializer;
import com.loopj.android.http.*;

import org.apache.http.Header;


/**
 * Created by wwm on 2016/8/11.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private Button btnPosition;
    private Button btn_location_base;
    private Button btn_map_base;
    private TextView tv;

    private static final String TAG = MainActivity.class.getSimpleName();
    private double latitude = 0.0;
    private double longitude = 0.0;
    private TextView info;
    private LocationManager locationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*获得locationManager服务*/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps) {
            getLocation();
        } else {
            openGPS();
            toggleGPS();
            new Handler() {
            }.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getLocation();
                }
            }, 2000);
        }
        /*得到布局中的所有对象*/
        findView();
        setListener();
//        login();
        btn_location_base.setOnClickListener(this);
        btn_map_base.setOnClickListener(this);
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

    private void toggleGPS() {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }

    private void getLocation() {
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        }
        if (tv == null) {
            findView();
        }
        tv.setText("纬度：" + latitude + "\n" + "经度：" + longitude);
    }

    private void findView() {
        btnPosition = (Button) findViewById(R.id.position);
        btn_location_base = (Button) findViewById(R.id.btn_location_base);
        btn_map_base=(Button)findViewById(R.id.btn_map_base);
        tv = (TextView) findViewById(R.id.tv);
    }

    private void setListener() {
        /*监听位置变化,2秒一次,距离10米以上*/
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        /*设置对象的监听器*/
        btnPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setText("当前的经度:\n当前的纬度");
            }
        });
    }

    /*位置监听器*/
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("onLocationChanged", "come in");
            if (location != null) {
                Log.w("Location", "Current altitude = " + location.getAltitude());
                Log.w("Location", "Current latitude = " + location.getLatitude());
                latitude = location.getLatitude(); // 经度
                longitude = location.getLongitude(); // 纬度
            }
            tv.setText("当前的经度:" + location.getLatitude() + ",\n当前的纬度:" + location.getLongitude());

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            Log.e(TAG, s);
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.e(TAG, s);
        }
    };

    private void login() {
        String webServiceUrl = "http://60.29.110.104:8082/api/";
        String loginURL = "Account/authenticate";
        String testURL = "Account/test";
        try {
            String dataParse = "name=" + URLEncoder.encode("admin", "UTF-8") + "&password=" + URLEncoder.encode("123", "UTF-8");
            AsyncHttpClient client = new AsyncHttpClient();
//            client.get(webServiceUrl + testURL, new AsyncHttpResponseHandler() {
//                @Override
//                public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                    Log.i("success:", new String(bytes));
//                    Toast.makeText(MainActivity.this, "get成功。", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                    Log.i("failure:", new String(bytes));
//                    Toast.makeText(MainActivity.this, "get失败。", Toast.LENGTH_SHORT).show();
//                }
//            });
            //post
            RequestParams params = new RequestParams();
            params.add("name", "admin");
            params.add("password", "123");
            client.post(webServiceUrl + loginURL, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Log.i("登录成功：", new String(bytes));
                    Toast.makeText(MainActivity.this, new String(bytes), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.i("登录失败：", new String(bytes));
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_location_base:
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                intent.putExtra("Position1", latitude);
                intent.putExtra("Position2", longitude);
                startActivity(intent);
                break;
            case R.id.btn_map_base:
                Intent intent1 = new Intent(MainActivity.this, mapActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }
}
