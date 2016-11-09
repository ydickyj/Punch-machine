package com.pemt.pda.punchmachine.punch_machine;

/**
 * Copyright PEMT Limited © 2012-2016, All rights Reserved.
 * ShenZhen Pioneers Electrical Measurement Technology CO., LTD
 * create time: 5/22/16
 *
 * @author
 */

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.DeleteBuilder;

import com.pemt.pda.punchmachine.punch_machine.db.MediaScanner;
import com.pemt.pda.punchmachine.punch_machine.db.PDASqliteOpenHelper;
import com.pemt.pda.punchmachine.punch_machine.db.SingleMediaScanner;
import com.pemt.pda.punchmachine.punch_machine.db.bean.AppData;
import com.pemt.pda.punchmachine.punch_machine.db.bean.EmployeeInformation;
import com.pemt.pda.punchmachine.punch_machine.jna.DeviceInterface;
import com.pemt.pda.punchmachine.punch_machine.jna.RFIDDevice;
import com.pemt.pda.punchmachine.punch_machine.jna.RFIDFilter;
import com.pemt.pda.punchmachine.punch_machine.jna.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;


import java.io.File;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    private static final Logger logger = LoggerFactory.getLogger(MainActivity_.class);
    private static final byte[] VERSION_CMD = {'V', 0x0D};
    private static final byte[] SINGLE_CMD = {'Q', 0x0D};
    private static final byte[] CONTINUE_CMD = {'U', 0x0D};
    private PDASqliteOpenHelper sqLiteOpenHelper = PdaApplication.getSqliteOpenHelper();
    @ViewById
    TextView tvContext;
    @ViewById
    Button btnContinueRead, btnSingleRead, btnVersionRead;
    private RFIDDevice device = null;
    private char command = 0;
    private byte[] toSend;
    private ReadThread readThread;
    Dao newDao;
    Dao employeeInfoDao;
    List listEmployInfo = null;
    // 实例并初始化TTS对象
    TextToSpeech txtToSpeech;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//            初始化数据
        initEmployeeInfo();

    }

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
            newEmpInfo.setNAME("杨杰");
            newEmpInfo.setRFID_NO("3400110000100033450875241100A994");
            listEmpInfo.add(newEmpInfo);
            newEmpInfo = new EmployeeInformation();
            newEmpInfo.setDEPARTMENT("研发部");
            newEmpInfo.setNAME("陆祖红");
            newEmpInfo.setRFID_NO("3000E200513631180158256012E8CB7");
            listEmpInfo.add(newEmpInfo);
            newEmpInfo = new EmployeeInformation();
            newEmpInfo.setDEPARTMENT("研发部");
            newEmpInfo.setNAME("冯艳芬");
            newEmpInfo.setRFID_NO("3000E2005136311801412560132CCDA");
            listEmpInfo.add(newEmpInfo);
            employeeInfoDao.create(listEmpInfo);
            List<String> newList = sqLiteOpenHelper.queryAllTableName();
            if (newList != null) {
                for (int i = 0; i < newList.size(); i++) {
                    sqLiteOpenHelper.ExportToCSV(newList.get(i), newList.get(i) + ".csv");
                }
            }
            listEmployInfo = employeeInfoDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //递归搜索
    private String[] searchFile(String path) {
        String filePath = null;
        String fileName = null;
        File[] files = new File(path).listFiles();
        for (File f : files) {
            logger.error("file:{}", f.getPath());
            if (f.getName().endsWith("jpg")) {
                filePath += f.getPath() + "\n";
                logger.error("filePath:{}", filePath);
                fileName += f.getName() + "\n";
            }
            if (f.isDirectory() && f.listFiles() != null) {
                searchFile(f.getPath() + "/");
            }
        }
        String[] s = new String[2];
        s[0] = filePath;
        s[1] = fileName;
        return s;
    }

    void initTtsPlay() {
        txtToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
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


    @AfterViews
    void afterView() {
        tvContext.setMovementMethod(LinkMovementMethod.getInstance());
        initTtsPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Click
    void btnVersionRead() {
        if (btnVersionRead.getText().equals(getResources().getText(R.string.version_read))) {
            btnContinueRead.setEnabled(false);
            btnSingleRead.setEnabled(false);
            btnVersionRead.setText(R.string.stop_read);

            toSend = VERSION_CMD;
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

    @Click
    void btnSingleRead() {
        if (btnSingleRead.getText().equals(getResources().getText(R.string.single_read))) {
            btnContinueRead.setEnabled(false);
            btnVersionRead.setEnabled(false);
            btnSingleRead.setText(R.string.stop_read);

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

    @Click
    void btnContinueRead() {
        if (btnContinueRead.getText().equals(getResources().getText(R.string.continue_read))) {
            btnVersionRead.setEnabled(false);
            btnSingleRead.setEnabled(false);
            btnContinueRead.setText(R.string.stop_read);

            toSend = CONTINUE_CMD;
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

    @UiThread
    void reset() {
        if (readThread != null) {
            readThread.needStop = true;
            readThread = null;
        }
        command = 0;
        if (device != null) {
            device.close();
            device = null;
        }
        btnVersionRead.setEnabled(true);
        btnSingleRead.setEnabled(true);
        btnContinueRead.setEnabled(true);
        btnVersionRead.setText(R.string.version_read);
        btnSingleRead.setText(R.string.single_read);
        btnContinueRead.setText(R.string.continue_read);
    }

    @UiThread
    void appendText(String msg) {
        tvContext.append(msg);
        MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.tip);
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
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

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
                                            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                                            String date = sDateFormat.format(new java.util.Date());
                                            checkSum(result);
                                        }
                                        command = 0;
                                        reset();
                                        needStop = true;
                                        //加入循环方法
                                        circularProcess();
                                    } else {
                                        logger.error("继续的标志：{}", command);
                                        Thread.sleep(200);
                                        if (result.length() > 1) {
                                            appendText(result + "\n");
                                        }
                                        baseDevice.write(CONTINUE_CMD);
                                    }
                                } else {
                                    logger.error("COMMAND NOT MATCH:" + result);
                                    appendText(result + "\n");
                                    reset();
                                    needStop = true;
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
        }
    }

    //持续监听
    @UiThread
    void circularProcess() {
        if (btnSingleRead.getText().equals(getResources().getText(R.string.single_read))) {
            btnContinueRead.setEnabled(false);
            btnVersionRead.setEnabled(false);
            btnSingleRead.setText(R.string.stop_read);

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

    @UiThread
    void checkSum(String result) {
        String name = null;
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        String date = sDateFormat.format(new java.util.Date());

        AppData newAppData = new AppData();
        logger.error("结果：{}", result.substring(1, result.length() - 1));
//        记录缓冲区
        if (listEmployInfo.size() != 0 && listEmployInfo != null) {
            for (int i = 0; i < listEmployInfo.size(); i++) {
                if (Objects.equals(((EmployeeInformation) (listEmployInfo.get(i))).getRFID_NO(), result.substring(1, result.length() - 1))) {
                    name = ((EmployeeInformation) (listEmployInfo.get(i))).getNAME();
                }
            }
        }
        if (name == null) {
            logger.error("员工不存在");
        } else {
            if (campareTime(sDateFormat, name)) {
                newAppData.setNAME(name);
                newAppData.setRECORD_TIME(date);
                try {
                    Calendar calendar = Calendar.getInstance();
                    newDao = sqLiteOpenHelper.getDao(AppData.class, calendar);
                    newDao.create(newAppData);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                txtToSpeech.speak(name + " 已打卡", TextToSpeech.QUEUE_FLUSH, null);
                String lastResult = name + " " + date + " 已打卡";
                appendText(lastResult + "\n\n");
            } else {
                logger.error("不需要频繁打卡");
            }
        }
    }

    //是否频繁打卡判断
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
            d1 = sDateFormat.parse(sDateFormat.format(new java.util.Date()));
            d2 = sDateFormat.parse(date);
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }
        long diff = (d1 != null ? d1.getTime() : 0) - (d2 != null ? d2.getTime() : 0);
        long day = diff / (24 * 60 * 60 * 1000);
        long hour = (diff / (60 * 60 * 1000) - day * 24);
        long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);

        return min >= 1;
    }


    //扫描系统文件
    private void scanSdCard() {
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + Environment.getExternalStorageDirectory().getPath())));

        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().getPath()}, null, null);
        File sdCardDir = Environment.getExternalStorageDirectory();
        new SingleMediaScanner(MainActivity.this.getApplicationContext(), sdCardDir);
        searchFile(Environment.getExternalStorageDirectory().getPath());
        Uri uri = Uri.fromFile(sdCardDir);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        sendBroadcast(intent);
    }
}

