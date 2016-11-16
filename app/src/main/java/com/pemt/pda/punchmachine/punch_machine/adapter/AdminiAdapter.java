package com.pemt.pda.punchmachine.punch_machine.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pemt.pda.punchmachine.punch_machine.R;
import com.pemt.pda.punchmachine.punch_machine.db.bean.AppData;

import java.util.ArrayList;


/**
 * copyright© www.pemt.com.cn
 * Created by eng005 on 2016/7/15
 */
public class AdminiAdapter extends BaseAdapter {

    Resources resources;
    private LayoutInflater mInflater = null;
    private ArrayList<AppData> mScanAppInfos;

    public AdminiAdapter(Context context, ArrayList<AppData> mScanAppInfos) {
        this.mInflater = LayoutInflater.from(context);
        this.mScanAppInfos = mScanAppInfos;
        resources = context.getResources();
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
        if (convertView == null) {
            //由于程序锁的条目与病毒扫描内容基本一致，因此重用程序锁的布局
            convertView = mInflater.inflate(R.layout.listview_item, null);
            mHolder.mHeadPortrait = (ImageView) convertView.findViewById(R.id.head_portrait);
            mHolder.mAppNameTV = (TextView) convertView.findViewById(R.id.tv_content);
            mHolder.mRecordTime = (TextView) convertView.findViewById(R.id.tv_time);
            mHolder.mTvJob = (TextView) convertView.findViewById(R.id.tv_position);
            mHolder.mTvWhere = (TextView) convertView.findViewById(R.id.tv_where);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
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
