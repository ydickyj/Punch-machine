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
    static byte[] crc16_tab_h = {(byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1,
            (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40};
    static byte[] crc16_tab_l = {(byte) 0x00, (byte) 0xC0, (byte) 0xC1, (byte) 0x01, (byte) 0xC3, (byte) 0x03, (byte) 0x02, (byte) 0xC2, (byte) 0xC6, (byte) 0x06, (byte) 0x07, (byte) 0xC7, (byte) 0x05, (byte) 0xC5, (byte) 0xC4, (byte) 0x04, (byte) 0xCC, (byte) 0x0C, (byte) 0x0D, (byte) 0xCD, (byte) 0x0F, (byte) 0xCF, (byte) 0xCE, (byte) 0x0E, (byte) 0x0A, (byte) 0xCA, (byte) 0xCB, (byte) 0x0B, (byte) 0xC9, (byte) 0x09, (byte) 0x08, (byte) 0xC8, (byte) 0xD8, (byte) 0x18, (byte) 0x19, (byte) 0xD9, (byte) 0x1B, (byte) 0xDB, (byte) 0xDA, (byte) 0x1A, (byte) 0x1E, (byte) 0xDE, (byte) 0xDF, (byte) 0x1F, (byte) 0xDD, (byte) 0x1D, (byte) 0x1C, (byte) 0xDC, (byte) 0x14, (byte) 0xD4, (byte) 0xD5, (byte) 0x15, (byte) 0xD7, (byte) 0x17, (byte) 0x16, (byte) 0xD6, (byte) 0xD2, (byte) 0x12,
            (byte) 0x13, (byte) 0xD3, (byte) 0x11, (byte) 0xD1, (byte) 0xD0, (byte) 0x10, (byte) 0xF0, (byte) 0x30, (byte) 0x31, (byte) 0xF1, (byte) 0x33, (byte) 0xF3, (byte) 0xF2, (byte) 0x32, (byte) 0x36, (byte) 0xF6, (byte) 0xF7, (byte) 0x37, (byte) 0xF5, (byte) 0x35, (byte) 0x34, (byte) 0xF4, (byte) 0x3C, (byte) 0xFC, (byte) 0xFD, (byte) 0x3D, (byte) 0xFF, (byte) 0x3F, (byte) 0x3E, (byte) 0xFE, (byte) 0xFA, (byte) 0x3A, (byte) 0x3B, (byte) 0xFB, (byte) 0x39, (byte) 0xF9, (byte) 0xF8, (byte) 0x38, (byte) 0x28, (byte) 0xE8, (byte) 0xE9, (byte) 0x29, (byte) 0xEB, (byte) 0x2B, (byte) 0x2A, (byte) 0xEA, (byte) 0xEE, (byte) 0x2E, (byte) 0x2F, (byte) 0xEF, (byte) 0x2D, (byte) 0xED, (byte) 0xEC, (byte) 0x2C, (byte) 0xE4, (byte) 0x24, (byte) 0x25, (byte) 0xE5, (byte) 0x27, (byte) 0xE7,
            (byte) 0xE6, (byte) 0x26, (byte) 0x22, (byte) 0xE2, (byte) 0xE3, (byte) 0x23, (byte) 0xE1, (byte) 0x21, (byte) 0x20, (byte) 0xE0, (byte) 0xA0, (byte) 0x60, (byte) 0x61, (byte) 0xA1, (byte) 0x63, (byte) 0xA3, (byte) 0xA2, (byte) 0x62, (byte) 0x66, (byte) 0xA6, (byte) 0xA7, (byte) 0x67, (byte) 0xA5, (byte) 0x65, (byte) 0x64, (byte) 0xA4, (byte) 0x6C, (byte) 0xAC, (byte) 0xAD, (byte) 0x6D, (byte) 0xAF, (byte) 0x6F, (byte) 0x6E, (byte) 0xAE, (byte) 0xAA, (byte) 0x6A, (byte) 0x6B, (byte) 0xAB, (byte) 0x69, (byte) 0xA9, (byte) 0xA8, (byte) 0x68, (byte) 0x78, (byte) 0xB8, (byte) 0xB9, (byte) 0x79, (byte) 0xBB, (byte) 0x7B, (byte) 0x7A, (byte) 0xBA, (byte) 0xBE, (byte) 0x7E, (byte) 0x7F, (byte) 0xBF, (byte) 0x7D, (byte) 0xBD, (byte) 0xBC, (byte) 0x7C, (byte) 0xB4, (byte) 0x74,
            (byte) 0x75, (byte) 0xB5, (byte) 0x77, (byte) 0xB7, (byte) 0xB6, (byte) 0x76, (byte) 0x72, (byte) 0xB2, (byte) 0xB3, (byte) 0x73, (byte) 0xB1, (byte) 0x71, (byte) 0x70, (byte) 0xB0, (byte) 0x50, (byte) 0x90, (byte) 0x91, (byte) 0x51, (byte) 0x93, (byte) 0x53, (byte) 0x52, (byte) 0x92, (byte) 0x96, (byte) 0x56, (byte) 0x57, (byte) 0x97, (byte) 0x55, (byte) 0x95, (byte) 0x94, (byte) 0x54, (byte) 0x9C, (byte) 0x5C, (byte) 0x5D, (byte) 0x9D, (byte) 0x5F, (byte) 0x9F, (byte) 0x9E, (byte) 0x5E, (byte) 0x5A, (byte) 0x9A, (byte) 0x9B, (byte) 0x5B, (byte) 0x99, (byte) 0x59, (byte) 0x58, (byte) 0x98, (byte) 0x88, (byte) 0x48, (byte) 0x49, (byte) 0x89, (byte) 0x4B, (byte) 0x8B, (byte) 0x8A, (byte) 0x4A, (byte) 0x4E, (byte) 0x8E, (byte) 0x8F, (byte) 0x4F, (byte) 0x8D, (byte) 0x4D,
            (byte) 0x4C, (byte) 0x8C, (byte) 0x44, (byte) 0x84, (byte) 0x85, (byte) 0x45, (byte) 0x87, (byte) 0x47, (byte) 0x46, (byte) 0x86, (byte) 0x82, (byte) 0x42, (byte) 0x43, (byte) 0x83, (byte) 0x41, (byte) 0x81, (byte) 0x80, (byte) 0x40};
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
    DialogInterface staffDialog;
    Context context;
    private PDASqliteOpenHelper sqLiteOpenHelper = PdaApplication.getSqliteOpenHelper();
    private ArrayList<Map<String, Object>> mAppList = new ArrayList<>();
    private ArrayList<EmployeeInformation> mStaffList = new ArrayList<>();

    /**
     * 计算CRC16校验
     *
     * @param data 需要计算的数组
     * @return CRC16校验值
     */
    public static int calcCrc16(byte[] data) {
        return calcCrc16(data, 0, data.length);
    }

    /**
     * 计算CRC16校验
     *
     * @param data   需要计算的数组
     * @param offset 起始位置
     * @param len    长度
     * @return CRC16校验值
     */
    public static int calcCrc16(byte[] data, int offset, int len) {
        return calcCrc16(data, offset, len, 0xffff);
    }

    /**
     * 计算CRC16校验
     *
     * @param data   需要计算的数组
     * @param offset 起始位置
     * @param len    长度
     * @param preval 之前的校验值
     * @return CRC16校验值
     */
    public static int calcCrc16(byte[] data, int offset, int len, int preval) {
        int ucCRCHi = (preval & 0xff00) >> 8;
        int ucCRCLo = preval & 0x00ff;
        int iIndex;
        for (int i = 0; i < len; ++i) {
            iIndex = (ucCRCLo ^ data[offset + i]) & 0x00ff;
            ucCRCLo = ucCRCHi ^ crc16_tab_h[iIndex];
            ucCRCHi = crc16_tab_l[iIndex];
        }
        return ((ucCRCHi & 0x00ff) << 8) | (ucCRCLo & 0x00ff) & 0xffff;
    }

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
        GetCRC();
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

    @Click
    void tvEdit() {
        if (Objects.equals(tvEdit.getText().toString(), "编辑")) {
            mBaseAdapter = new SimpleAdapter(this, mAppList, R.layout.listview_item_staff_edit, new String[]{"tvContext", "tvEngContext", "icon"}, new int[]{R.id.tv_content, R.id.tv_eng_content, R.id.img_icon});
            lvContext.setDivider(null);
            lvContext.setAdapter(mBaseAdapter);
            lvContext.setEnabled(true);
            mBaseAdapter.notifyDataSetChanged();
            tvEdit.setText("退出编辑");
            lvContext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    ImageView ivDelete = (ImageView) view.findViewById(R.id.edit_icon);
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
                    mStaffList = (ArrayList<EmployeeInformation>) employeeInfoDao.queryForAll();
                } catch (SQLException e) {
                    Toast.makeText(this, "数据保存失败" + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                mBaseAdapter.notifyDataSetChanged();
                break;
            case 2:
                try {
                    employeeInfoDao = sqLiteOpenHelper.getDao(EmployeeInformation.class);
                    employeeInfoDao.update(Custom);
                    staffDialog.dismiss();
                    Toast.makeText(this, "数据更新成功", Toast.LENGTH_SHORT).show();
                    mStaffList = (ArrayList<EmployeeInformation>) employeeInfoDao.queryForAll();
                } catch (SQLException e) {
                    Toast.makeText(this, "数据保存失败" + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                mBaseAdapter.notifyDataSetChanged();
                break;
            case 3:
                try {
                    employeeInfoDao = sqLiteOpenHelper.getDao(EmployeeInformation.class);
                    employeeInfoDao.delete(Custom);
                    staffDialog.dismiss();
                    mStaffList = (ArrayList<EmployeeInformation>) employeeInfoDao.queryForAll();

                    Toast.makeText(this, "数据删除成功", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    Toast.makeText(this, "数据删除失败" + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                mBaseAdapter.notifyDataSetChanged();
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

    int GetCRC1() {
        // 0x02 05 00 03 FF 00 , crc16=7C 09
        int crc = calcCrc16(new byte[]{(byte) 0x01, (byte) 0x20, (byte) 0x00, (byte) 0x00, (byte) 0x01});
        System.out.println(String.format("打印" + "0x%04x", crc));
        logger.error("打印：{}", crc);
        return crc;
    }

    void GetCRC() {
        byte[] data = new byte[]{(byte) 0x01, (byte) 0x20, (byte) 0x00, (byte) 0x00, (byte) 0x01};
        int i;
        int crc = (~data[1] << 8) | (~data[0] & 0xFF);
        for (i = 2; i < data.length; i++) {
            crc = calcCrc16(new byte[]{data[i], (byte) crc});
        }
        crc = calcCrc16(new byte[]{0x00, (byte) crc});
        crc = calcCrc16(new byte[]{0x00, (byte) crc});
        crc = ~crc;
        crc = crc >> 8 | crc << 8;
        System.out.println(String.format("打印" + "0x%04x", crc));
//        return crc;
    }

}
