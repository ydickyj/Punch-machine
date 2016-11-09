/**
 * copyright© www.pemt.com.cn
 * create time: 14-2-18
 */
package com.pemt.pda.punchmachine.punch_machine.jna;



import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.io.FileDescriptor;
import java.lang.reflect.Method;

/**
 * @author hocking
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 根据传入的整形fd返回{@link FileDescriptor}对象
     *
     * @param fd fd
     * @return {@link FileDescriptor}对象
     */
    public static FileDescriptor getFileDescriptor(int fd) {
        FileDescriptor descriptor = new FileDescriptor();
        for (Method method : FileDescriptor.class.getMethods()) {
            if (method.getName().contains("setInt")) {
                try {
                    method.invoke(descriptor, fd);
                } catch (Exception e) {
                    logger.warn("", e);
                    return null;
                }
                break;
            }
        }
        return descriptor;
    }
}
