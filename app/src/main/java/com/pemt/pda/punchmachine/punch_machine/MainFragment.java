package com.pemt.pda.punchmachine.punch_machine;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.pemt.pda.punchmachine.punch_machine.adapter.MyFragmentPagerAdapter;
import com.pemt.pda.punchmachine.punch_machine.pages.ClockView;

import java.util.ArrayList;


/**
 * Created by dicky on 2016/11/9.
 */


public class MainFragment extends FragmentActivity {
    private static ClockView mClockView;
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentList;
    private ImageView image;
    private TextView view1, view2, view3, view4;
    private int currIndex;//当前页卡编号
    private int bmpW;//横线图片宽度
    private int offset;//图片移动的偏移量

    static public void stopClockRefresh() {
        mClockView.stopViewRefresh();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_tab);
        mClockView = (ClockView) findViewById(R.id.clock);
        InitTextView();
        InitImage();
        InitViewPager();
    }

    /*
     * 初始化标签名
     */
    public void InitTextView() {

        view1 = (TextView) findViewById(R.id.tv_guid1);
        view2 = (TextView) findViewById(R.id.tv_guid2);
        view1.setOnClickListener(new txListener(0));
        view2.setOnClickListener(new txListener(1));

    }

    /*
     * 初始化图片的位移像素
     */
    public void InitImage() {
        image = (ImageView) findViewById(R.id.iv_cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.main_fragment_cursor3).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
//        offset = (screenW / 3 - bmpW) / 2;
        offset = 0;

        //imgageview设置平移，使下划线平移到初始位置（平移一个offset）
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        image.setImageMatrix(matrix);
    }

    /*
     * 初始化ViewPager
     */
    public void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentList = new ArrayList<Fragment>();
        Fragment administratorFragment = new AdministratorFragment_();
        Fragment mainFragment = new MainActivity_();
        fragmentList.add(mainFragment);
        fragmentList.add(administratorFragment);

        //给ViewPager设置适配器
        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        mPager.addOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
        mPager.setCurrentItem(0);//设置当前显示标签页为第一页
        view1.setBackground(getResources().getDrawable(R.drawable.begin_record_pressed));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mClockView.startViewRefresh();
    }

    public class txListener implements View.OnClickListener {
        private int index = 0;

        public txListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            mPager.setCurrentItem(index);

        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private int one = offset * 2 + bmpW;//两个相邻页面的偏移量

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            Animation animation = new TranslateAnimation(currIndex * one, arg0 * one, 0, 0);//平移动画
            currIndex = arg0;
            animation.setFillAfter(true);//动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);//动画持续时间0.2秒
            image.startAnimation(animation);//是用ImageView来显示动画的
            switch (arg0) {
                case 0:
                    view1.setBackground(getResources().getDrawable(R.drawable.begin_record_pressed));
                    view2.setBackground(getResources().getDrawable(R.drawable.card_setting));
                    break;
                case 1:
                    view1.setBackground(getResources().getDrawable(R.drawable.begin_record3));
                    view2.setBackground(getResources().getDrawable(R.drawable.card_setting_pressed));
            }
            int i = currIndex + 1;
//            Toast.makeText(MainFragment.this, "您选择了第" + i + "个页卡", Toast.LENGTH_SHORT).show();
        }
    }
}