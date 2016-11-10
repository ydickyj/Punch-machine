package com.pemt.pda.punchmachine.punch_machine.db.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 员工信息表
 */
@DatabaseTable(tableName = "T_APP_DATA_")
public class AppData implements Serializable {

    @DatabaseField(generatedId = true, columnName = "ID", dataType = DataType.INTEGER)
    private int id;

    @DatabaseField
    private String NAME;                //姓名

    @DatabaseField
    private String RECORD_TIME;        //记录时间

    @DatabaseField
    private String OFFICE_LOCATION;    //记录地点

    @DatabaseField
    private String DEPARTMENT;         //部门

    @DatabaseField
    private String JOB;                //职位


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getRECORD_TIME() {
        return RECORD_TIME;
    }

    public void setRECORD_TIME(String RECORD_TIME) {
        this.RECORD_TIME = RECORD_TIME;
    }

    public String getJOB() {
        return JOB;
    }

    public void setJOB(String JOB) {
        this.JOB = JOB;
    }

    public String getOFFICE_LOCATION() {
        return OFFICE_LOCATION;
    }

    public void setOFFICE_LOCATION(String OFFICE_LOCATION) {
        this.OFFICE_LOCATION = OFFICE_LOCATION;
    }

    public String getDEPARTMENT() {
        return DEPARTMENT;
    }

    public void setDEPARTMENT(String DEPARTMENT) {
        this.DEPARTMENT = DEPARTMENT;
    }
}
