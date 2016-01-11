package org.daubin.adafriuit;

import java.io.IOException;

import com.pi4j.io.i2c.I2CDevice;

class TouchScreenImpl implements TouchScreen {

    private final I2CDevice i2cDevice;

    public TouchScreenImpl(I2CDevice i2cDevice) {
        this.i2cDevice = i2cDevice;
    }

    // Touch screen Y-axis Register 
    private static final int PS_TSY = 0xE3;
    // Touch screen X-axis Register 
    private static final int PS_TSX = 0xE5;
    
    /**
     * Reads the x-coordinate of the touch screen press.
     * @throws IOException 
     */
    @Override
    public int getTouchX() throws IOException {
        return readShort(PS_TSX);
    }
    
    /**
     * Reads the x-coordinate of the touch screen press.
     * @throws IOException 
     */
    @Override
    public int getTouchY() throws IOException {
        return readShort(PS_TSY);
    }

    private int readShort(int address) throws IOException {
        byte[] bytes = new byte[Short.BYTES];
        int bytesRead = i2cDevice.read(address, bytes, 0, bytes.length);
        if (bytesRead < 0) {
            return -1;
        } else {
            return toInt(bytes, bytesRead);
        }
    }
    
    static int toInt(byte[] bytes, int length) {
        int val = 0;
        for (int i = 0; i < length; i++) {
            val += (bytes[i]&0xff) << (i*8);
        }
        return val;
    }
}
