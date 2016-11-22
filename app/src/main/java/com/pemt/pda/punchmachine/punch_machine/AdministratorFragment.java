package com.pemt.pda.punchmachine.punch_machine;

/**
 * Copyright PEMT Limited © 2012-2016, All rights Reserved.
 * ShenZhen Pioneers Electrical Measurement Technology CO., LTD
 * create time: 5/22/16
 *
 * @author
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@EFragment(R.layout.activity_administrator)
public class AdministratorFragment extends Fragment {
    private static Logger logger = LoggerFactory.getLogger(AdministratorFragment_.class);
    @ViewById
    ListView lvContext;
    SimpleAdapter mBaseAdapter;
    private ArrayList<Map<String, Object>> mAppList = new ArrayList<>();

    private void initView() {
        final Context newContext = this.getActivity().getApplication().getBaseContext();
        mBaseAdapter = new SimpleAdapter(newContext, mAppList, R.layout.listview_item_admin, new String[]{"tvContext", "tvEngContext", "icon"}, new int[]{R.id.tv_content, R.id.tv_eng_content, R.id.img_icon});
//        SwingBottomInAnimationAdapter nMyAdapter = new SwingBottomInAnimationAdapter(mBaseAdapter);
//        nMyAdapter.setListView(lvContext);
        lvContext.setDivider(null);
        lvContext.setAdapter(mBaseAdapter);

    }

    @AfterViews
    void afterView() {

        logger.error("afterView");
        Map<String, Object> listem = new HashMap<String, Object>();
        listem.put("tvContext", "员工管理");
        listem.put("tvEngContext", "personal management");
        listem.put("icon", R.drawable.personal_management);
        mAppList.add(listem);
        listem = new HashMap<String, Object>();
        listem.put("tvContext", "打卡记录");
        listem.put("tvEngContext", "punch record");
        listem.put("icon", R.drawable.punch_record);
        mAppList.add(listem);
        initView();
    }


    @ItemClick
    public void lvContext(int position) {
        final Context newContext = this.getActivity().getApplication().getBaseContext();
        switch (position) {
            case 0:
                startActivity(new Intent(newContext, StaffManagementActivity_.class));
                break;
            case 1:
                startActivity(new Intent(newContext, PunchRecord.class));
                break;
        }
    }

}

