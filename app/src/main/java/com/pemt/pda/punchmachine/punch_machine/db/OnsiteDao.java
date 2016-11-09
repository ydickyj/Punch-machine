package com.pemt.pda.punchmachine.punch_machine.db;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;

/**
 * 基础Dao，共有的Dao可以在此实现
 */
public class OnsiteDao<T, ID> extends BaseDaoImpl<T, ID> {


    public OnsiteDao(Class<T> dataClass) throws SQLException {
        super(dataClass);
    }

    public OnsiteDao(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public OnsiteDao(ConnectionSource connectionSource, DatabaseTableConfig<T> tableConfig) throws SQLException {
        super(connectionSource, tableConfig);
    }

    public T queryForAppNo(String appNo) throws SQLException {
        return queryBuilder().where().eq("APP_NO", appNo).queryForFirst();
    }

}