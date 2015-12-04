package org.daubin.adafriuit;

import java.awt.image.BufferedImage;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiDevice;

public class ILI9341Builder {
           
    private Pin dc = RaspiPin.GPIO_18;
    private Pin rst;
    
    private GpioController gpioController = GpioFactory.getInstance();
    
    private BufferedImage image;
    
    private ILI9341Builder() {}
    
    /**
     * Creates a new {@link ILI9341} instance.
     */
    public ILI9341 build(SpiDevice spiDevice) {
        if (null == image) {
            image = new BufferedImage(ILI9341.ILI9341_TFTWIDTH, ILI9341.ILI9341_TFTHEIGHT, BufferedImage.TYPE_USHORT_565_RGB);
        }
        
        GpioPinDigitalOutput dcPin = gpioController.provisionDigitalOutputPin(dc, "dc", PinState.HIGH);        
        GpioPinDigitalOutput resetPin = rst == null ? null : gpioController.provisionDigitalOutputPin(rst, "reset", PinState.HIGH);
        
        return new ILI9341(dcPin, resetPin, spiDevice, image);
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

    public ILI9341Builder setGpioController(GpioController gpioController) {
        this.gpioController = gpioController;
        return this;
    }
   
}
