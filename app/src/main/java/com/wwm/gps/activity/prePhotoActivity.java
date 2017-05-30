package com.wwm.gps.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wwm.gps.R;
import com.wwm.gps.constant.Constant;
import com.wwm.gps.dialog.TwoBtnDialog;
import com.wwm.gps.utils.SPUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

import static com.wwm.gps.utils.Common.listToString;


/**
 * Created by wwmin on 2017/5/29.
 */

public class prePhotoActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_back;
    private Button btn_album;
    private Button btn_take_photo;
    private Button btn_clear_all;
    private TextView tv_photo_num;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int IMAGE_OPEN = 3;

    private final int IMAGE_NUM = 6;
    private List<String> imageList = new ArrayList<>();
    public List<PhotoInfo> mPhotoList = new ArrayList<>();
    //    private String pathImage;                //选择图片路径
    private ArrayList<HashMap<String, Object>> imageItem;
    private SimpleAdapter simpleAdapter; // 适配器

    private GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /*
         * 防止键盘挡住输入框
         * 不希望遮挡设置activity属性 android:windowSoftInputMode="adjustPan"
         * 希望动态调整高度 android:windowSoftInputMode="adjustResize"
         */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //锁定屏幕方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.photo_image_layout);
        btn_back = (Button) this.findViewById(R.id.btn_back);
        btn_album = (Button) this.findViewById(R.id.btn_album);
        btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);
        btn_clear_all = (Button) this.findViewById(R.id.btn_clear_all);
        tv_photo_num = (TextView) this.findViewById(R.id.tv_photo_num);
        gridView = (GridView) this.findViewById(R.id.gv_add_img);
        btn_back.setOnClickListener(this);
        btn_album.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);
        btn_clear_all.setOnClickListener(this);
        imageItem = new ArrayList<HashMap<String, Object>>();

        String[] imagePaths = SPUtil.getData(prePhotoActivity.this, Constant.IMAGE_LIST, "").toString().split("\\|");
        int len = imagePaths.length;
        int i = 0;
        int imageNum = 0;
        while (i < len) {
            if (!imagePaths[i].isEmpty()) {
                imageNum++;
                showImage(imagePaths[i]);
            }
            i++;
        }
        tv_photo_num.setText(imageNum + " 张");

        initGridViewEvent();
    }

    private void initGridViewEvent() {
          /*
         * 监听GridView点击事件
         */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            }
        });
        /*
        * 长按点击事件
        * */
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
//                final TwoBtnDialog btnDialog = new TwoBtnDialog();
//                final int pos=position;
//                btnDialog.showdialog(prePhotoActivity.this, "确定删除该图片吗?", "确定", "取消");
//                btnDialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        imageItem.remove(pos);
//                        mPhotoList.remove(pos);
//                        tv_photo_num.setText(imageItem.size()+" 张");
//                        Log.i("IMAGE_ITEM______:",imageItem.toString());
//                        String il=listToString(imageItem,'|');
//                        SPUtil.saveData(prePhotoActivity.this,Constant.IMAGE_LIST,il);
//                        simpleAdapter.notifyDataSetChanged();
//                        btnDialog.getDialog().cancel();
//                    }
//                });
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                Intent intentBack = new Intent(prePhotoActivity.this, CameraActivity.class);
                intentBack.putExtra("Number", String.valueOf(mPhotoList.size()));
                startActivity(intentBack);
                break;
            case R.id.btn_album:
                GalleryFinal.openGalleryMuti(Constant.REQUEST_CODE_GALLERY, IMAGE_NUM, mOnHandlerResultCallback);
                ///*选择图片的最基本方式*/
                //Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(intent, IMAGE_OPEN);
                break;
            case R.id.btn_take_photo:
                GalleryFinal.openCamera(Constant.REQUEST_CODE_CAMERA, mOnHandlerResultCallback);
                break;
            case R.id.btn_clear_all:
                if (gridView.getChildCount() > 0) {
                    final TwoBtnDialog btnDialog = new TwoBtnDialog();
                    btnDialog.showdialog(this, "确定清空已添加图片吗?", "确定", "取消");
                    btnDialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv_photo_num.setText("0 张");
                            imageItem.clear();
                            mPhotoList.clear();
                            SPUtil.saveData(prePhotoActivity.this, Constant.IMAGE_LIST, "");
                            simpleAdapter.notifyDataSetChanged();
                            btnDialog.getDialog().cancel();
                        }
                    });
                    break;
                } else {
                    Toast.makeText(prePhotoActivity.this, "已清空",
                            Toast.LENGTH_LONG).show();
                }
            default:
                break;
        }
    }

    private GalleryFinal.OnHanlderResultCallback mOnHandlerResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int requestCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                mPhotoList.clear();
                imageList.clear();
                mPhotoList.addAll(resultList);
                for (int i = 0; i < mPhotoList.size(); i++) {
                    imageList.add(mPhotoList.get(i).getPhotoPath());
                }
                for (int j = 0; j < resultList.size(); j++) {
                    showImage(resultList.get(j).getPhotoPath());
                }
                String il = listToString(imageList, '|');
                Log.i("图片路径:", il);

                String ips = SPUtil.getData(prePhotoActivity.this, Constant.IMAGE_LIST, "").toString();
                String[] imagePaths = ips.split("\\|");
                int len = imagePaths.length;
                int i = 0;
                int imageNum = 0;
                while (i < len) {
                    if (!imagePaths[i].isEmpty()) {
                        imageNum++;
                    }
                    i++;
                }
                tv_photo_num.setText(imageNum + mPhotoList.size() + " 张");
                SPUtil.saveData(prePhotoActivity.this, Constant.IMAGE_LIST, ips + '|' + il);
