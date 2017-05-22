package com.wwm.gps.data;

import com.wwm.gps.R;
import com.wwm.gps.bean.HomeMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wwmin on 2017/5/22.
 */

public class LocalData {
    public static List<HomeMenu> getHomeMenu () {
        List<HomeMenu> list = new ArrayList<HomeMenu>();

        HomeMenu menu01 = new HomeMenu();
        menu01.setId(1);
        menu01.setName("无图定位");
        menu01.setImg(R.drawable.home_icon_01);
        list.add(menu01);

        HomeMenu menu02 = new HomeMenu();
        menu02.setId(2);
        menu02.setName("基础定位");
        menu02.setImg(R.drawable.home_icon_02);
        list.add(menu02);

        HomeMenu menu03 = new HomeMenu();
        menu03.setId(3);
        menu03.setName("高级定位");
        menu03.setImg(R.drawable.home_icon_03);
        list.add(menu03);


        HomeMenu menu13 = new HomeMenu();
        menu13.setId(13);
        menu13.setName("系统设置");
        menu13.setImg(R.drawable.home_icon_13);
        list.add(menu13);

        return list;

    }
}
