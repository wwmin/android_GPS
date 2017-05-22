package com.wwm.gps.application;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.wwm.gps.service.LocationService;

/**
 * Created by wwmin on 2017/5/22.
 */

public class MainApplication extends Application {
    public static Context applicationContext;
    private static MainApplication instance;

    public LocationService locationService;
    public Vibrator mVibrator;

    @Override
    public void onCreate(){
        super.onCreate();
        applicationContext=this;
        instance=this;

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());
    }
    public static MainApplication getInstance() {
        return instance;
    }
}