//                lksb.setImages(imageList);
//                mChoosePhotoListAdapter.notifyDataSetChanged();
//                String path = resultList.get(0).getPhotoPath();
//                Glide.with(LKSBAddActivity.this).load("file://" + path).into(sdv_deadimg_mine);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(prePhotoActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_OPEN && resultCode == RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
//                Cursor cursor = getContentResolver().query(uri, null, null,
//                        null, null);
//                cursor.moveToFirst();

//                String imageFilePath = cursor.getString(1);
//                System.out.println("File path is----->" + imageFilePath);
//                filepath = imageFilePath;
//                if(BhsbDb.picnames==null)
//                    BhsbDb.picnames = new ArrayList<String>();

//                BhsbDb.picnames.add(filepath);

//                FileInputStream fis = new FileInputStream(imageFilePath);
//                BitmapFactory.Options options=new BitmapFactory.Options();
//                options.inJustDecodeBounds = false;
//                options.inSampleSize = 10;
//
//                Bitmap bitmap = BitmapFactory.decodeStream(fis,null,options);
//
//                int width = bitmap.getWidth();
//                int height = bitmap.getHeight();
//                System.out.println("压缩前的宽高----> width: " + width + " height:"
//                        + height);

				/* 压缩获取的图像 */
//                showImgs(bitmap, false);
//                fis.close();
//                cursor.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        } else if (requestCode == TAKE_PHOTO
//                && resultCode == RESULT_OK && data != null) {
//            cameraCamera(data);
            } catch (Exception e) {
                Log.w("拾取照片错误:", e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showImage(String pathImage) {
        if (!TextUtils.isEmpty(pathImage)) {
            Bitmap addbmp = BitmapFactory.decodeFile(pathImage);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", addbmp);
            imageItem.add(map);
            simpleAdapter = new SimpleAdapter(this,
                    imageItem, R.layout.griditem_addpic,
                    new String[]{"itemImage"}, new int[]{R.id.img_add});
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    // TODO Auto-generated method stub
                    if (view instanceof ImageView && data instanceof Bitmap) {
                        ImageView i = (ImageView) view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            gridView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
            //刷新后释放防止手机休眠后自动添加
            pathImage = null;
        }
    }
}
