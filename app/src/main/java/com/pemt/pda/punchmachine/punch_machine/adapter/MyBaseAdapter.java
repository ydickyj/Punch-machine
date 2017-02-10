package com.pemt.pda.punchmachine.punch_machine.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pemt.pda.punchmachine.punch_machine.R;
import com.pemt.pda.punchmachine.punch_machine.db.bean.AppData;

import java.util.ArrayList;
import java.util.Random;


/**
 * copyright© www.pemt.com.cn
 * Created by eng005 on 2016/7/15
 */
public class MyBaseAdapter extends BaseAdapter {

    Resources resources;
    Context mC;
    private LayoutInflater mInflater = null;
    private ArrayList<AppData> mScanAppInfos;

    public MyBaseAdapter(Context context, ArrayList<AppData> mScanAppInfos) {
        this.mInflater = LayoutInflater.from(context);
        this.mScanAppInfos = mScanAppInfos;
        resources = context.getResources();
        mC = context;
    }

    @Override
    public int getCount() {
        return mScanAppInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mScanAppInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder = new ViewHolder();
//        if (convertView == null) {
            //由于程序锁的条目与病毒扫描内容基本一致，因此重用程序锁的布局
            convertView = mInflater.inflate(R.layout.listview_item, null);
        mHolder.mHeadPortrait = (ImageView) convertView.findViewById(R.id.head_portrait);
            mHolder.mAppNameTV = (TextView) convertView.findViewById(R.id.tv_content);
            mHolder.mRecordTime = (TextView) convertView.findViewById(R.id.tv_time);
        mHolder.mTvJob = (TextView) convertView.findViewById(R.id.tv_position);
        mHolder.mTvWhere = (TextView) convertView.findViewById(R.id.tv_where);

//        } else {
//            mHolder = (ViewHolder) convertView.getTag();
//        }

        AppData scanAppInfo = mScanAppInfos.get(position);
        mHolder.mAppNameTV.setText(scanAppInfo.getNAME());
        mHolder.mRecordTime.setText(scanAppInfo.getRECORD_TIME());
        mHolder.mTvJob.setText(scanAppInfo.getJOB());
        mHolder.mTvWhere.setText(scanAppInfo.getOFFICE_LOCATION());
        int a = new Random().nextInt(2);
        if (scanAppInfo.getNAME().indexOf("小姐") > 0) {
            a = a + 3;
        }
        Log.e("123123", a + "");
        switch (a) {
            case 0:
                mHolder.mHeadPortrait.setBackground(ContextCompat.getDrawable(mC, R.drawable.boy));
                break;
            case 1:
                mHolder.mHeadPortrait.setBackground(ContextCompat.getDrawable(mC, R.drawable.boy1));
                break;
            case 2:
                mHolder.mHeadPortrait.setBackground(ContextCompat.getDrawable(mC, R.drawable.boy2));
                break;
            case 3:
                mHolder.mHeadPortrait.setBackground(ContextCompat.getDrawable(mC, R.drawable.girl));
                break;
            case 4:
                mHolder.mHeadPortrait.setBackground(ContextCompat.getDrawable(mC, R.drawable.girl2));
                break;
            case 5:
                mHolder.mHeadPortrait.setBackground(ContextCompat.getDrawable(mC, R.drawable.girl3));
                break;
        }

        convertView.setTag(mHolder);
        return convertView;
    }

    private static class ViewHolder {
        TextView mAppNameTV;
        TextView mRecordTime;
        TextView mTvJob;
        TextView mTvWhere;
        ImageView mHeadPortrait;
    }
}
