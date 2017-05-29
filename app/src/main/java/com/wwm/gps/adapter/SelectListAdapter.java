package com.wwm.gps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wwm.gps.R;
import com.wwm.gps.bean.Item;

import java.util.List;

public class SelectListAdapter extends BaseAdapter {

	private Context context;
	private List<Item> list;
	LayoutInflater inflater;
	private int selected = -1;

	public SelectListAdapter(Context context, List<Item> list) {
		// TODO Auto-generated constructor stub
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = new ViewHolder();
		
		if (v==null){
			v = inflater.inflate(R.layout.dialog_select_list_item, null);
			viewHolder.tvName = (TextView) v.findViewById(R.id.tv_select_popup_name);
			v.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) v.getTag();
		}
		
		Item info = list.get(position);
		
		if (info != null){
			viewHolder.tvName.setText(info.getTitle());
		}

		if (selected >= 0 && position == selected) {
			viewHolder.tvName.setTextColor(context.getResources().getColor(R.color.blue));
		} else {
			viewHolder.tvName.setTextColor(context.getResources().getColor(R.color.black));
		}
		
		return v;
	}
	
	private class ViewHolder{

		TextView tvName;
	}

	public void setSelected(int selected) {
		this.selected = selected;
		notifyDataSetChanged();
	}

}