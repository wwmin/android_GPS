package com.wwm.gps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wwm.gps.R;
import com.wwm.gps.bean.HomeMenu;

import java.util.List;

/**
 * Created by ly on 16/9/4.
 */
public class MainMenuAdapter extends BaseAdapter {

    private Context context;
    private List<HomeMenu> inforList;
    private int nowPosition = 0;

    public MainMenuAdapter(Context context, List<HomeMenu> inforList) {
        this.context = context;
        this.inforList = inforList;
    }

    @Override
    public int getCount() {
        return inforList.size();
    }

    @Override
    public Object getItem(int position) {
        return inforList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return nowPosition;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.main_menu_item, null);
            holder = new ViewHolder();

            holder.iv_menu = (ImageView) convertView.findViewById(R.id.iv_home_menu);
            holder.tv_menu_name = (TextView) convertView.findViewById(R.id.tv_home_menu_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HomeMenu menu = inforList.get(position);
        holder.iv_menu.setImageDrawable(context.getResources().getDrawable(menu.getImg()));
        holder.tv_menu_name.setText(menu.getName());

        return convertView;
    }

    class ViewHolder {
    	public ImageView iv_menu;
    	public TextView tv_menu_name;

    }
}
