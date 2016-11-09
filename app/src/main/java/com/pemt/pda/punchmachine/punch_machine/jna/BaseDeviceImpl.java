/*
 * Copyright PEMT Limited © 2012-2016, All rights Reserved. ShenZhen Pioneers Electrical Measurement Technology CO., LTD
 */
package com.pemt.pda.punchmachine.punch_machine.jna;




import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.pemt.pda.jna.CLibrary;
import com.pemt.pda.jna.Termios;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @author hocking
 */
public abstract class BaseDeviceImpl implements DeviceInterface {

    private static Logger logger = LoggerFactory.getLogger(BaseDeviceImpl.class);

    private int parity = Constants.PARITY_E;
    private int baudrate = 9600;
    private int fd = -1;
    private InputStream inputStream;
    private OutputStream outputStream;

    public abstract String getName();

    protected abstract int getFd() throws IOException;

    protected abstract void openDevice() throws IOException;

    protected abstract void closeDevice(int fd);

    @Override
    public synchronized void open() throws IOException {
        openDevice();
        fd = getFd();
        if (fd != -1) {
            cfmakeraw();
            setParity(parity);
            FileDescriptor fileDescriptor = FileUtils.getFileDescriptor(fd);
            inputStream = new FileInputStream(fileDescriptor);
            outputStream = new FileOutputStream(fileDescriptor);
        } else {
            throw new IOException("cannot open uart.");
        }
    }

    void cfmakeraw() {
        if (fd != -1) {
            Termios.ByReference termios = new Termios.ByReference();
            CLibrary.INSTANCE.tcgetattr(fd, termios);
           CLibrary.INSTANCE.cfmakeraw(termios);
            termios.c_cflag &= ~Termios.CSTOPB;
            int b = Termios.B9600;
            switch (getBaudrate()) {
                case 300:
                    b = Termios.B300;
                    break;
                case 600:
                    b = Termios.B600;
                    break;
                case 1200:
                    b = Termios.B1200;
                    break;
                case 2400:
                    b = Termios.B2400;
                    break;
                case 4800:
                    b = Termios.B4800;
                    break;
                case 9600:
                    b = Termios.B9600;
                    break;
                case 19200:
                    b = Termios.B19200;
                    break;
                case 38400:
                    b = Termios.B38400;
                    break;
                case 57600:
                    b = Termios.B57600;
                    break;
                case 115200:
                    b = Termios.B115200;
                    break;
                case 230400:
                    b = Termios.B230400;
                    break;
                case 460800:
                    b = Termios.B460800;
                    break;
                case 500000:
                    b = Termios.B500000;
                    break;
                case 576000:
                    b = Termios.B576000;
                    break;
                case 921600:
                    b = Termios.B921600;
                    break;
                default:
                    b = Termios.B9600;
                    break;
            }
            CLibrary.INSTANCE.cfsetispeed(termios, b);
          CLibrary.INSTANCE.cfsetospeed(termios, b);
           CLibrary.INSTANCE.tcsetattr(fd, Termios.TCSANOW, termios);
        }
    }

    @Override
    public int read() throws IOException {
        InputStream in = inputStream;
        if (in == null) {
            throw new IOException("stream closed.");
        }
        return inputStream.read();
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        InputStream in = inputStream;
        if (in == null) {
            throw new IOException("stream closed.");
        }
        return inputStream.read(buffer);
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        InputStream in = inputStream;
        if (in == null) {
            throw new IOException("stream closed.");
        }
        return inputStream.read(buffer, offset, length);
    }

    @Override
    public synchronized void write(int b) throws IOException {
        if (outputStream == null) {
            throw new IOException("stream closed.");
        }
        outputStream.write(b);
    }

    @Override
    public synchronized void write(byte[] buffer) throws IOException {
        if (outputStream == null) {
            throw new IOException("stream closed.");
        }
        outputStream.write(buffer);
    }

    @Override
    public synchronized void write(byte[] buffer, int offset, int count) throws IOException {
        if (outputStream == null) {
            throw new IOException("stream closed.");
        }
        outputStream.write(buffer, offset, count);
    }

    @Override
    public synchronized void close() {
        if (fd != -1) {
            try {
                outputStream.close();
            } catch (Exception e) {
                logger.warn("", e);
            } finally {
                outputStream = null;
            }
            try {
                inputStream.close();
            } catch (Exception e) {
                logger.warn("", e);
            } finally {
                inputStream = null;
            }
            closeDevice(fd);
            fd = -1;
        }
    }

    public synchronized int getParity() {
        return parity;
    }

    /**
     * 设置奇偶校验
     *
     * @param parity 1表示无校验,2表示奇校验,3表示偶校验
     */
    public synchronized void setParity(int parity) {
        this.parity = parity;
        if (fd != -1) {
            Termios.ByReference termios = new Termios.ByReference();
           CLibrary.INSTANCE.tcgetattr(fd, termios);
            switch (parity) {
                case 1://N
                    termios.c_cflag &= ~Termios.PARENB;
                    termios.c_cflag |= Termios.PARODD;
                    termios.c_iflag &= ~Termios.INPCK;
                    break;
                case 2://O
                    termios.c_cflag |= Termios.PARENB;
                    termios.c_cflag |= Termios.PARODD;
                    termios.c_iflag |= Termios.INPCK;
                    break;
                default://E
                    termios.c_cflag |= Termios.PARENB;
                    termios.c_cflag &= ~Termios.PARODD;
                    termios.c_iflag |= Termios.INPCK;
                    break;
            }
            CLibrary.INSTANCE.tcsetattr(fd, Termios.TCSANOW, termios);
        }
    }

    public int getBaudrate() {
        return baudrate;
    }

    public void setBaudrate(int baudrate) {
        this.baudrate = baudrate;
    }
}
