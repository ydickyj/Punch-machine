/**
 * copyright© www.pemt.com.cn
 * create time: 14-2-18
 */
package com.pemt.pda.punchmachine.punch_machine.jna;

import java.io.IOException;

/**
 * @author hocking
 */
public interface DeviceInterface {

    public void open() throws IOException;

    public int read() throws IOException;

    public int read(byte[] buffer) throws IOException;

    public int read(byte[] buffer, int offset, int length) throws IOException;

    public void write(int b) throws IOException;

    public void write(byte[] buffer) throws IOException;

    public void write(byte[] buffer, int offset, int count) throws IOException;

    public void close();
}
