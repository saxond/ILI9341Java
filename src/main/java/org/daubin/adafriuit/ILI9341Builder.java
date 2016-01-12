package org.daubin.adafriuit;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.daubin.adafriuit.image.ImageRotation;

import com.google.common.eventbus.EventBus;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiDevice;

public class ILI9341Builder {
           
    private Pin dc = RaspiPin.GPIO_05;
    private Pin rst;
    private ImageRotation imageRotation = ImageRotation.NONE;
    
    private GpioController gpioController = GpioFactory.getInstance();
    
    private BufferedImage image;
    private EventBus eventBus;
    
    private ILI9341Builder() {}
    
    /**
     * Creates a new {@link ILI9341} instance.
     * @throws IOException 
     */
    public ILI9341 build(SpiDevice spiDevice) throws IOException {
        if (null == image) {
            Dimension dimension = imageRotation.getDimension(ILI9341.ILI9341_TFTWIDTH, ILI9341.ILI9341_TFTHEIGHT);
            image = new BufferedImage((int)dimension.getWidth(), (int)dimension.getHeight(), BufferedImage.TYPE_USHORT_565_RGB);
        }
        
        GpioPinDigitalOutput dcPin = gpioController.provisionDigitalOutputPin(dc, "dc", PinState.HIGH);
        GpioPinDigitalOutput resetPin = rst == null ? null : gpioController.provisionDigitalOutputPin(rst, "reset", PinState.HIGH);
        
        ILI9341 display = new ILI9341(dcPin, resetPin, spiDevice, image, imageRotation);
        if (null != eventBus) {
            eventBus.register(display);
        }
        return display;
    }

    public static ILI9341Builder newBuilder() {
        return new ILI9341Builder();
    }

    /**
     * Set the DC pin.  Remember that p4j uses the wiring pin numbers, not the BCI or 
     * physical pins.
     * @param dc
     * @return
     */
    public ILI9341Builder setDcPin(Pin dc) {
        this.dc = dc;
        return this;
    }

    public ILI9341Builder setResetPin(Pin rst) {
        this.rst = rst;
        return this;
    }

    public ILI9341Builder setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        return this;
    }

    public ILI9341Builder setImageRotation(ImageRotation imageRotation) {
        this.imageRotation = imageRotation;
        return this;
    }

    public ILI9341Builder setGpioController(GpioController gpioController) {
        this.gpioController = gpioController;
        return this;
    }
   
}
