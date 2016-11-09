/**
 * copyright© www.pemt.com.cn
 * create time: 14-2-20
 */
package com.pemt.pda.punchmachine.punch_machine.jna;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author hocking
 */
public class Utils {

    public static String toHex(byte data[], int offset, int len) {
        StringBuilder sb = new StringBuilder();
        if (len + offset > data.length) {
            len = data.length - offset;
        }
        for (int i = offset; i < len + offset; i++) {
            sb.append(String.format("%02X", data[i] & 0xff)).append(" ");
        }
        return sb.toString();
    }

    public static String toHexNoSpace(byte data[], int offset, int len) {
        StringBuilder sb = new StringBuilder();
        if (len + offset > data.length) {
            len = data.length - offset;
        }
        for (int i = offset; i < len + offset; i++) {
            sb.append(String.format("%02x", data[i] & 0xff));
        }
        return sb.toString();
    }

    public static byte[] fromHex(String hexString) throws NumberFormatException {
        String s[] = hexString.split(" ");
        byte ret[] = new byte[s.length];
        for (int i = 0; i < s.length; i++) {
            ret[i] = (byte) Integer.parseInt(s[i], 16);
        }
        return ret;
    }

    public static int checksum(byte data[], int offset, int len) {
        int cs = 0;
        if (len + offset > data.length) {
            len = data.length - offset;
        }
        for (int i = offset; i < offset + len; i++) {
            cs += data[i];
        }
        return cs & 0xff;
    }

    public static String md5(File file) throws IOException {
        MessageDigest digest = null;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
        in = new FileInputStream(file);
        while ((len = in.read(buffer, 0, 1024)) != -1) {
            digest.update(buffer, 0, len);
        }
        in.close();
        byte[] md5 = digest.digest();
        return toHexNoSpace(md5, 0, md5.length);
    }

    /**
     * 低位在前，高位在后，将4个字节数据value添加到数组data的offset索引处
     */
    public static void setInt(byte data[], int offset, int value) {
        data[offset] = (byte) (value & 0xff);
        data[offset + 1] = (byte) ((value >> 8) & 0xff);
        data[offset + 2] = (byte) ((value >> 16) & 0xff);
        data[offset + 3] = (byte) ((value >> 24) & 0xff);
    }

    /**
     * 低位在前，高位在后，将4个字节数据value从数组data的offset索引处读取出来
     */
    public static int getInt(byte data[], int offset) {
        return (data[offset] & 0xff) | ((data[offset + 1] & 0xff) << 8) | ((data[offset + 2] & 0xff) << 16)
                | ((data[offset + 3] & 0xff) << 24);
    }

    // 当前方法名
    public static String getMethodName() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        return traceElement.getMethodName();
    }

    // 当前行号
    public static int getLineNumber() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        return traceElement.getLineNumber();
    }

}
