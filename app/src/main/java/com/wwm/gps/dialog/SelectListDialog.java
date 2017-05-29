package com.wwm.gps.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wwm.gps.R;
import com.wwm.gps.adapter.SelectListAdapter;
import com.wwm.gps.bean.Item;

import java.util.List;


public class SelectListDialog {
	
	private AlertDialog dialog;
	private ListView listView;
	private SelectListAdapter popupAdapter;
	private TextView tv_title;
	private EditText et_other;
	private RelativeLayout rl_cancel;
	
	public ListView getListView(){
		return listView;
	}
	
	public AlertDialog getDialog(){
		return dialog;
	}

	public EditText getOther(){
		return et_other;
	}

	public RelativeLayout getRlBtn(){
		return rl_cancel;
	}

	public void showdialog(Context context, String title, List<Item> list){


		dialog = new AlertDialog.Builder(context).create();
		dialog.show();

		Window window = dialog.getWindow();
		window.setContentView(R.layout.dialog_select_list);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = (int)(wm.getDefaultDisplay().getWidth() * 0.8);
		int height = (int)(wm.getDefaultDisplay().getHeight() * 0.7);

		window.setLayout(width, LayoutParams.WRAP_CONTENT);
//		window.setWindowAnimations(R.style.AnimBottom);
		window.setGravity(Gravity.CENTER);

		et_other = (EditText) window.findViewById(R.id.et_other);
		listView = (ListView) window.findViewById(R.id.list_select_popup);
		tv_title = (TextView) window.findViewById(R.id.tv_dialog_list_title);
		tv_title.setText(title);
		rl_cancel = (RelativeLayout) window.findViewById(R.id.rl_select_dialog_cancel);
		popupAdapter = new SelectListAdapter(context, list);
		listView.setAdapter(popupAdapter);
		
        rl_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
	}
	
}
