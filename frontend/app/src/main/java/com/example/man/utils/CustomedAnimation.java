package com.example.man.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class CustomedAnimation {
    static public void fadeInAnimation(View view) {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(200); // 设置动画持续时间
        view.setVisibility(View.VISIBLE); // 将组件设置为可见
        view.startAnimation(fadeIn); // 开始淡入动画
    }

    static public void fadeOutAnimation(final View view) {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(50); // 设置动画持续时间
        fadeOut.setFillAfter(true); // 设置动画结束后保持最后状态
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE); // 将组件设置为隐藏
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(fadeOut); // 开始淡出动画
    }
}
