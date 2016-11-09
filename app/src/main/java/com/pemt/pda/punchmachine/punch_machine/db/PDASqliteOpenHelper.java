package com.pemt.pda.punchmachine.punch_machine.db;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;

import com.j256.ormlite.android.DatabaseTableConfigUtil;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;
import com.pemt.pda.punchmachine.punch_machine.MainActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author yfn020
 */
public class PDASqliteOpenHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "/sdcard/PDA/punch-machine.db3";
    private static final int version = 1;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS";
    //统一时间格式
    //private static final String DATE_FORMAT = "yy-MM-dd HH:mm:ss";
    private static final String SQL_FILE = "sql/tb.sql";
    private static Logger logger = LoggerFactory.getLogger(PDASqliteOpenHelper.class);
    private static Map<String, Dao<?, ?>> daoMap = new HashMap<>();
    private final ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
        }
    };
    private final Context context;
    private List<UpgradeSql> upgradeSqls = new ArrayList<>();
    private List<UpgradeMethod> upgradeMethods = new ArrayList<>();
    private String allInOneSql = null;
    private Method allInOneMethod = null;

    public PDASqliteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
        this.context = context;
        try {
            for (String file : context.getAssets().list("sql"))
                try {
                    if (file.toLowerCase().matches("^\\d+.sql$")) {
                        upgradeSqls.add(new UpgradeSql(Integer.parseInt(file.replace(".sql", "")), "sql/" + file));
                    } else if (file.toLowerCase().equals(String.format(Locale.getDefault(), "0-%d.sql", version))) {
                        allInOneSql = "sql/" + file;
                    }
                } catch (Exception e) {
                    logger.warn("", e);
                }
        } catch (IOException e) {
            logger.error("", e);
        }
        //排序
        Collections.sort(upgradeSqls, new Comparator<UpgradeSql>() {
            @Override
            public int compare(UpgradeSql lhs, UpgradeSql rhs) {
                return lhs.ver - rhs.ver;
            }
        });

        for (Method method : getClass().getDeclaredMethods()) {
            try {
                if (method.getName().matches("^r\\d+$")) {
                    int ver = Integer.parseInt(method.getName().replace("r", ""));
                    upgradeMethods.add(new UpgradeMethod(ver, method));
                } else if (method.getName().equals(String.format(Locale.getDefault(), "r0_%d", version))) {
                    allInOneMethod = method;
                }
            } catch (Exception e) {
                logger.warn("", e);
            }
        }
        //排序
        Collections.sort(upgradeMethods, new Comparator<UpgradeMethod>() {
            @Override
            public int compare(UpgradeMethod lhs, UpgradeMethod rhs) {
                return lhs.ver - rhs.ver;
            }
        });
    }

    private static Dao<?, ?> lookupDao(String key) {
        if (daoMap == null) {
            daoMap = new HashMap<>();
        }
        Dao<?, ?> dao = daoMap.get(key);
        if (dao == null) {
            return null;
        } else {
            return dao;
        }
    }

    private Constructor<?> findConstructor(Class<?> daoClass, Object[] params) {
        for (Constructor<?> constructor : daoClass.getConstructors()) {
            Class<?>[] paramsTypes = constructor.getParameterTypes();
            if (paramsTypes.length == params.length) {
                boolean match = true;
                for (int i = 0; i < paramsTypes.length; i++) {
                    if (!paramsTypes[i].isAssignableFrom(params[i].getClass())) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return constructor;
                }
            }
        }
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        logger.debug("PDASqliteOpenHelper.onCreate");
        if (allInOneSql != null) {
            try {
                DbUtils.executeSqlScript(context, database, allInOneSql, false);
                if (allInOneMethod != null) {
                    allInOneMethod.invoke(this);
                } else {
                    for (UpgradeMethod method : upgradeMethods) {
                        if (method.ver > 0 && method.ver <= version) {
                            try {
                                method.method.invoke(this);
                            } catch (Exception e) {
                                logger.error("", e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        } else {
            onUpgrade(database, connectionSource, 0, version);
        }

        //注册DataPersister,否则开启混淆时崩溃
//        DataPersisterManager.registerDataPersisters(JsonDataType.getSingleton());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        logger.debug("PDASqliteOpenHelper.onUpgrade from <{}> to <{}>", oldVersion, newVersion);
        for (UpgradeSql sql : upgradeSqls) {
            if (sql.ver > oldVersion && sql.ver <= newVersion) {
                try {
                    DbUtils.executeSqlScript(context, database, sql.file, false);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
        if (allInOneMethod != null) {
            try {
                allInOneMethod.invoke(this);
            } catch (Exception e) {
                logger.error("", e);
            }
        } else {
            for (UpgradeMethod method : upgradeMethods) {
                if (method.ver > oldVersion && method.ver <= newVersion) {
                    try {
                        method.method.invoke(this);
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            }
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        logger.debug("PDASqliteOpenHelper.onDowngrade from <{}> to <{}>", oldVersion, newVersion);
        context.deleteDatabase(DATABASE_NAME);
        onCreate(database, getConnectionSource());
    }

    /**
     * @return 返回执行后语句受影响的行数。
     */
    public <T> int clearTable(Class<T> clazz) {
        try {
            return TableUtils.clearTable(connectionSource, clazz);
        } catch (SQLException ignored) {
        }
        return 0;
    }

    /**
     * @return 返回执行后语句受影响的行数。
     */
    public <T> int clearTable(Class<T> clazz, Calendar calendar) {
        try {
            DatabaseTableConfig<T> config = DatabaseTableConfigUtil.fromClass(connectionSource, clazz);
            DatabaseTable databaseTable = clazz.getAnnotation(DatabaseTable.class);
            String tableName = databaseTable.tableName() +
                    String.format(Locale.getDefault(), "%d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
            config.setTableName(tableName);
            return TableUtils.clearTable(connectionSource, clazz);
        } catch (SQLException e) {
            logger.error("", e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public synchronized <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {

        /* 检测数据库表名是否正确：从而判断传入的参数是否正确，是否为单表数据 */
        DatabaseTable databaseTable = clazz.getAnnotation(DatabaseTable.class);
        String tableName = databaseTable.tableName();

        if (tableName.endsWith("_")) {
            throw new SQLException(clazz.getName() + ",数据库单表表名不能以”_“结束：" + tableName);
        }
        /* 查找该数据库表对应的Dao是否已经生成过，如果有的话直接返回 */
        Dao<?, ?> dao = lookupDao(tableName);
        if (dao != null) {
            createTableIfNotExists(dao, tableName, "");
            return (D) dao;
        }
        logger.error("Dao没有生成过");
        /* 没有的话创建对应的Dao */
        dao = DaoManager.createDao(connectionSource, clazz);
        D castDao = (D) dao;

        createTableIfNotExists(castDao, tableName, "");
         /* 加入缓存队列 */
        daoMap.put(tableName, castDao);
        logger.error("返回castDao");
        return castDao;
    }

    /* 判断该Dao对应的数据库表是否已经存在，不存在的话创建数据库表 */
    private void createTableIfNotExists(Dao<?, ?> dao, String Prefix, String Suffix) throws SQLException {
        if (!dao.isTableExists()) {
            try {
                createSingleTable(context, SQL_FILE, Prefix, Suffix);
                logger.error("createSingleTable");
            } catch (IOException e) {
                logger.error("", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized <D extends Dao<T, ?>, T> D getDao(Class<T> clazz, Calendar calendar) throws SQLException {

        /* 检测数据库表名是否正确：从而判断传入的参数是否正确，是否为分表数据 */
        DatabaseTable databaseTable = clazz.getAnnotation(DatabaseTable.class);
        String prefix = databaseTable.tableName();
        if (!prefix.endsWith("_")) {
            throw new SQLException(clazz.getName() + ",数据库分表表名前缀应以”_“结束：" + prefix);
        }

        /* 根据时间确定分表，组成完整的表名 */
        String suffix = String.format(Locale.getDefault(),
                "%d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        String tableName = prefix + suffix;
        logger.error("tableName:{}", tableName);
        /* 查找该数据库表对应的Dao是否已经生成过，如果有的话直接返回 */
        Dao<?, ?> dao = lookupDao(tableName);
        if (dao != null) {
            createTableIfNotExists(dao, prefix, suffix);
            return (D) dao;
        }

        /* 没有的话设置的数据库表名，创建对应的Dao */
        DatabaseTableConfig<T> config = DatabaseTableConfigUtil.fromClass(connectionSource, clazz);
        config.setTableName(tableName);
        dao = createDao(config);
        DaoManager.registerDaoWithTableConfig(connectionSource, dao);

        /* 加入缓存队列 */
        createTableIfNotExists(dao, prefix, suffix);
        daoMap.put(tableName, dao);
        return (D) dao;
    }

    @SuppressWarnings("unchecked")
    private <T, ID> Dao<T, ID> createDao(DatabaseTableConfig<T> tableConfig) throws SQLException {

        /* 检测DatabaseTableConfig是否为null，null则抛出异常 */
        if (tableConfig == null) {
            throw new SQLException("createDao：tableConfig is null");
        }

        Dao<T, ?> dao;
        DatabaseTable databaseTable = tableConfig.getDataClass().getAnnotation(DatabaseTable.class);
        Class<?> daoClass = databaseTable.daoClass();

        /* 获取配置的注解的daoClass，如果没有配置，则用默认的Dao函数*/
        if (daoClass == Void.class) {
            dao = new OnsiteDao<T, ID>(connectionSource, tableConfig);
            return (Dao<T, ID>) dao;
        }

         /* 如果注解配置了daoClass，调用注解制定的daoClass */
        Object[] arguments = new Object[]{connectionSource, tableConfig};
        Constructor<?> constructor = findConstructor(daoClass, arguments);
        /* 注解配置了daoClass没有找到构成正确的函数，抛出异常 */
        if (constructor == null) {
            throw new SQLException(
                    "Could not find public constructor with ConnectionSource, DatabaseTableConfig parameters in class "
                            + daoClass);
        }
        /* 用注解配置的daoClass的构造函数创建Dao */
        try {
            dao = (Dao<T, ?>) constructor.newInstance(arguments);
        } catch (Exception e) {
            throw SqlExceptionUtil.create("Could not call the constructor in class " + daoClass, e);
        }
        return (Dao<T, ID>) dao;
    }

    /**
     * 把记录转换为java对象
     *
     * @param cursor 游标
     * @param column 列号
     * @return 返回java对象
     */
    private Object convert(Cursor cursor, int column) {
        switch (cursor.getType(column)) {
            case Cursor.FIELD_TYPE_INTEGER:
                long aLong = cursor.getLong(column);
                if (aLong > Integer.MAX_VALUE || aLong < Integer.MIN_VALUE) {
                    return aLong;
                } else {
                    return (int) aLong;
                }
            case Cursor.FIELD_TYPE_FLOAT:
                return cursor.getDouble(column);
            case Cursor.FIELD_TYPE_STRING:
                String string = cursor.getString(column);
                //"yyyy-MM-dd HH:mm:ss.SSSSSS"
                if (string != null && string.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{6}")) {
                    try {
                        return threadLocal.get().parse(string);
                    } catch (Exception ignored) {
                    }
                }
                return string;
            case Cursor.FIELD_TYPE_BLOB:
                return cursor.getBlob(column);
            case Cursor.FIELD_TYPE_NULL:
            default:
                return null;
        }
    }

    /**
     * 执行指定的sql查询语句,将结果通过回调接口通知结果
     *
     * @param sql      查询语句
     * @param callback 回调接口
     */
    public void query(String sql, QueryCallback callback) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, null);
            if (callback == null) {
                return;
            }
            int columnCount = cursor.getColumnCount();
            int rowCount = cursor.getCount();
            String nameArray[] = cursor.getColumnNames();
            while (cursor.moveToNext()) {
                Map<String, Object> hashMap = new HashMap<>();
                for (int i = 0; i < columnCount; i++) {
                    hashMap.put(nameArray[i], convert(cursor, i));
                }
                if (!callback.newRow(hashMap, rowCount)) {
                    return;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 查询指定的sql语句,返回第一行记录
     *
     * @param sql 指定的查询语句
     * @return 返回结果Map
     */
    public Map<String, Object> queryForFirst(String sql) {
        Map<String, Object> map = new HashMap<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, null);
            if (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    map.put(cursor.getColumnName(i), convert(cursor, i));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return map;
    }

    /**
     * 查询指定表名的SQL创建语句，然后创建该数据库表
     *
     * @param tableNamePrefix 指定的表的前缀
     */
    private void createSingleTable(Context context, String sqlFilename,
                                   String tableNamePrefix, String tableNameSuffix) throws IOException {
        String createTableSql = String.format("Create Table If Not Exists %s", tableNamePrefix);
        InputStream in = context.getResources().getAssets().open(sqlFilename);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int byteRead;
            byte[] buffer = new byte[1024];
            while ((byteRead = in.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteRead);
            }
            String sql = new String(outputStream.toByteArray(), UTF_8.name());
            for (String line : sql.split(";(\\s)*[\n\r]")) {
                line = line.trim();
                boolean iscreateTableSql = line.contains(createTableSql);
                if (iscreateTableSql) {
                    String newSql = line.replace(tableNamePrefix, tableNamePrefix + tableNameSuffix);
                    logger.info("{}", line);
                    getWritableDatabase().execSQL(newSql);
                    break;
                }
            }
        } finally {
            in.close();
        }
    }

    /**
     * 查询回调接口
     */
    interface QueryCallback {

        /**
         * 回调函数
         *
         * @param map  单row结果
         * @param size 全部记录数
         * @return true表示继续执行查询回调, false表示取消继续回调
         */
        boolean newRow(Map<String, Object> map, int size);
    }

    private class UpgradeSql {
        int ver;
        String file;

        UpgradeSql(int ver, String file) {
            this.ver = ver;
            this.file = file;
        }
    }

    private class UpgradeMethod {
        int ver;
        Method method;

        UpgradeMethod(int ver, Method method) {
            this.ver = ver;
            this.method = method;
        }
    }

    /**
     * 执行指定的sql查询语句,查询所有表名
     */
    public ArrayList<String> queryAllTableName() {
        ArrayList<String> listTableName = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("select name from sqlite_master where type='table' order by name", null);
            while (cursor.moveToNext()) {
                //遍历出表名
                String name = cursor.getString(0);

                logger.error("System.out:{}", name);
                listTableName.add(name);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return listTableName;
    }

    public void ExportToCSV(String tableName, String fileName) {
        Cursor cursor = null;
        int rowCount = 0;
        int colCount = 0;
        FileWriter fw;
        BufferedWriter bfw;
        File sdCardDir = new File(Environment.getExternalStorageDirectory().getPath()+"/storage/emulated/0");
        File saveFile = new File(sdCardDir, fileName);
        try {
            cursor = getReadableDatabase().rawQuery("select * from " + tableName, null);
            rowCount = cursor.getCount();
            colCount = cursor.getColumnCount();
            fw = new FileWriter(saveFile);
            bfw = new BufferedWriter(fw);
            if (rowCount > 0) {
                cursor.moveToFirst();
                // 写入表头
                for (int i = 0; i < colCount; i++) {
                    if (i != colCount - 1)
                        bfw.write(cursor.getColumnName(i) + ',');
                    else
                        bfw.write(cursor.getColumnName(i));
                }
                // 写好表头后换行
                bfw.newLine();
                // 写入数据
                for (int i = 0; i < rowCount; i++) {
                    cursor.moveToPosition(i);
                    // Toast.makeText(mContext, "正在导出第"+(i+1)+"条",
                    // Toast.LENGTH_SHORT).show();
                    logger.error("导出数据:{}", "正在导出第" + (i + 1) + "条");
                    for (int j = 0; j < colCount; j++) {
                        if (j != colCount - 1)
                            bfw.write(cursor.getString(j) + ',');
                        else
                            bfw.write(cursor.getString(j));
                    }
                    // 写好每条记录后换行
                    bfw.newLine();
                }
            }
            // 将缓存数据写入文件
            bfw.flush();
            // 释放缓存
            bfw.close();
            // Toast.makeText(mContext, "导出完毕！", Toast.LENGTH_SHORT).show();
            logger.error("导出数据:{}", "导出完毕！");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            assert cursor != null;
            cursor.close();
        }


    }
}
