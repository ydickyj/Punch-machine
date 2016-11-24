package com.pemt.pda.punchmachine.punch_machine;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.pemt.pda.punchmachine.punch_machine.adapter.PunchRecordAdapter;
import com.pemt.pda.punchmachine.punch_machine.db.PDASqliteOpenHelper;
import com.pemt.pda.punchmachine.punch_machine.db.bean.AppData;
import com.pemt.pda.punchmachine.punch_machine.pages.BlueDialog;
import com.pemt.pda.punchmachine.punch_machine.pages.KCalendar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 * Created by eng005 on 2016/11/21.
 */

@EActivity(R.layout.activity_punch_record)
public class PunchRecord extends Activity {
    private static Logger logger = LoggerFactory.getLogger(PunchRecord_.class);
    @ViewById
    TextView tvEdit;
    @ViewById
    ImageView ivBack;
    @ViewById
    TextView tvTitle;
    @ViewById
    ListView lvContext;
    @ViewById
    Button btnExport;
    String date = null;// 设置默认选中的日期  格式为 “2016-11-22” 标准DATE格式
    Button bt;
    PDASqliteOpenHelper sqLiteOpenHelper = PdaApplication.getSqliteOpenHelper();
    private ArrayList<AppData> mAppList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void afterView() {
        tvTitle.setText("打卡记录");
        tvEdit.setVisibility(View.GONE);
        bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new PopupWindows(PunchRecord.this, bt);
            }
        });
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        date = sDateFormat.format(new java.util.Date());
        String btnDate = "日期：" + date;
        bt.setText(btnDate);
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout titleView = (LinearLayout) inflater.inflate(R.layout.listview_title_head, null);//得到头部的布局
        LinearLayout footView = (LinearLayout) inflater.inflate(R.layout.listview_title_foot, null);//得到尾部的布局
        lvContext.addHeaderView(titleView);
        lvContext.addFooterView(footView);
        lvContext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (id <= -1) {
                    // 点击的是headerView或者footerView
                    Toast.makeText(PunchRecord.this, "点击的是headerView或者footerView", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(PunchRecord.this, "position:" + position, Toast.LENGTH_SHORT).show();
            }
        });
        updateDate();
    }

    @Click
    void ivBack() {
        onBackPressed();
    }

    @UiThread
    void updateDate() {
        String strDate = "日期：" + date;
        bt.setText(strDate);
        databaseQuery();
    }

    @Background
    void databaseQuery() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date newDate = sDateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newDate);
            Dao newDao = sqLiteOpenHelper.getDao(AppData.class, calendar);
            mAppList = (ArrayList<AppData>) newDao.queryBuilder().orderBy("ID", true).where().like("RECORD_TIME", "%" + date + "%").query();
            logger.error("数据长度：{}", mAppList.size());
            PunchRecordAdapter mAdapter = new PunchRecordAdapter(this, mAppList);
            loadingData(mAdapter);
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }
    }

    @UiThread
    void loadingData(PunchRecordAdapter adapter) {
        lvContext.setDivider(null);
        lvContext.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Click
    void btnExport() {
        final Random random = new Random(System.currentTimeMillis());
        final Context mContext = this.getBaseContext();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View layout = layoutInflater.inflate(R.layout.export_record_dialog, null);
        BlueDialog.Builder builder = new BlueDialog.Builder(this).setContentView(layout);
        builder.setTitle("导出打卡记录");
        builder.setPositiveButton("导出本日", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LayoutInflater mLayoutInflater = LayoutInflater.from(PunchRecord.this);
                        View mLayout = mLayoutInflater.inflate(R.layout.export_loading_dialog, null);
                        SpringProgressView mSpv = (SpringProgressView) mLayout.findViewById(R.id.spv);
                        BlueDialog.Builder mBuilder = new BlueDialog.Builder(PunchRecord.this).setContentView(mLayout);
                        mBuilder.setTitle("导出记录中");
                        mBuilder.create().show();
                        mSpv.setMaxCount(50000.0f);
                        for (int i = 0; i < 5; i++) {
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mSpv.setCurrentCount(i * 5000);
                        }


                    }
                    //设置你的操作事项
                }
        );
        builder.setNegativeButton("导出本月",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }


    public class PopupWindows extends PopupWindow {

        public PopupWindows(final Context mContext, View parent) {

            final View view = View.inflate(mContext, R.layout.popupwindow_calendar,
                    null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_in));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
//            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
//                    R.anim.push_bottom_in_1));

            setWidth(LinearLayout.LayoutParams.FILL_PARENT);
            setHeight(LinearLayout.LayoutParams.FILL_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            final TextView popupwindow_calendar_month = (TextView) view
                    .findViewById(R.id.popupwindow_calendar_month);
            final KCalendar calendar = (KCalendar) view
                    .findViewById(R.id.popupwindow_calendar);
            Button popupwindow_calendar_bt_enter = (Button) view
                    .findViewById(R.id.popupwindow_calendar_bt_enter);

            popupwindow_calendar_month.setText(calendar.getCalendarYear() + "年"
                    + calendar.getCalendarMonth() + "月");

            if (null != date) {

                int years = Integer.parseInt(date.substring(0,
                        date.indexOf("-")));
                int month = Integer.parseInt(date.substring(
                        date.indexOf("-") + 1, date.lastIndexOf("-")));
                popupwindow_calendar_month.setText(years + "年" + month + "月");

                calendar.showCalendar(years, month);
                calendar.setCalendarDayBgColor(date,
                        R.drawable.calendar_date_focused);
            }

            List<String> list = new ArrayList<String>(); //设置标记列表
            list.add("2014-04-01");
            list.add("2014-04-02");
            calendar.addMarks(list, 0);

            //监听所选中的日期
            calendar.setOnCalendarClickListener(new KCalendar.OnCalendarClickListener() {

                public void onCalendarClick(int row, int col, String dateFormat) {
                    int month = Integer.parseInt(dateFormat.substring(
                            dateFormat.indexOf("-") + 1,
                            dateFormat.lastIndexOf("-")));

                    if (calendar.getCalendarMonth() - month == 1//跨年跳转
                            || calendar.getCalendarMonth() - month == -11) {
                        calendar.lastMonth();

                    } else if (month - calendar.getCalendarMonth() == 1 //跨年跳转
                            || month - calendar.getCalendarMonth() == -11) {
                        calendar.nextMonth();

                    } else {
                        calendar.removeAllBgColor();
                        calendar.setCalendarDayBgColor(dateFormat,
                                R.drawable.calendar_date_focused);
                        date = dateFormat;//最后返回给全局 date
                    }
                }
            });

            //监听当前月份
            calendar.setOnCalendarDateChangedListener(new KCalendar.OnCalendarDateChangedListener() {
                public void onCalendarDateChanged(int year, int month) {
                    popupwindow_calendar_month
                            .setText(year + "年" + month + "月");
                }
            });

            //上月监听按钮
            RelativeLayout popupwindow_calendar_last_month = (RelativeLayout) view
                    .findViewById(R.id.popupwindow_calendar_last_month);
            popupwindow_calendar_last_month
                    .setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            calendar.lastMonth();
                        }

                    });

            //下月监听按钮
            RelativeLayout popupwindow_calendar_next_month = (RelativeLayout) view
                    .findViewById(R.id.popupwindow_calendar_next_month);
            popupwindow_calendar_next_month
                    .setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            calendar.nextMonth();
                        }
                    });

            //关闭窗口
            popupwindow_calendar_bt_enter
                    .setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            if (!Objects.equals(date, "") && !Objects.equals(date, "NULL") && date != null) {
                                updateDate();
                            }
                            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                                    R.anim.fade_out));
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            dismiss();
                        }
                    });
        }
    }
}
