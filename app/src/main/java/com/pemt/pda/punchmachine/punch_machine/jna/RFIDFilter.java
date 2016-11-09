/**
 * Copyright PEMT Limited © 2012-2016, All rights Reserved.
 * ShenZhen Pioneers Electrical Measurement Technology CO., LTD
 * create time: 5/22/16
 */
package com.pemt.pda.punchmachine.punch_machine.jna;



import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author hocking
 */
public class RFIDFilter {

    private static Logger logger = LoggerFactory.getLogger(RFIDFilter.class);

    private static RFIDFilter instance = new RFIDFilter();

    /**
     * 私有的构造函数确保单例模式
     */
    private RFIDFilter() {

    }

    /**
     * 单例模式
     *
     * @return null
     */
    public static RFIDFilter getInstance() {
        return instance;
    }

    public final byte[] filter(ByteBuffer in) {
        STATUS status = STATUS.WAIT4FIRST0A;
        // 循环处理，直至数据长度不够
        while (true) {
            switch (status) {
                case WAIT4FIRST0A:
                    for (int i = in.position(); i < in.limit(); i++) {
                        if ((in.get(i) & 0xff) == 0x0A) {
                            in.position(i);
                            status = STATUS.WAIT4FIRST0D;
                            break;
                        }
                    }
                case WAIT4FIRST0D:
                    if (in.remaining() < 3) {
                        return null;
                    }
                    for (int i = in.position() + 1; i < in.limit(); i++) {
                        if ((in.get(i) & 0xff) == 0x0A) {
                            in.position(in.position() + 1);
                            status = STATUS.WAIT4FIRST0A;
                            break;
                        } else if ((in.get(i) & 0xff) == 0x0D) {
                            if ((i + 1 < in.limit())) {
                                if (((in.get(i + 1) & 0xff) == 0x0A)) {
                                    try {
                                        return Arrays.copyOfRange(in.array(), in.position(), i + 2);
                                    } finally {
                                        in.position(i + 2);
                                        in.compact();
                                        in.flip();
                                    }
                                } else {
                                    in.position(in.position() + 1);
                                    status = STATUS.WAIT4FIRST0A;
                                    break;
                                }
                            } else {
                                return null;
                            }
                        }
                    }
                    if (status == STATUS.WAIT4FIRST0D) {
                        return null;
                    }
            }
        }
    }

    private enum STATUS {
        WAIT4FIRST0A, WAIT4FIRST0D
    }
}
