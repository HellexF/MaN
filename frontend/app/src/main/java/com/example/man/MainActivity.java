package com.example.man;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.man.utils.SharedPreferencesManager;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Banner banner;
    private Button mobileButton;
    private Button emailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置登录按钮的ICON
        mobileButton = findViewById(R.id.mobile_button);
        Drawable icon = getResources().getDrawable(R.drawable.mobile_icon);
        icon.setBounds(0, 0, 60, 60);
        mobileButton.setCompoundDrawables(icon, null, null, null);

        emailButton = findViewById(R.id.email_button);
        icon = getResources().getDrawable(R.drawable.email_icon);
        icon.setBounds(0, 0, 60, 60);
        emailButton.setCompoundDrawables(icon, null, null, null);

        // 设置轮播组件
        banner = findViewById(R.id.banner);
        List<Integer> imageResIds = new ArrayList<>();
        imageResIds.add(R.drawable.memo_description);
        imageResIds.add(R.drawable.note_description);
        imageResIds.add(R.drawable.notification_description);

        banner.setAdapter(new BannerImageAdapter<Integer>(imageResIds) {
            @Override
            public void onBindView(BannerImageHolder holder, Integer data, int position, int size) {
                holder.imageView.setImageResource(data);
                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
        }).addBannerLifecycleObserver(this)
                .setIndicator(new CircleIndicator(this));

        banner.setIndicatorNormalColorRes(R.color.button_border_grey);
        banner.setIndicatorSelectedColorRes(R.color.yellow);

        mobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(MainActivity.this, MobileLoginActivity.class);
                Intent intent = new Intent(MainActivity.this, NoteContentActivity.class);
                startActivity(intent);
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EmailLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}