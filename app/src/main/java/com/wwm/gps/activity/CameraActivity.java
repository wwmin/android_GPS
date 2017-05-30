package com.wwm.gps.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.maiml.wechatrecodervideolibrary.recoder.WechatRecoderActivity;
import com.wwm.gps.R;
import com.wwm.gps.bean.Item;
import com.wwm.gps.constant.Constant;
import com.wwm.gps.constant.UrlUtils;
import com.wwm.gps.dialog.SelectListDialog;
import com.wwm.gps.utils.PermissionUtil;
import com.wwm.gps.utils.SPUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by wwmin on 2017/5/28.
 */

public class CameraActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_qlsb_detiale_pic;
    private TextView tv_qlsb_detiale_video;
    private TextView tv_pre_photo;

    private final int IMAGE_NUM = 6;
    private List<String> imageList = new ArrayList<>();
    private String LOCAL_IMAGE_PATH = Environment.getExternalStorageDirectory() + "/road";
    public List<PhotoInfo> mPhotoList = new ArrayList<>();
    private String videoPath;
    private Button btn_qlsb_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canmera_layout);
        init();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.top_view_text);
        tv_title.setText(R.string.pic_video);
        iv_back = (ImageView) findViewById(R.id.top_view_back);
        iv_back.setVisibility(View.GONE);

        btn_qlsb_add = (Button) findViewById(R.id.btn_qlsb_add);
        btn_qlsb_add.setOnClickListener(this);
        tv_qlsb_detiale_pic = (TextView) findViewById(R.id.tv_qlsb_detiale_pic);
        tv_qlsb_detiale_pic.setOnClickListener(this);
        tv_pre_photo = (TextView) findViewById(R.id.tv_pre_photo);
        tv_pre_photo.setOnClickListener(this);
        tv_qlsb_detiale_video = (TextView) findViewById(R.id.tv_qlsb_detiale_video);
        tv_qlsb_detiale_video.setOnClickListener(this);
        tv_qlsb_detiale_video.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WechatRecoderActivity.launchActivity(CameraActivity.this, 1001);
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_qlsb_detiale_pic: //选择图片
                showCheckDialog();
                break;
            case R.id.tv_pre_photo:
                Intent intent = new Intent(CameraActivity.this, prePhotoActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_qlsb_detiale_video: //拍摄视频
                if (videoPath != null && !videoPath.isEmpty()) {
                    play(videoPath);
                    Toast.makeText(this, "开始播放视频", Toast.LENGTH_SHORT).show();
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                            WechatRecoderActivity.launchActivity(CameraActivity.this, 1001);
                        } else {
                            PermissionUtil permissionUtil = new PermissionUtil();
                            permissionUtil.getVideoPermiss(this);
                        }
                    } else {
                        WechatRecoderActivity.launchActivity(CameraActivity.this, 1001);
                    }
                }
                break;
            case R.id.btn_qlsb_add:
//                badType = et_qlsb_badtype.getText().toString();
//                badRange = et_qlsb_badrange.getText().toString();
//                idea = et_qlsb_idea.getText().toString();
//                if (bridgePartNameId.isEmpty()) {
//                    Toast.makeText(QLSBActivity.this, "请选择部件", Toast.LENGTH_SHORT).show();
//                } else if (badType.isEmpty()) {
//                    Toast.makeText(QLSBActivity.this, "缺损类型不能为空", Toast.LENGTH_SHORT).show();
//                } else if (badRange.isEmpty()) {
//                    Toast.makeText(QLSBActivity.this, "缺损范围不能为空", Toast.LENGTH_SHORT).show();
//                } else if (idea.isEmpty()) {
//                    Toast.makeText(QLSBActivity.this, "保养措施意见不能为空", Toast.LENGTH_SHORT).show();
//                } else if (imageList == null || imageList.size() == 0) {
//                    Toast.makeText(QLSBActivity.this, "图片不能为空", Toast.LENGTH_SHORT).show();
//                }  else {
////                    btn_qlsb_add.setClickable(false);
////                    btn_qlsb_add.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_circle_blue_light));
////                    httpAddBridgeInfo();
//                btn_qlsb_add.setClickable(false);
//                btn_qlsb_add.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_circle_blue_light));
                loadingDialog.show();
                //上传视频
                if (videoPath != null && !videoPath.isEmpty()) {
                    File file = new File(videoPath);
                    uploadFile(file, UrlUtils.UPLOAD_VIDEO);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
//            httpSearchBridge();
        } else {
            if (RESULT_OK == resultCode) {
                if (requestCode == 1001) {
                    videoPath = data.getStringExtra(WechatRecoderActivity.VIDEO_PATH);
                    tv_qlsb_detiale_video.setText("点击播放，长按重拍");
                }
            }
        }
    }

    private void play(String videoPath) {
        startActivity(new Intent(this, PlayActivity.class).putExtra("path", videoPath));
    }

    private void showCheckDialog() {
        List<Item> menuList = new ArrayList<>();
        Item item1 = new Item();
        item1.setTitle("拍照");
        menuList.add(item1);
        Item item2 = new Item();
        item2.setTitle("从图库中选择");
        menuList.add(item2);
        final SelectListDialog selectListDialog = new SelectListDialog();
        selectListDialog.showdialog(CameraActivity.this, "", menuList);
        selectListDialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    GalleryFinal.openCamera(Constant.REQUEST_CODE_CAMERA, mOnHandlerResultCallback);
                } else if (position == 1) {
                    GalleryFinal.openGalleryMuti(Constant.REQUEST_CODE_GALLERY, IMAGE_NUM, mOnHandlerResultCallback);
                }
                selectListDialog.getDialog().dismiss();
            }
        });

    }

    private GalleryFinal.OnHanlderResultCallback mOnHandlerResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int requestCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                mPhotoList.addAll(resultList);
                tv_qlsb_detiale_pic.setText(mPhotoList.size() + "张");
                imageList.clear();
                for (int i = 0; i < mPhotoList.size(); i++) {
                    imageList.add(mPhotoList.get(i).getPhotoPath());
                }
