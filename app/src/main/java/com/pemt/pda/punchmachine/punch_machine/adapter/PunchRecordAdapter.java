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
import java.util.List;

/**
 * Created by eng005 on 2016/11/23.
 */

public class PunchRecordAdapter extends BaseAdapter {
    public List<View> viewlist = new ArrayList<View>();
    Resources resources;
    private LayoutInflater mInflater = null;
    private ArrayList<AppData> mAppData;

    public PunchRecordAdapter(Context mContext, ArrayList<AppData> mAppData) {

        this.mAppData = mAppData;
        this.mInflater = LayoutInflater.from(mContext);
        resources = mContext.getResources();
    }

    @Override
    public int getCount() {
        return mAppData.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mAppData.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        若listview向下滚动，则在listview生成item的view的时候，将view都储存进一个集合
        if (position + 1 > viewlist.size()) {
            convertView = mInflater.inflate(R.layout.listview_item_punch, null);
            viewlist.add(convertView);
        }
        //若listview往回滚动，则将集合中存储好的view赋值给convertview。
        else {
            convertView = viewlist.get(position);
        }

        ViewHolder mHolder = new ViewHolder();
        mHolder.mStartFlag = (ImageView) convertView.findViewById(R.id.start_flag);
        mHolder.mEndFlag = (ImageView) convertView.findViewById(R.id.end_flag);
        mHolder.mRecordTime = (TextView) convertView.findViewById(R.id.tv_record_time);
        mHolder.mName = (TextView) convertView.findViewById(R.id.tv_name);
        mHolder.mJobDepartment = (TextView) convertView.findViewById(R.id.tv_job_department);
        mHolder.mRecordTime.setText(mAppData.get(position).getRECORD_TIME());
        mHolder.mName.setText(mAppData.get(position).getNAME());
        mHolder.mJobDepartment.setText(mAppData.get(position).getDEPARTMENT());
        return convertView;
    }

    private static class ViewHolder {
        TextView mName;
        TextView mRecordTime;
        TextView mJobDepartment;
        ImageView mStartFlag;
        ImageView mEndFlag;

    }
}
