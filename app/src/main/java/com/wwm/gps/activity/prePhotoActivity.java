package com.wwm.gps.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wwm.gps.R;
import com.wwm.gps.constant.Constant;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by wwmin on 2017/5/29.
 */

public class prePhotoActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_back;
    private Button btn_album;
    private Button btn_take_photo;
    private TextView tv_photo_num;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int IMAGE_OPEN = 3;

    private final int IMAGE_NUM = 6;
    private List<String> imageList = new ArrayList<>();
    public List<PhotoInfo> mPhotoList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_image_layout);
        btn_back = (Button) this.findViewById(R.id.btn_back);
        btn_album = (Button) this.findViewById(R.id.btn_album);
        btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);
        tv_photo_num = (TextView) this.findViewById(R.id.tv_photo_num);
        btn_back.setOnClickListener(this);
        btn_album.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);
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
//                Intent intentAlbum=new Intent();
//                intentAlbum.setType("image/*");
//                intentAlbum.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intentAlbum,IMAGE_OPEN);
                GalleryFinal.openGalleryMuti(Constant.REQUEST_CODE_GALLERY, IMAGE_NUM, mOnHandlerResultCallback);
                break;
            case R.id.btn_take_photo:
                GalleryFinal.openCamera(Constant.REQUEST_CODE_CAMERA, mOnHandlerResultCallback);
                break;
            default:
                break;
        }
    }

    private GalleryFinal.OnHanlderResultCallback mOnHandlerResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int requestCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                mPhotoList.addAll(resultList);
                tv_photo_num.setText(mPhotoList.size() + " 张");
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
}
