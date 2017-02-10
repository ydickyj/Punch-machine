package com.pemt.pda.punchmachine.punch_machine;

/**
 * Copyright PEMT Limited © 2012-2016, All rights Reserved.
 * ShenZhen Pioneers Electrical Measurement Technology CO., LTD
 * create time: 5/22/16
 *
 * @author
 */

import android.content.Context;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.pemt.pda.punchmachine.punch_machine.adapter.MyBaseAdapter;
import com.pemt.pda.punchmachine.punch_machine.adapter.listviewAnimationAdapter.SwingBottomInAnimationAdapter;
import com.pemt.pda.punchmachine.punch_machine.db.PDASqliteOpenHelper;
import com.pemt.pda.punchmachine.punch_machine.db.bean.AppData;
import com.pemt.pda.punchmachine.punch_machine.db.bean.EmployeeInformation;
import com.pemt.pda.punchmachine.punch_machine.jna.DeviceInterface;
import com.pemt.pda.punchmachine.punch_machine.jna.RFIDDevice;
import com.pemt.pda.punchmachine.punch_machine.jna.RFIDFilter;
import com.pemt.pda.punchmachine.punch_machine.jna.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@EFragment(R.layout.activity_main)
public class MainActivity extends Fragment {
    private static final byte[] SINGLE_CMD = {'Q', 0x0D};
    private static final byte[] CONTINUE_CMD = {'U', 0x0D};
    private static Logger logger = LoggerFactory.getLogger(MainActivity_.class);
    @ViewById
    TextView tvContext;
    @ViewById
    Switch switchSingleRead;
    @ViewById(R.id.lv_context)
    ListView lvContext;
    MyBaseAdapter mBaseAdapter;
    private PDASqliteOpenHelper sqLiteOpenHelper = PdaApplication.getSqliteOpenHelper();
    private RFIDDevice device = null;
    private char command = 0;
    private byte[] toSend;
    private ReadThread readThread;
    private Dao newDao;
    private Dao employeeInfoDao;
    private List listEmployInfo = null;
    private ArrayList<AppData> mAppList = new ArrayList<>();
    // 实例并初始化TTS对象
    private TextToSpeech txtToSpeech;
    private String officeLocation = "卫东龙商务大厦";

