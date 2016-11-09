/**
 * Copyright PEMT Limited © 2012-2016, All rights Reserved.
 * ShenZhen Pioneers Electrical Measurement Technology CO., LTD
 * create time: 5/22/16
 */
package com.pemt.pda.punchmachine.punch_machine.jna;





import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.pemt.pda.jna.CLibrary;
import com.pemt.pda.jna.Fcntl;
import com.pemt.pda.jna.GPIOController;
import com.pemt.pda.jna.GPIOPin;

import java.io.IOException;


/**
 * @author hocking
 */
public class RFIDDevice extends BaseDeviceImpl {

    private static final Logger logger = LoggerFactory.getLogger(RFIDDevice.class);

    private GPIOController.GPIO rfidPower;
    private GPIOController.GPIO rfidCE;

    public RFIDDevice() {
        this(38400);
    }

    public RFIDDevice(int baudrate) {
        rfidPower = GPIOController.getInstance().getGPIO(GPIOPin.RFID_PWR);
        rfidCE = GPIOController.getInstance().getGPIO(GPIOPin.RFID_CE);
        setBaudrate(baudrate);
        setParity(Constants.PARITY_N);
    }

    @Override
    public String getName() {
        return "RFID";
    }

    @Override
    public int getFd() throws IOException {
        return CLibrary.INSTANCE.open("/dev/ttyp1",
                Fcntl.O_RDWR | Fcntl.O_NOCTTY | Fcntl.O_NONBLOCK);
    }

    @Override
    public void openDevice() throws IOException {
        rfidCE.setHigh(true);
        rfidPower.setHigh(true);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }


    @Override
    public void closeDevice(int fd) {
        CLibrary.INSTANCE.close(fd);
        rfidPower.setHigh(false);
        rfidCE.setHigh(false);
    }
}
