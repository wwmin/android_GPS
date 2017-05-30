package com.wwm.gps.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.wwm.gps.application.LocationApplication;
import com.wwm.gps.application.MainApplication;
import com.wwm.gps.utils.BaiduMapUtils;
import com.wwm.gps.utils.DateUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ly on 2016/9/19.
 */
public class XCService extends Service {

    private String trackId = "0";
    private int myTime = 0;
    private Handler handler;
    private Timer timer;

    private boolean nowState = true;
    private int meter = 0;

    private double preLng = 0;
    private double preLat = 0;
    private float direction=0;
    private Intent sendIntent = new Intent("com.wwm.gps.RECEIVER");
    private boolean isStop = true;

    private LocationClient mLocClient;
    private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果
    private boolean isFirstLoc = true;
    private LocationService locService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initMap();

    }


    private void initMap() {
        // 定位初始化
//        mLocClient = new LocationClient(this);
//        mLocClient.registerLocationListener(myListener);
//        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//        option.setCoorType("bd09ll");
//        option.setScanSpan(3000);
//        option.setOpenGps(true);
//        option.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
//        option.setLocationNotify(true);//true可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        mLocClient.start();
//        mLocClient.requestNotifyLocation();

//        locService = ((MainApplication) getApplication()).locationService;
        locService = ((LocationApplication) getApplication()).locationService;
        LocationClientOption mOption = locService.getDefaultLocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mOption.setCoorType("bd09ll");
        mOption.setScanSpan(3000);
        mOption.setOpenGps(true);
        mOption.setLocationNotify(true);
        locService.setLocationOption(mOption);
        locService.registerListener(myListener);
        locService.start();

        TimerTask task = new TimerTask(){
            public void run() {
                myTime++;
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer = new Timer();
        timer.schedule(task, 1000, 1000);

        handler = new Handler(){
            public void handleMessage(Message msg) {
                if (msg.what == 1){
                    sendIntent.putExtra("time", myTime);
                    sendIntent.putExtra("meter", meter);
                    sendIntent.putExtra("lat", preLat);
                    sendIntent.putExtra("lng", preLng);
                    sendIntent.putExtra("direction",direction);
                    sendBroadcast(sendIntent);
                }
                super.handleMessage(msg);
            }
        };

    }


    /***
     * 定位结果回调，在此方法中处理定位结果
     */
    private BDLocationListener myListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub

            if (location != null && (location.getLocType() == 61 || location.getLocType() == 161 || location.getLocType() == 66)) {
                Log.e("type", location.getLocType()+"");
                Message locMsg = locHandler.obtainMessage();
                Bundle locData = Algorithm(location);
                if (locData != null) {
                    locData.putParcelable("loc", location);
                    locMsg.setData(locData);
                    locHandler.sendMessage(locMsg);
                }
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    };

    /***
     * 平滑策略代码实现方法，主要通过对新定位和历史定位结果进行速度评分，
     *
     * @param
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

    /***
     * 接收定位结果消息
     */
    private Handler locHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            try {
                BDLocation location = msg.getData().getParcelable("loc");
                int iscal = msg.getData().getInt("iscalculate");
                int m = 0;
                if (location != null) {
                    if (preLng != 0 && preLat != 0){
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        LatLng prelatLng = new LatLng(preLat, preLng);
                        meter += DistanceUtil.getDistance(latLng, prelatLng);
                        m = (int) DistanceUtil.getDistance(latLng, prelatLng);
                    }
                    preLng = location.getLongitude();
                    preLat = location.getLatitude();
                    direction=location.getDirection();
                    if (nowState){
                        //上传数据
                        if (m < 100){
//                            httpAddGpsInfo(location.getLongitude(), location.getLatitude());
                        }

                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    };

    /**
     * 封装定位结果和时间的实体类
     *
     * @author baidu
     *
     */
    class LocationEntity {
        BDLocation location;
        long time;
    }

    /**
     * 添加GPS点位信息
     */
    public void httpAddGpsInfo(double x, double y){
        FinalHttp mHttp = new FinalHttp();
        mHttp.configCharset("utf-8");
        AjaxParams params = new AjaxParams();
        params.put("RealTimePositionID", "0");
        params.put("UploadTime", DateUtils.getStringToday());
        params.put("RealTimePositionX", x+""); //经度
        params.put("RealTimePositionY", y+""); //纬度
        params.put("GpsDirection", "");
        params.put("GpsSpeed", "");
        params.put("GpsWarning", "");
        params.put("GpsStatus", "");
        params.put("TrackID", trackId);
        params.put("UserID", "0");
        params.put("UploadType", "1");

//        mHttp.post(UrlUtils.SET_SUBGPS_INFO, params, new AjaxCallBack<String>() {
//            @Override
//            public void onSuccess(String content) {
//                Log.e("service_SET_SUBGPS_INFO", content);
//                try {
//                    JSONObject jsonObj = new JSONObject(content);
//                    boolean result = jsonObj.getBoolean("result");
//                    if (result) {
////                        JSONArray dataArr = jsonObj.getJSONArray("data");
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
//                Toast.makeText(XCService.this, getString(R.string.http_failed), Toast.LENGTH_SHORT).show();
//                Log.e("error", errorNo+"");
//            }
//        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        trackId = intent.getStringExtra("trackId");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        locService.stop();
//        mLocClient.stop();
        ((MainApplication) getApplication()).locationService.stop();
        isStop = false;
        timer.cancel();
//        mLocClient.unRegisterLocationListener(myListener); //注销掉监听
        super.onDestroy();
    }
}
