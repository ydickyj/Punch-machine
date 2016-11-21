package com.pemt.pda.punchmachine.punch_machine;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.pemt.pda.punchmachine.punch_machine.db.PDASqliteOpenHelper;
import com.pemt.pda.punchmachine.punch_machine.db.bean.EmployeeInformation;
import com.pemt.pda.punchmachine.punch_machine.pages.BlueDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created dicky on 2016/11/17.
 */

@EActivity(R.layout.activity_staff)
public class StaffManagementActivity extends Activity {
    private static Logger logger = LoggerFactory.getLogger(StaffManagementActivity_.class);
    @ViewById
    TextView tvEdit;
    @ViewById
    ImageView ivBack;
    @ViewById
    TextView tvTitle;
    @ViewById
    ListView lvContext;
    @ViewById
    Button addCustom;
    SimpleAdapter mBaseAdapter;
    SimpleAdapter mEditBaseAdapter;
    DialogInterface staffDialog;
    private PDASqliteOpenHelper sqLiteOpenHelper = PdaApplication.getSqliteOpenHelper();
    private ArrayList<Map<String, Object>> mAppList = new ArrayList<>();
    private ArrayList<EmployeeInformation> mStaffList = new ArrayList<>();

    private void initView() {
        mBaseAdapter = new SimpleAdapter(this, mAppList, R.layout.listview_item_staff, new String[]{"tvContext", "tvEngContext", "icon"}, new int[]{R.id.tv_content, R.id.tv_eng_content, R.id.img_icon});
//        SwingBottomInAnimationAdapter nMyAdapter = new SwingBottomInAnimationAdapter(mBaseAdapter);
//        nMyAdapter.setListView(lvContext);
        lvContext.setDivider(null);
        lvContext.setAdapter(mBaseAdapter);
        lvContext.setEnabled(false);
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
        mAppList = dataListUpdata(mStaffList);
        initView();
    }

    @Click
    void ivBack() {
        onBackPressed();
    }

