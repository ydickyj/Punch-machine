

--APP打卡数据记录
Create Table If Not Exists T_APP_DATA_ (
	ID                  INTEGER PRIMARY KEY AUTOINCREMENT,
	NAME                VARCHAR,    --姓名
	OFFICE_LOCATION     VARCHAR,    --办公地点
	RECORD_TIME			VARCHAR, 	--记录时间
    DEPARTMENT          VARCHAR,    --部门
    JOB                 VARCHAR     --职位
);

--员工信息对比表
Create Table If Not Exists T_EMPLOYEE_INFORMATION(
    ID                  INTEGER PRIMARY KEY AUTOINCREMENT,
	NAME                VARCHAR,    --姓名
    DEPARTMENT          VARCHAR,	--部门
    JOB_NUMBER          VARCHAR,	--工号
    JOB                 VARCHAR,    --职位
    RFID_NO             VARCHAR     --RFID编号

)

