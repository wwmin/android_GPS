package com.wwm.gps.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wwm.gps.R;


/**
 * 箭头文字
 *
 * Created by Administrator on 2016/6/6.
 */
public class ArrowText extends RelativeLayout {

    private String greenTitle;
    private String mainTitle;
    private TextView tv_green;
    private TextView tv_main;
    private ImageView tv_pic;

    public ArrowText(Context context) {
        this(context,null);
    }

    public ArrowText(Context context, AttributeSet attrs) {
        this(context , attrs , 0);
    }

    public ArrowText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.customview_arrowtext,this,true);
        tv_main = (TextView) findViewById(R.id.custom_arrowtextview_tv_mian);
        tv_main.setText(mainTitle+"");
        tv_green = (TextView) findViewById(R.id.custom_arrowtextview_tv_green);
        tv_green.setText(greenTitle+"");
        tv_pic = (ImageView) findViewById(R.id.iv_pic_arrowtext);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs , R.styleable.arrowTextView , defStyleAttr , 0);
        int indexCount = a.getIndexCount();

        for (int i = 0; i < indexCount ; i++){
            int index = a.getIndex(i);
            switch (index){

                case R.styleable.arrowTextView_blackTitle:
                    String string = a.getString(index);
                    if (!string.isEmpty()) {
                        tv_main.setText(string);
                    }
                    break;

                case R.styleable.arrowTextView_grayTitle:
                    String string1 = a.getString(index);
                    if (!string1.isEmpty()) {
                        tv_green.setText(string1);
                    }else{
                        tv_green.setText("");
                    }
                    break;

                case R.styleable.arrowTextView_titleImage:
                    int resourceId = a.getResourceId(index, 0);
                    Log.i("zzz" , "--不填写--" + resourceId);

                    if (resourceId != 0) {
                        tv_pic.setVisibility(VISIBLE);
                        tv_pic.setBackgroundResource(resourceId);
                    }else{
                        tv_pic.setVisibility(GONE);
                    }
                    break;

            }
        }
        a.recycle();


    }

    //对外方法
    public void setGreenTitle(String greenTitle){
        tv_green.setText(greenTitle);
    }

    //对外方法
    public void setMainTitle(String mainTitle){
        tv_main.setText(mainTitle);
    }

    /**
     * 获取绿色数据
     */
    public String getGreenTitle(){
        return tv_green.getText().toString();
    }


}