    @Click
    void tvEdit() {
        if (Objects.equals(tvEdit.getText().toString(), "编辑")) {
            mEditBaseAdapter = new SimpleAdapter(this, mAppList, R.layout.listview_item_staff_edit, new String[]{"tvContext", "tvEngContext", "icon"}, new int[]{R.id.tv_content, R.id.tv_eng_content, R.id.img_icon});
            lvContext.setDivider(null);
            lvContext.setAdapter(mEditBaseAdapter);
            lvContext.setEnabled(true);
            mEditBaseAdapter.notifyDataSetChanged();
            tvEdit.setText("退出编辑");
            lvContext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    RelativeLayout ivDelete = (RelativeLayout) view.findViewById(R.id.edit_icon);
                    editCustom(position);
                    ivDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LayoutInflater layoutInflater = LayoutInflater.from(StaffManagementActivity.this);
                            final View layout = layoutInflater.inflate(R.layout.warning_dialog, null);
                            BlueDialog.Builder builder = new BlueDialog.Builder(StaffManagementActivity.this).setContentView(layout);
                            builder.setTitle("删除警示");
                            builder.setMessage("你确定要删除该公司成员信息么？");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    EmployeeInformation newEmployee = mStaffList.get(position);
                                    addCustom(newEmployee, 3);
                                    staffDialog = dialog;
                                    //设置你的操作事项
                                }
                            });
                            builder.setNegativeButton("取消",
                                    new android.content.DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            builder.create().show();
                        }
                    });
                }
            });
        } else if (Objects.equals(tvEdit.getText().toString(), "退出编辑")) {
            initView();
            dataListUpdata(mStaffList);
            mBaseAdapter.notifyDataSetChanged();
            tvEdit.setText("编辑");
        }
    }

    @Click
    void addCustom() {
        final Context newContext = this.getApplication().getBaseContext();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View layout = layoutInflater.inflate(R.layout.add_custom_dialog, null);
        BlueDialog.Builder builder = new BlueDialog.Builder(this).setContentView(layout);
        builder.setTitle("新员工入职");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EditText etName = (EditText) layout.findViewById(R.id.et_name);
                EditText etDepartment = (EditText) layout.findViewById(R.id.et_job_department);
                EditText etJob = (EditText) layout.findViewById(R.id.et_job);
                EditText etJobNo = (EditText) layout.findViewById(R.id.et_job_no);
                EditText etRfid = (EditText) layout.findViewById(R.id.et_rfid);
                String etName12 = etName.getText().toString();
                logger.error("名字：{}", etName12);
                if (Objects.equals(etName.getText().toString(), "")) {
                    Toast.makeText(newContext, "姓名不能为空", Toast.LENGTH_SHORT).show();
                } else if (Objects.equals(etDepartment.getText().toString(), "")) {
                    Toast.makeText(newContext, "职位不能为空", Toast.LENGTH_SHORT).show();
                } else if (Objects.equals(etJob.getText().toString(), "")) {
                    Toast.makeText(newContext, "部门不能为空", Toast.LENGTH_SHORT).show();
                } else if (Objects.equals(etJobNo.getText().toString(), "")) {
                    Toast.makeText(newContext, "工号不能为空", Toast.LENGTH_SHORT).show();
                } else if (Objects.equals(etRfid.getText().toString(), "")) {
                    Toast.makeText(newContext, "RFID不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    EmployeeInformation newCustom = new EmployeeInformation();
                    newCustom.setNAME(etName.getText().toString());
                    newCustom.setDEPARTMENT(etDepartment.getText().toString());
                    newCustom.setJOB(etJob.getText().toString());
                    newCustom.setJOB_NUMBER(etJobNo.getText().toString());
                    newCustom.setRFID_NO(etRfid.getText().toString());
                    addCustom(newCustom, 1);
                    staffDialog = dialog;
                }
                //设置你的操作事项
            }
        });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();

    }

    @UiThread
    void addCustom(EmployeeInformation Custom, int flag) {
        Dao employeeInfoDao = null;
//        1代表增加数据 2代表更新数据 3删除数据
        switch (flag) {
            case 1:
                try {
                    employeeInfoDao = sqLiteOpenHelper.getDao(EmployeeInformation.class);
                    employeeInfoDao.create(Custom);
                    staffDialog.dismiss();
                    Toast.makeText(this, "数据保存成功", Toast.LENGTH_SHORT).show();
                    mStaffList.clear();
                    mStaffList.addAll((ArrayList<EmployeeInformation>) employeeInfoDao.queryForAll());
                } catch (SQLException e) {
                    Toast.makeText(this, "数据保存失败" + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                mAppList.clear();
                mAppList.addAll(dataListUpdata(mStaffList));
                mBaseAdapter.notifyDataSetChanged();
                mEditBaseAdapter.notifyDataSetChanged();
                break;
            case 2:
                try {
                    employeeInfoDao = sqLiteOpenHelper.getDao(EmployeeInformation.class);
                    employeeInfoDao.update(Custom);
                    staffDialog.dismiss();
                    Toast.makeText(this, "数据更新成功", Toast.LENGTH_SHORT).show();
                    mStaffList.clear();
                    mStaffList.addAll((ArrayList<EmployeeInformation>) employeeInfoDao.queryForAll());
                } catch (SQLException e) {
                    Toast.makeText(this, "数据保存失败" + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                mAppList.clear();
                mAppList.addAll(dataListUpdata(mStaffList));
                mEditBaseAdapter.notifyDataSetChanged();
                break;
            case 3:
                try {
                    employeeInfoDao = sqLiteOpenHelper.getDao(EmployeeInformation.class);
                    employeeInfoDao.delete(Custom);
                    staffDialog.dismiss();
                    mStaffList.clear();
                    mStaffList.addAll((ArrayList<EmployeeInformation>) employeeInfoDao.queryForAll());

                    Toast.makeText(this, "数据删除成功", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    Toast.makeText(this, "数据删除失败" + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                mAppList.clear();
                mAppList.addAll(dataListUpdata(mStaffList));
                mEditBaseAdapter.notifyDataSetChanged();
                break;
        }

    }

    @UiThread
    void editCustom(final int position) {
        final Context newContext = this.getApplication().getBaseContext();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View layout = layoutInflater.inflate(R.layout.add_custom_dialog, null);
        BlueDialog.Builder builder = new BlueDialog.Builder(this).setContentView(layout);
        final EditText etName = (EditText) layout.findViewById(R.id.et_name);
        final EditText etDepartment = (EditText) layout.findViewById(R.id.et_job_department);
        final EditText etJob = (EditText) layout.findViewById(R.id.et_job);
        final EditText etJobNo = (EditText) layout.findViewById(R.id.et_job_no);
        final EditText etRfid = (EditText) layout.findViewById(R.id.et_rfid);
        etName.setText(mStaffList.get(position).getNAME());
        etDepartment.setText(mStaffList.get(position).getDEPARTMENT());
        etJob.setText(mStaffList.get(position).getJOB());
        etJobNo.setText(mStaffList.get(position).getJOB_NUMBER());
        etRfid.setText(mStaffList.get(position).getRFID_NO());
        builder.setTitle("员工信息编辑");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (Objects.equals(etName.getText().toString(), "")) {
                    Toast.makeText(newContext, "姓名不能为空", Toast.LENGTH_SHORT).show();
                } else if (Objects.equals(etDepartment.getText().toString(), "")) {
                    Toast.makeText(newContext, "职位不能为空", Toast.LENGTH_SHORT).show();
                } else if (Objects.equals(etJob.getText().toString(), "")) {
                    Toast.makeText(newContext, "部门不能为空", Toast.LENGTH_SHORT).show();
                } else if (Objects.equals(etJobNo.getText().toString(), "")) {
                    Toast.makeText(newContext, "工号不能为空", Toast.LENGTH_SHORT).show();
                } else if (Objects.equals(etRfid.getText().toString(), "")) {
                    Toast.makeText(newContext, "RFID不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    EmployeeInformation newCustom = new EmployeeInformation();
                    newCustom.setId(mStaffList.get(position).getId());
                    newCustom.setNAME(etName.getText().toString());
                    newCustom.setDEPARTMENT(etDepartment.getText().toString());
                    newCustom.setJOB(etJob.getText().toString());
                    newCustom.setJOB_NUMBER(etJobNo.getText().toString());
                    newCustom.setRFID_NO(etRfid.getText().toString());
                    staffDialog = dialog;
                    addCustom(newCustom, 2);
                }
                //设置你的操作事项
            }
        });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private ArrayList<Map<String, Object>> dataListUpdata(ArrayList<EmployeeInformation> staffList) {
        ArrayList<Map<String, Object>> appList = new ArrayList<>();
        if (staffList != null) {
            Map<String, Object> listem = new HashMap<String, Object>();
            for (int i = 0; i < mStaffList.size(); i++) {
                listem = new HashMap<String, Object>();
                listem.put("tvContext", mStaffList.get(i).getNAME());
                listem.put("tvEngContext", mStaffList.get(i).getJOB());
                appList.add(listem);
            }
        }
        return appList;
    }
}