    private void initEmployeeInfo() {
        Calendar calendar = Calendar.getInstance();
        try {
            newDao = sqLiteOpenHelper.getDao(AppData.class, calendar);
            employeeInfoDao = sqLiteOpenHelper.getDao(EmployeeInformation.class);
            DeleteBuilder<EmployeeInformation, Integer> deleteBuilder = employeeInfoDao.deleteBuilder();
            employeeInfoDao.delete(deleteBuilder.prepare());
            ArrayList<EmployeeInformation> listEmpInfo = new ArrayList<>();
            EmployeeInformation newEmpInfo = new EmployeeInformation();
            newEmpInfo.setDEPARTMENT("研发部");
            newEmpInfo.setNAME("dicky");
            newEmpInfo.setJOB("安卓软件开发工程师");
            newEmpInfo.setRFID_NO("3000E2003098060700601090AA55610");
            listEmpInfo.add(newEmpInfo);
            newEmpInfo = new EmployeeInformation();
            newEmpInfo.setDEPARTMENT("研发部");
            newEmpInfo.setNAME("陆师");
            newEmpInfo.setJOB("软件开发工程师");
            newEmpInfo.setRFID_NO("3000E200513631180158256012E8CB7");
            listEmpInfo.add(newEmpInfo);
            newEmpInfo = new EmployeeInformation();
            newEmpInfo.setDEPARTMENT("研发部");
            newEmpInfo.setNAME("马小姐");
            newEmpInfo.setRFID_NO("3000E2005136311801412560132CCDA");
            newEmpInfo.setJOB("项目专员");
            listEmpInfo.add(newEmpInfo);
            newEmpInfo = new EmployeeInformation();
            newEmpInfo.setDEPARTMENT("研发部");
            newEmpInfo.setNAME("李小姐");
            newEmpInfo.setRFID_NO("3000E2003098060700601030B2C4C5D");
            newEmpInfo.setJOB("项目专员");
            listEmpInfo.add(newEmpInfo);
            employeeInfoDao.create(listEmpInfo);

            listEmployInfo = employeeInfoDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initTtsPlay() {
        final Context newContext = this.getActivity().getApplication().getBaseContext();
        txtToSpeech = new TextToSpeech(newContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // 设置朗读语言
                    int supported = txtToSpeech.setLanguage(Locale.CHINESE);
                    if ((supported != TextToSpeech.LANG_AVAILABLE)
                            && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
                        logger.error("不支持当前");
                    }
                }
            }
        });

    }

    private void initView() {
        final Context newContext = this.getActivity().getApplication().getBaseContext();
        tvContext.setMovementMethod(LinkMovementMethod.getInstance());
        mBaseAdapter = new MyBaseAdapter(newContext, mAppList);
        SwingBottomInAnimationAdapter nMyAdapter = new SwingBottomInAnimationAdapter(mBaseAdapter);
        nMyAdapter.setListView(lvContext);
        lvContext.setDivider(null);
        lvContext.setAdapter(nMyAdapter);
        switchSingleRead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toSend = SINGLE_CMD;
                    try {
                        device = new RFIDDevice();
                        device.open();
                        readThread = new ReadThread();
                        readThread.start();
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                } else {
                    reset();
                }
            }
        });
    }

    @AfterViews
    void afterView() {
        initEmployeeInfo();
        initTtsPlay();
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @UiThread
    void reset() {
        logger.error("reset");
        command = 0;
        if (device != null) {
            device.close();
            device = null;
        }
    }

    @UiThread
    void appendText(String msg) {
        tvContext.append(msg);
        final Context newContext = this.getActivity().getApplication().getBaseContext();
        MediaPlayer mPlayer = MediaPlayer.create(newContext, R.raw.tip);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mPlayer.start();
    }

    @UiThread
    void makeToast(String msg) {
        final Context newContext = this.getActivity().getApplication().getBaseContext();
        Toast.makeText(newContext, msg, Toast.LENGTH_SHORT).show();
    }


    //    重复开启线程
    @UiThread
    void circularProcess() {
        if (readThread != null) {
            if (readThread.needStop) {
                logger.error("线程已停止");
            } else {
                toSend = SINGLE_CMD;
                try {
                    device = new RFIDDevice();
                    device.open();
                    readThread = new ReadThread();
                    readThread.start();
                } catch (Exception e) {
                    logger.error("", e);
                }
                logger.error("线程开启");
            }
        }

    }


    //    是否频繁打卡判断
    private boolean campareTime(SimpleDateFormat sDateFormat, String name) {
        java.util.Date d1 = null;
        java.util.Date d2 = null;
        try {
            Calendar calendar = Calendar.getInstance();
            newDao = sqLiteOpenHelper.getDao(AppData.class, calendar);
            String date = null;
            AppData appDate = (AppData) newDao.queryBuilder().orderBy("ID", false).where().eq("NAME", name).queryForFirst();

            if (appDate == null) {
                return true;
            } else {
                date = appDate.getRECORD_TIME();
            }
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            logger.error("之前记录时间：{},当前记录时间：{}", date, sDateFormat.format(curDate));
            d1 = sDateFormat.parse(sDateFormat.format(curDate));
            d2 = sDateFormat.parse(date);
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }
        long diff = (d1 != null ? d1.getTime() : 0) - (d2 != null ? d2.getTime() : 0);
        long day = diff / (24 * 60 * 60 * 1000);
        long hour = (diff / (60 * 60 * 1000) - day * 24);
        long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        logger.error("比较完成");
        return min >= 1;
    }

    @UiThread
    void recordRightResult(AppData appData) {
        mAppList.add(appData);
        try {
            Calendar calendar = Calendar.getInstance();
            newDao = sqLiteOpenHelper.getDao(AppData.class, calendar);
            newDao.create(appData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mBaseAdapter.notifyDataSetChanged();
        lvContext.setSelection(lvContext.getBottom());
        txtToSpeech.speak(appData.getNAME() + " 已打卡", TextToSpeech.QUEUE_FLUSH, null);

//        String lastResult = name + " " + date + " 已打卡";
//        appendText(lastResult + "\n\n");
    }

    //  监听线程定义
    private class ReadThread extends Thread {
        boolean needStop = false;

        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            final byte[] buff = new byte[64];
            DeviceInterface baseDevice;
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                logger.error("", e);
            }
            while (!needStop) {
                try {
                    baseDevice = device;
                    if (baseDevice == null) {
                        Thread.sleep(100);
                        continue;
                    }
                    command = (char) toSend[0];
                    baseDevice.write(toSend);
                    final int count = baseDevice.read(buff);
                    if (count > 0) {
                        logger.debug("data recv:{}", Utils.toHex(buff, 0, count));
                        buffer.put(buff, 0, count);
                        buffer.flip();
                        while (true) {
                            byte[] frame = RFIDFilter.getInstance().filter(buffer);
                            if (frame == null) {// 为空表明没有数据可以处理了
                                buffer.compact();// 切换模式
                                break;
                            } else {
                                String result = new String(frame).trim();
                                logger.debug("frame:{}", result);
                                if ((result.charAt(0) & 0xff) == (command & 0xff)) {
                                    if (command != CONTINUE_CMD[0]) {
                                        logger.error("结束的标志：{}", command);
                                        if (!Objects.equals(result, "Q")) {
                                            //读取成功
                                            String name = null;
                                            String job = null;
                                            String department = null;
                                            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                            String date = sDateFormat.format(new java.util.Date());
                                            AppData newAppData = new AppData();
                                            logger.error("结果：{}", result.substring(1, result.length() - 1));
                                            if (listEmployInfo.size() != 0 && listEmployInfo != null) {
                                                for (int i = 0; i < listEmployInfo.size(); i++) {
                                                    if (Objects.equals(((EmployeeInformation) (listEmployInfo.get(i))).getRFID_NO(), result.substring(1, result.length() - 1))) {
                                                        EmployeeInformation dataCache = (EmployeeInformation) (listEmployInfo.get(i));
                                                        name = dataCache.getNAME();
                                                        job = dataCache.getJOB();
                                                        department = dataCache.getDEPARTMENT();
                                                    }
                                                }
                                            }
                                            if (name == null) {
                                                logger.error("员工不存在");
                                            } else {
                                                if (campareTime(sDateFormat, name)) {
                                                    newAppData.setNAME(name);
                                                    newAppData.setRECORD_TIME(date);
                                                    newAppData.setJOB(job);
                                                    newAppData.setDEPARTMENT(department);
                                                    newAppData.setOFFICE_LOCATION(officeLocation);
                                                    recordRightResult(newAppData);
                                                } else {
                                                    logger.error("不需要频繁打卡");
                                                }
                                            }
                                        }
                                        command = 0;
//                                        reset();
//                                        needStop = true;
                                        //加入循环方法

                                    }
                                } else {
                                    logger.error("COMMAND NOT MATCH:" + result);
//                                    appendText(result + "\n");
//                                    reset();
//                                    needStop = true;
                                }
                            }
                        }
                    } else {
                        Thread.sleep(100);

                    }
                } catch (Exception e) {
                    logger.error("", e);
                    needStop = true;
                }

            }
            circularProcess();
        }
    }
}

