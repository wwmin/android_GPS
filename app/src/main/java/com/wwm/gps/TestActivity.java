package com.wwm.gps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.Poi;
import com.wwm.gps.service.LocationService;

import java.util.List;

import static com.wwm.gps.R.id.tv_bd;

/**
 * Created by wwm on 2017/5/9.
 */

public class TestActivity extends Activity {
    private LocationService locationService;
    private TextView LocationResult;
    private Button startLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test);
        Intent intent = getIntent();
        String pos1 = intent.getStringExtra("Position1");
        String pos2 = intent.getStringExtra("Position2");

//        txt_x = (TextView) findViewById(R.id.txt_x);
//        txt_y = (TextView) findViewById(R.id.txt_y);
//        txt_x.setText(pos1);
//        txt_y.setText(pos2);

        LocationResult = (TextView) findViewById(tv_bd);
        LocationResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        startLocation = (Button) findViewById(R.id.btn_bd);
    }

    @Override
    protected void onStop() {
        //Stop location service
        locationService.unregisterListener(mListener);//注销掉监听
        locationService.stop();//停止定位服务
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // -----------location config ------------
        locationService = ((LocationApplication) getApplication()).locationService;
//        locationService = new LocationService(getApplicationContext());
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());////可在别处设置option
        startLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startLocation.getText().toString().equals(getString(R.string.startLocation))) {
                    locationService.start();//定位开始
                    // start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
                    startLocation.setText(getString(R.string.stopLocation));
                } else {
                    locationService.stop();
                    startLocation.setText(getString(R.string.startLocation));
                }
            }
        });
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                //获取定位结果
                StringBuffer sb = new StringBuffer(256);
                /*
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append("time : ");
                sb.append(location.getTime());    //获取定位时间
                sb.append("\nerror code : ");
                sb.append(location.getLocType());    //获取类型类型
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());    //获取纬度信息
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());    //获取经度信息
                sb.append("\nradius : ");
                sb.append(location.getRadius());    //获取定位精准度
                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    // GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());    // 单位：公里每小时
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());    //获取卫星数
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());    //获取海拔高度信息，单位米
                    sb.append("\ndirection : ");
                    sb.append(location.getDirection());    //获取方向信息，单位度
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());    //获取地址信息
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    // 网络定位结果
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());    //获取地址信息
                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());    //获取运营商信息
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    // 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不通导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                sb.append("\nlocationdescribe : ");
                sb.append(location.getLocationDescribe());    //位置语义化信息
                List<Poi> list = location.getPoiList();    // POI数据
                if (list != null) {
                    sb.append("\npoilist size = : ");
                    sb.append(list.size());
                    for (Poi p : list) {
                        sb.append("\npoi= : ");
                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                    }
                }
                Log.i("BaiduLocationApiDem", sb.toString());
                logMsg(sb.toString());
            }
        }

        /**
         * 显示请求字符串
         *
         * @param str msg
         */
        private void logMsg(String str) {
            final String s = str;
            try {
                if (LocationResult != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            LocationResult.post(new Runnable() {
                                @Override
                                public void run() {
                                    LocationResult.setText(s);
                                }
                            });

                        }
                    }).start();
                }
                //LocationResult.setText(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    };
}
