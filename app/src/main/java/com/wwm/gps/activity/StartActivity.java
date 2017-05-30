package com.wwm.gps.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.wwm.gps.R;
import com.wwm.gps.bean.UserInfos;
import com.wwm.gps.utils.MySetting;

/**
 * Created by wwmin on 2017/5/23.
 */

public class StartActivity extends Activity {
    private UserInfos userInfos = new UserInfos();
    private ImageView welcomeImg = null;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
        welcomeImg = (ImageView) this.findViewById(R.id.welcome_img);
        AlphaAnimation anima=new AlphaAnimation(0.9f,1.0f);
        anima.setDuration(600);//设置动画时间
        welcomeImg.startAnimation(anima);
        anima.setAnimationListener(new AnimationImpl());
    }

    private class AnimationImpl implements Animation.AnimationListener{
        @Override
        public void onAnimationStart(Animation animation){
            welcomeImg.setBackgroundResource(R.drawable.welcome);
        }

        @Override
        public void onAnimationEnd(Animation animation){
            skip();// 动画结束后跳转到别的页面
        }

        @Override
        public void onAnimationRepeat(Animation animation){

        }
    }
    private void skip(){
        userInfos = MySetting.getSaveLogin(this);
        if (userInfos != null && userInfos.infos.size() > 0) {
            /*此时的登录用户就是上次登录成功并记住密码的  可以跳转到主页*/
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
}
