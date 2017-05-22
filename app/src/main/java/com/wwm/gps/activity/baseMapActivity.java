package com.wwm.gps.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.wwm.gps.R;
import com.wwm.gps.application.LocationApplication;
import com.wwm.gps.application.MainApplication;
import com.wwm.gps.constant.UrlUtils;
import com.wwm.gps.service.LocationService;
import com.wwm.gps.service.XCService;
import com.wwm.gps.utils.BaiduMapUtils;
import com.wwm.gps.utils.Common;
import com.wwm.gps.utils.DateUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by wwmin on 2017/5/22.
 */

public class baseMapActivity extends BaseActivity {
    private TextView tv_state, tv_time, tv_mileage;
    private Button btn_xc;

    private String trackId = "0";
    private int myTime = 0;
    private int meter = 0;
    private float direction=0;

    private MsgReceiver msgReceiver;
    private boolean firstIn = true;

    private MapView map_yzt;
    private LocationService locService;
    private BaiduMap mBaiduMap;
    private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果
    private boolean isFirstLoc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_map_layout);


        init();
        initMap();

        //动态注册广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.wwm.gps.RECEIVER");
        registerReceiver(msgReceiver, intentFilter);
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.top_view_text);
        tv_title.setText(R.string.map_base);
        iv_back = (ImageView) findViewById(R.id.top_view_back);
        iv_back.setVisibility(View.GONE);

        tv_state = (TextView) findViewById(R.id.tv_xc_state);
        tv_time = (TextView) findViewById(R.id.tv_xc_time);
        tv_mileage = (TextView) findViewById(R.id.tv_xc_mileage);
        btn_xc = (Button) findViewById(R.id.btn_xc);
        btn_xc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_xc.getText().equals("开始定位")) {
//                    mLocationClient.start();
                    httpStartEndXC(0);
                } else if (btn_xc.getText().equals("结束定位")){
//                    mLocationClient.stop();
                    httpStartEndXC(1);
                }

            }
        });

        map_yzt = (MapView) findViewById(R.id.mv_map_yzt);

    }
    private void initMap() {
        mBaiduMap = map_yzt.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
//        locService = ((MainApplication) getApplication()).locationService;
        locService = ((LocationApplication) getApplication()).locationService;
        LocationClientOption mOption = locService.getDefaultLocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mOption.setCoorType("bd09ll");
        mOption.setScanSpan(0);
        mOption.setOpenGps(false);
        mOption.setLocationNotify(false);
        locService.setLocationOption(mOption);
        locService.registerListener(listener);
        locService.start();

    }
    /***
     * 定位结果回调，在此方法中处理定位结果
     */
    private BDLocationListener listener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub

            if (location != null && (location.getLocType() == 61 || location.getLocType() == 161 || location.getLocType() == 66)) {
//                Toast.makeText(XCActivity.this,location.getLocType()+","+ location.getLatitude(),Toast.LENGTH_SHORT).show();
                Message locMsg = locHander.obtainMessage();
                Bundle locData;
                locData = Algorithm(location);
                if (locData != null) {
                    locData.putParcelable("loc", location);
                    locMsg.setData(locData);
                    locHander.sendMessage(locMsg);
                }
            }
        }

        public void onConnectHotSpotMessage(String s, int i){

        }
    };
    /***
     * 接收定位结果消息
     */
    private Handler locHander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            try {
                BDLocation location = msg.getData().getParcelable("loc");
                int iscal = msg.getData().getInt("iscalculate");
                if (location != null) {
                    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());

                    MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(location.getDirection()).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
                    mBaiduMap.setMyLocationData(locData);
//                        isFirstLoc = false;
                    locService.stop();
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(point).zoom(15.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    };

    /***
     * 平滑策略代码实现方法，主要通过对新定位和历史定位结果进行速度评分，
     * 来判断新定位结果的抖动幅度，如果超过经验值，则判定为过大抖动，进行平滑处理,若速度过快，
     * 则推测有可能是由于运动速度本身造成的，则不进行低速平滑处理 ╭(●｀∀´●)╯
     *
     * @param location
     * @return Bundle
     */
    private Bundle Algorithm(BDLocation location) {
        Bundle locData = new Bundle();
        double curSpeed = 0;
        if (locationList.isEmpty() || locationList.size() < 2) {
            LocationEntity temp = new LocationEntity();
            temp.location = location;
            temp.time = System.currentTimeMillis();
            locData.putInt("iscalculate", 0);
            locationList.add(temp);
        } else {
            if (locationList.size() > 5)
                locationList.removeFirst();
            double score = 0;
            for (int i = 0; i < locationList.size(); ++i) {
                LatLng lastPoint = new LatLng(locationList.get(i).location.getLatitude(),
                        locationList.get(i).location.getLongitude());
                LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
                double distance = DistanceUtil.getDistance(lastPoint, curPoint);
                curSpeed = distance / (System.currentTimeMillis() - locationList.get(i).time) / 1000;
                score += curSpeed * BaiduMapUtils.EARTH_WEIGHT[i];
            }
            if (score > 0.00000999 && score < 0.00005) { // 经验值,开发者可根据业务自行调整，也可以不使用这种算法
                location.setLongitude(
                        (locationList.get(locationList.size() - 1).location.getLongitude() + location.getLongitude())
                                / 2);
                location.setLatitude(
                        (locationList.get(locationList.size() - 1).location.getLatitude() + location.getLatitude())
                                / 2);
                locData.putInt("iscalculate", 1);
            } else {
                locData.putInt("iscalculate", 0);
            }
            LocationEntity newLocation = new LocationEntity();
            newLocation.location = location;
            newLocation.time = System.currentTimeMillis();
            locationList.add(newLocation);

        }
        return locData;
    }
    /**
     * 开始巡查
     * falg 0开始 1结束
     */
    private void httpStartEndXC(final int flag){
        FinalHttp mHttp = new FinalHttp();
        mHttp.configCharset("utf-8");

        AjaxParams params = new AjaxParams();
//        params.put("UserID", Common.getUserId(baseMapActivity.this));
        params.put("TrackID", trackId);
        params.put("ElapsedTime", myTime+"");
        params.put("TrackMileage", (int)meter +"");
        if(flag==0){
            setStartXC();
        }else{
            setEndXC();
        }

//        mHttp.post(UrlUtils.SET_SUBGPS_INFO, params, new AjaxCallBack<String>() {
//            @Override
//            public void onSuccess(String content) {
//                Log.e("SET_MAINGPS_INFO", content);
//                try {
//                    JSONObject jsonObj = new JSONObject(content);
//                    boolean result = jsonObj.getBoolean("result");
//                    if (result) {
//                        if (flag == 0){
//                            JSONObject data = jsonObj.getJSONObject("data");
//                            trackId = data.getString("TrackID");
//                            setStartXC();
//                        } else if (flag == 1){
//                            trackId = "0";
//                            setEndXC();
//                        }
//
//                    }
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(Throwable t, int errorNo, String strMsg) {
//                Toast.makeText(baseMapActivity.this, getString(R.string.http_failed), Toast.LENGTH_SHORT).show();
//                Log.e("error", errorNo+"");
//            }
//        });
    }
    private void setStartXC () {

//        Intent dwintent = new Intent(XCActivity.this, DWService.class);
//        stopService(dwintent);

//        nowState = true;
        tv_state.setText("定位中");
        btn_xc.setText("结束定位");
        btn_xc.setBackgroundResource(R.drawable.btn_circle_red);

        Intent intent = new Intent(baseMapActivity.this, XCService.class);
        intent.putExtra("trackId", trackId);
        startService(intent);

    }


    private void setEndXC (){
        //owState = false;
        tv_state.setText("待定位");
        btn_xc.setText("开始定位");
        btn_xc.setBackgroundResource(R.drawable.btn_circle_blue);
        myTime = 0;
        tv_time.setText("0时0分");
        tv_mileage.setText("0米");

        //注销广播
//        unregisterReceiver(msgReceiver);

        Intent intent = new Intent(baseMapActivity.this, XCService.class);
        stopService(intent);

//        Intent dwintent = new Intent(XCActivity.this, DWService.class);
//        intent.putExtra("userId", Common.getUserId(XCActivity.this));
//        startActivity(dwintent);

    }
    @Override
    protected void onPause() {
        map_yzt.onPause();
        super.onPause();
    }
    /**
     * 广播接收器
     */
    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //拿到进度，更新UI
            myTime = intent.getIntExtra("time", 0);
            meter = intent.getIntExtra("meter", 0);
            double lat = intent.getDoubleExtra("lat", 0);
            double lng = intent.getDoubleExtra("lng", 0);
            direction=intent.getFloatExtra("direction",0);
            Log.e("xcservice_back",lat+"");
            tv_time.setText(DateUtils.secToTime(myTime));
            tv_mileage.setText(meter + "米");
            LatLng point = new LatLng(lat, lng);

            MyLocationData locData = new MyLocationData.Builder()
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(direction).latitude(lat).longitude(lng).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(point).zoom(15.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }

            if (firstIn) {
                firstIn = false;
                tv_state.setText("定位中");
                btn_xc.setText("结束定位");
                btn_xc.setBackgroundResource(R.drawable.btn_circle_red);
            }

        }

    }
    @Override
    protected void onResume() {
        map_yzt.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        locService.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        map_yzt.onDestroy();
        map_yzt = null;
        super.onDestroy();
    }
    /**
     * 封装定位结果和时间的实体类
     */
    class LocationEntity {
        BDLocation location;
        long time;
    }
}


