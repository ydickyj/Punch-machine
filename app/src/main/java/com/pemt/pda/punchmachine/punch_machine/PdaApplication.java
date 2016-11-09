package com.pemt.pda.punchmachine.punch_machine;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;


import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.pemt.pda.punchmachine.punch_machine.db.PDASqliteOpenHelper;


import org.androidannotations.annotations.EApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * copyright© www.pemt.com.cn
 * Created by yf on 16-4-11.
 */

@EApplication
public class PdaApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(PdaApplication.class);

    public static String optCardNo = "8693159143152724";         //操作员卡
    public static String busiCardNo = "86931590DA011D1B";        //业务员卡号
    public static String optName = "";           //操作员名

    private static PdaApplication application;
    private static PDASqliteOpenHelper sqliteOpenHelper;
    private Handler handler = new Handler();
    private volatile SharedPreferences pref;

    public static PdaApplication getApplication() {
        return application;
    }

    public static PDASqliteOpenHelper getSqliteOpenHelper() {
        return sqliteOpenHelper;
    }

    public static Handler getHandler() {
        return application.handler;
    }

    public static void makeToast(final String msg) {
        makeToast(msg, Toast.LENGTH_SHORT);
    }

    public static void makeToast(final String msg, final int length) {
        application.handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(application, msg, length).show();
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        sqliteOpenHelper = OpenHelperManager.getHelper(this, PDASqliteOpenHelper.class);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


}