//                lksb.setImages(imageList);
//                mChoosePhotoListAdapter.notifyDataSetChanged();
//                String path = resultList.get(0).getPhotoPath();
//                Glide.with(LKSBAddActivity.this).load("file://" + path).into(sdv_deadimg_mine);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(CameraActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 上传文件到服务器
     *
     * @param file       需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public void uploadFile(final File file, final String RequestURL) {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                final String TAG = "uploadFile";
                Log.e(TAG, "正在上传");
                final int TIME_OUT = 100 * 1000; // 超时时间
                final String CHARSET = "utf-8"; // 设置编码
                int res = 0;
                String result = null;
                String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
                String PREFIX = "--", LINE_END = "\r\n";
                String CONTENT_TYPE = "multipart/form-data"; // 内容类型

                try {
                    URL url = new URL(RequestURL);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setReadTimeout(TIME_OUT);
                    conn.setConnectTimeout(TIME_OUT);
                    conn.setDoInput(true); // 允许输入流
                    conn.setDoOutput(true); // 允许输出流
                    conn.setUseCaches(false); // 不允许使用缓存
                    conn.setRequestMethod("POST"); // 请求方式
                    conn.setRequestProperty("Charset", CHARSET); // 设置编码
                    conn.setRequestProperty("connection", "keep-alive");
                    conn.setRequestProperty("Content-Type", CONTENT_TYPE
                            + ";boundary=" + BOUNDARY);

                    if (file != null) {
                        /**
                         * 当文件不为空时执行上传
                         */
                        DataOutputStream dos = new DataOutputStream(
                                conn.getOutputStream());
                        StringBuffer sb = new StringBuffer();
                        sb.append(PREFIX);
                        sb.append(BOUNDARY);
                        sb.append(LINE_END);
                        /**
                         * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                         * filename是文件的名字，包含后缀名
                         */

                        sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                                + file.getName() + "\"" + LINE_END);
                        sb.append("Content-Type: image/jpeg; charset="
                                + CHARSET + LINE_END);
                        sb.append(LINE_END);
                        dos.write(sb.toString().getBytes());
                        InputStream is = new FileInputStream(file);
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        while ((len = is.read(bytes)) != -1) {
                            dos.write(bytes, 0, len);
                        }
                        is.close();
                        dos.write(LINE_END.getBytes());
                        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                                .getBytes();
                        dos.write(end_data);
                        dos.flush();
                        /**
                         * 获取响应码 200=成功 当响应成功，获取响应的流
                         */
                        res = conn.getResponseCode();
                        Log.e(TAG, "response code:" + res);
                        if (res == 200) {
                            Log.e(TAG, "request success");
                            InputStream input = conn.getInputStream();
                            StringBuffer sb1 = new StringBuffer();
                            int ss;
                            while ((ss = input.read()) != -1) {
                                sb1.append((char) ss);
                            }
                            result = sb1.toString();
                            JSONObject jsonobject = new JSONObject(result);
                            Log.e(TAG, "result : " + result);
                            //{"result":true,"error":"","data":[{"Success":true,"Msg":"æä½æå","Code":200,"Data":"/Upload/Vedio/2047936DF2EF45F191C112C9FCA7992E.mp4"}]}

//                            JSONArray dataArr = jsonobject.getJSONArray("data");
//                            String path = "";
//                            if (dataArr != null && dataArr.length() > 0) {
//                                path = dataArr.getJSONObject(0).getString("Data");
//                            }
                            //上报
                            String path = jsonobject.getString("data");
                            Message msg = new Message();
                            msg.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putString("path", path);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        } else {
                            Log.e(TAG, "request error");
                            loadingDialog.dismiss();
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    loadingDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                    loadingDialog.dismiss();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    loadingDialog.dismiss();
                }
            }

        }.start();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {

                String path = msg.getData().getString("path");
//                httpAddBridgeInfo(path);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String number = intent.getStringExtra("Number");
        if (number == null) {
            number = "0";
        }
        String[] imagePaths=SPUtil.getData(CameraActivity.this, Constant.IMAGE_LIST, "").toString().split("\\|");
        int len=imagePaths.length;
        int i=0;
        int imageNum=0;
        while (i<len){
            if(!imagePaths[i].isEmpty()){
                imageNum++;
            }
            i++;
        }
        tv_pre_photo.setText(imageNum+" 张");
    }
}
