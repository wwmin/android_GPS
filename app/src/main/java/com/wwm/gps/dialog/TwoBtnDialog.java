package com.wwm.gps.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.wwm.gps.R;

public class TwoBtnDialog {

	private TextView tv_title;
	private TextView btn_ok;
	private TextView btn_cancle;
	private TextView tv_content;
	private AlertDialog dialog;
	
	public TextView getTitle(){
		return tv_title;
	}
	
	public TextView getBtnOk(){
		return btn_ok;
	}
	public TextView getBtnCancle(){
		return btn_cancle;
	}
	
	public AlertDialog getDialog(){
		return dialog;
	}
	
	public void showdialog(Context context, String content, String ok, String cancle){

		dialog = new AlertDialog.Builder(context).create();
		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.dialog_two_btn);
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.CENTER);
		tv_title = (TextView) window.findViewById(R.id.tv_two_dialog_title);
		tv_content = (TextView) window.findViewById(R.id.tv_two_dialog_content);
		tv_content.setText(content);
		btn_ok = (TextView) window.findViewById(R.id.tv_two_dialog_ok);
		btn_ok.setText(ok);
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
		btn_cancle = (TextView) window.findViewById(R.id.tv_two_dialog_cancle);
		btn_cancle.setText(cancle);
		btn_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
				
			}
		});
		
        
	}
}
