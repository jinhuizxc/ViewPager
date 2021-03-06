package com.example.smily.viewpager;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ViewPager mViewPager;
    private List<ImageView> mImageViewList;
    private int[] images = {R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5};
    private int currentPosition = 1;
    private int dotPosition = 0;
    private int prePosition = 0;
    private LinearLayout mLinearLayoutDot;
    private List<ImageView> mImageViewDotList;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mViewPager.setCurrentItem(currentPosition, false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();

        setDot();

        setViewPager();

        autoPlay();
    }


    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.vp_main);
        mLinearLayoutDot = (LinearLayout) findViewById(R.id.ll_main_dot);
    }


    private void initData() {
        mImageViewList = new ArrayList<>();
        mImageViewDotList = new ArrayList<>();
        ImageView imageView;
        // i = 0,1,2,3,4,5,6
        for (int i = 0; i < images.length + 2; i++) {
            if (i == 0) {   //判断当i=0为该处的ImageView设置最后一张图片作为背景
                imageView = new ImageView(this);
                imageView.setBackgroundResource(images[images.length - 1]);
                mImageViewList.add(imageView);
            } else if (i == images.length + 1) {   //判断当i=images.length+1时为该处的ImageView设置第一张图片作为背景
                imageView = new ImageView(this);
                imageView.setBackgroundResource(images[0]);
                mImageViewList.add(imageView);
            } else {  //其他情况则为ImageView设置images[i-1]的图片作为背景
                imageView = new ImageView(this);
                imageView.setBackgroundResource(images[i - 1]);
                mImageViewList.add(imageView);
            }
        }
    }

    //  设置轮播小圆点
    private void setDot() {
        //  设置LinearLayout的子控件的宽高，这里单位是像素。
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15, 15);
        params.rightMargin = 20;
        //  for循环创建images.length个ImageView（小圆点）
        for (int i = 0; i < images.length; i++) {
            ImageView imageViewDot = new ImageView(this);
            imageViewDot.setLayoutParams(params);
            //  设置小圆点的背景为暗红图片
            imageViewDot.setBackgroundResource(R.drawable.red_dot_night);
            mLinearLayoutDot.addView(imageViewDot);
            mImageViewDotList.add(imageViewDot);
        }
        //设置第一个小圆点图片背景为红色
        mImageViewDotList.get(dotPosition).setBackgroundResource(R.drawable.red_dot);
    }

    private void setViewPager() {
        MyPagerAdapter adapter = new MyPagerAdapter(mImageViewList);

        mViewPager.setAdapter(adapter);

        mViewPager.setCurrentItem(currentPosition);
        //页面改变监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {    //判断当切换到第0个页面时把currentPosition设置为images.length,即倒数第二个位置，小圆点位置为length-1
                    currentPosition = images.length;
                    dotPosition = images.length - 1;
                    Log.e(TAG, "currentPosition 0= " + position);
                } else if (position == images.length + 1) {    //当切换到最后一个页面时currentPosition设置为第一个位置，小圆点位置为0
                    currentPosition = 1;
                    dotPosition = 0;
                    Log.e(TAG, "currentPosition 1= " + position);
                } else {
                    currentPosition = position;
                    dotPosition = position - 1;
                    Log.e(TAG, "currentPosition 2= " + position);
                }
                //  把之前的小圆点设置背景为暗红，当前小圆点设置为红色
                mImageViewDotList.get(prePosition).setBackgroundResource(R.drawable.red_dot_night);
                mImageViewDotList.get(dotPosition).setBackgroundResource(R.drawable.red_dot);
                prePosition = dotPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //当state为SCROLL_STATE_IDLE即没有滑动的状态时切换页面
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    // setCurrentItem 调用onPageSelected方法
                    mViewPager.setCurrentItem(currentPosition, false);
                }
            }
        });
    }

    //  设置自动播放
    private void autoPlay() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                while (true) {
                    SystemClock.sleep(3000);
                    currentPosition++;
                    handler.sendEmptyMessage(1);
                }
            }
        }.start();
    }
}
