package com.pemt.pda.punchmachine.punch_machine.db.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 员工信息表
 */
@DatabaseTable(tableName = "T_EMPLOYEE_INFORMATION")
public class EmployeeInformation implements Serializable {

    @DatabaseField(generatedId = true, columnName = "ID", dataType = DataType.INTEGER)
    private int id;

    @DatabaseField
    private String  NAME;         //姓名

    @DatabaseField
    private String  DEPARTMENT;          //部门

    @DatabaseField
    private String JOB_NUMBER;           //工号

    @DatabaseField
    private String  RFID_NO;            //RFID编号

    @DatabaseField
    private String  JOB;                 //职位


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

    public String getDEPARTMENT() {
        return DEPARTMENT;
    }

    public void setDEPARTMENT(String DEPARTMENT) {
        this.DEPARTMENT = DEPARTMENT;
    }

    public String getJOB_NUMBER() {
        return JOB_NUMBER;
    }

    public void setJOB_NUMBER(String JOB_NUMBER) {
        this.JOB_NUMBER = JOB_NUMBER;
    }

    public String getRFID_NO() {
        return RFID_NO;
    }

    public void setRFID_NO(String RFID_NO) {
        this.RFID_NO = RFID_NO;
    }

    public String getJOB() {
        return JOB;
    }

    public void setJOB(String JOB) {
        this.JOB = JOB;
    }
}
