package org.daubin.adafriuit;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiDevice;

public class ILI9341Builder {
           
    private Pin dc = RaspiPin.GPIO_18;
    private Pin rst;
    
    private GpioController gpioController = GpioFactory.getInstance();
    
    private int width = ILI9341.ILI9341_TFTWIDTH;
    private int height = ILI9341.ILI9341_TFTHEIGHT;
    
    private int rotation = 0;
    
    private ILI9341Builder() {}
    
    /**
     * Creates a new {@link ILI9341} instance.
     */
    public ILI9341 build(SpiDevice spiDevice) {
        return new ILI9341(dc, rst, spiDevice, gpioController, width, height, rotation);
    }

    public static ILI9341Builder newBuilder() {
        return new ILI9341Builder();
    }

    public ILI9341Builder setDcPin(Pin dc) {
        this.dc = dc;
        return this;
    }

    public ILI9341Builder setResetPin(Pin rst) {
        this.rst = rst;
        return this;
    }

    public ILI9341Builder setRotation(int rotation) {
        this.rotation = rotation;
        return this;
    }

    public ILI9341Builder setGpioController(GpioController gpioController) {
        this.gpioController = gpioController;
        return this;
    }

    public ILI9341Builder setWidth(int width) {
        this.width = width;
        return this;
    }

    public ILI9341Builder setHeight(int height) {
        this.height = height;
        return this;
    }
   
}
