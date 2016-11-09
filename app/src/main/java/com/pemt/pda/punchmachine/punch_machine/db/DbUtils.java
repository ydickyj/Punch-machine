/**
 * copyright© www.pemt.com.cn
 * create time: 14-7-7
 */
package com.pemt.pda.punchmachine.punch_machine.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author hocking
 */
public class DbUtils {
    /**
     * 执行sql语句
     *
     * @param context       android context
     * @param db            database
     * @param assetFilename sql脚本文件，在asset中的路径
     * @param cancelIfFail  当有脚本执行失败时是否继续执行
     * @return 执行成功的语句
     * @throws IOException 如果asset文件打开出错
     */
    public static int executeSqlScript(Context context, SQLiteDatabase db, String assetFilename, boolean cancelIfFail)
            throws IOException {
        int count = 0;
        InputStream in = context.getResources().getAssets().open(assetFilename);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int byteRead;
            byte[] buffer = new byte[1024];
            while ((byteRead = in.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteRead);
            }
            String sql = new String(outputStream.toByteArray(), "UTF-8");
            for (String line : sql.split(";(\\s)*[\n\r]")) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        db.execSQL(line);
                        count++;
                    } catch (SQLException e) {
                        if (cancelIfFail) {
                            throw e;
                        }
                    }
                }
            }
        } finally {
            in.close();
        }
        return count;
    }

}
