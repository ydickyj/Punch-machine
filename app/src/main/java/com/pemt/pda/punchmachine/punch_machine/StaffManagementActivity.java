package com.pemt.pda.punchmachine.punch_machine;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.pemt.pda.punchmachine.punch_machine.db.PDASqliteOpenHelper;
import com.pemt.pda.punchmachine.punch_machine.db.bean.EmployeeInformation;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eng005 on 2016/11/17.
 */

@EActivity(R.layout.activity_staff)
public class StaffManagementActivity extends Activity {
    private static Logger logger = LoggerFactory.getLogger(StaffManagementActivity_.class);
    @ViewById
    ImageView ivBack;
    @ViewById
    TextView tvTitle;
    @ViewById
    ListView lvContext;
    SimpleAdapter mBaseAdapter;

    private PDASqliteOpenHelper sqLiteOpenHelper = PdaApplication.getSqliteOpenHelper();
    private ArrayList<Map<String, Object>> mAppList = new ArrayList<>();
    private ArrayList<EmployeeInformation> mStaffList = new ArrayList<>();

    private void initView() {
        final Context newContext = this.getApplication().getBaseContext();
        mBaseAdapter = new SimpleAdapter(newContext, mAppList, R.layout.listview_item_staff, new String[]{"tvContext", "tvEngContext", "icon"}, new int[]{R.id.tv_content, R.id.tv_eng_content, R.id.img_icon});
//        SwingBottomInAnimationAdapter nMyAdapter = new SwingBottomInAnimationAdapter(mBaseAdapter);
//        nMyAdapter.setListView(lvContext);
        lvContext.setDivider(null);
        lvContext.setAdapter(mBaseAdapter);
    }

    @AfterViews
    void afterView() {
        tvTitle.setText("员工管理");
        Dao employeeInfoDao = null;
        try {
            employeeInfoDao = sqLiteOpenHelper.getDao(EmployeeInformation.class);
            mStaffList = (ArrayList<EmployeeInformation>) employeeInfoDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        logger.error("afterView");

        if (mStaffList != null) {
            Map<String, Object> listem = new HashMap<String, Object>();
            for (int i = 0; i < mStaffList.size(); i++) {
                listem = new HashMap<String, Object>();
                listem.put("tvContext", mStaffList.get(i).getNAME());
                listem.put("tvEngContext", mStaffList.get(i).getJOB());
                mAppList.add(listem);
            }
        }
        initView();
    }

    @Click
    void ivBack() {
        onBackPressed();
    }

}
