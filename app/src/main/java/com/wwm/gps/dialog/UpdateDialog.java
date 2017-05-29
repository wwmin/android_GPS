package com.wwm.gps.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.wwm.gps.R;
import com.wwm.gps.constant.Constant;
import com.wwm.gps.constant.UrlUtils;
import com.wwm.gps.service.DownloadService;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateDialog {

	private TextView tvCancle;
	private TextView tvOk;
	private TextView tvTitle;
	private TextView tvContent;
	private AlertDialog dialog;
	private String downloadUrl;
	
	public TextView getBtnCancle(){
		return tvCancle;
	}
	public TextView getBtnOk(){
		return tvOk;
	}
	
	public AlertDialog getDialog(){
		return dialog;
	}

	public void checkUpdate(final Context context, final String fromFlag){
		FinalHttp mHttp = new FinalHttp();
		mHttp.configCharset(Constant.CHARSET);
		AjaxParams params = new AjaxParams();
		String url = mHttp.getUrlWithQueryString(UrlUtils.GET_VERSION,params);

		mHttp.post(url, new AjaxCallBack<String>() {
			public void onSuccess(String content) {
				Log.i("GET_VERSION", content);
				try {
					JSONObject json = new JSONObject(content);
					boolean result = json.getBoolean("result");
					if (result) {
						JSONObject data = json.getJSONObject("data");
						String newVersionCode = data.getString("VersionCode");
						String versionDesc = data.getString("VersionMemo");
						downloadUrl = UrlUtils.URL + data.getString("UploadURL");
						boolean isTrue = data.getBoolean("IsMustUpload");
						String isDown = "1";
						if (!isTrue) {
							isDown = "0";
						}

						int versionCode = 9999;
						try {
							PackageInfo packageInfo=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
							versionCode = packageInfo.versionCode;
						} catch (NameNotFoundException e) {
							e.printStackTrace();
						}
						if (Integer.valueOf(newVersionCode) > versionCode ) {
							showdialog(context, versionDesc, "退出", "更新", isDown);
						} else if("setting".equals(fromFlag)){
							Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show();
						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			public void onFailure(Throwable t, int errorNo, String strMsg) {
//				Toast.makeText(context, "updateDialog:网络错误", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void showdialog(final Context context,String title,String cacle,String ok,String isDown){

		dialog = new AlertDialog.Builder(context).create();
		dialog.show();
		dialog.setCancelable(false);
		Window window = dialog.getWindow();
		window.setContentView(R.layout.dialog_two_btn);
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.CENTER);
		tvTitle = (TextView) window.findViewById(R.id.tv_two_dialog_title);
		tvTitle.setText("版本更新");
		tvContent = (TextView) window.findViewById(R.id.tv_two_dialog_content);
		tvContent.setText(title);
		tvCancle = (TextView) window.findViewById(R.id.tv_two_dialog_cancle);
		if (isDown.equals("0")) {
			tvCancle.setText(cacle);
			tvCancle.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.cancel();
					
				}
			});
		}else{
			tvCancle.setText("退出");
			tvCancle.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					System.exit(0);
					
				}
			});
		}
		
		
		tvOk = (TextView) window.findViewById(R.id.tv_two_dialog_ok);
		tvOk.setText(ok);
		tvOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
					Intent intent = new Intent(context, DownloadService.class);
					intent.putExtra(DownloadService.INTENT_URL, downloadUrl);
					context.startService(intent);

				} else {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
					context.startActivity(intent);
				}
			}
		});
		
        
	}
}
