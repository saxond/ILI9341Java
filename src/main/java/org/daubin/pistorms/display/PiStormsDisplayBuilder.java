package org.daubin.pistorms.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import org.daubin.adafriuit.ComponentDisplayAdapter;
import org.daubin.adafriuit.Display;
import org.daubin.adafriuit.DisplayRepaintManager;
import org.daubin.adafriuit.ILI9341;
import org.daubin.adafriuit.ILI9341Builder;
import org.daubin.adafriuit.NotifyingTouchScreen;
import org.daubin.adafriuit.TouchScreenBuilder;
import org.daubin.adafriuit.image.ImageRotation;

import com.google.common.eventbus.EventBus;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.io.spi.SpiMode;

public class PiStormsDisplayBuilder {
    private ImageRotation rotation = ImageRotation.LEFT_90;

    private PiStormsDisplayBuilder() {}
    
    public static PiStormsDisplayBuilder newBuilder() {
        return new PiStormsDisplayBuilder();
    }
    
    public PiStormsDisplayBuilder setImageRotation(ImageRotation rotation) {
        this.rotation = rotation;
        return this;
    }

    public PiStormsDisplay build() throws IOException {

        // Create LCD display class.
        SpiDevice spiDevice = SpiFactory.getInstance(
                SpiChannel.CS0, 64000000, SpiMode.MODE_0);
        
        final I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);

        I2CDevice i2cDevice = bus.getDevice(0x34 >> 1); 

        EventBus eventBus = new EventBus();
        
        ILI9341 display = ILI9341Builder.newBuilder().
                // wiring pin 5, BCM pin 24, physical pin 18
                setDcPin(RaspiPin.GPIO_05).
                setResetPin(RaspiPin.GPIO_06).
                setImageRotation(rotation).
                setEventBus(eventBus).
                build(spiDevice);
        
        NotifyingTouchScreen touchScreen = new NotifyingTouchScreen(
            TouchScreenBuilder.newBuilder().setImageRotation(rotation).createTouchScreen(i2cDevice),
            eventBus, 100, TimeUnit.MILLISECONDS);
        
        display.clear(Color.BLACK);
        
        BufferedImage bufferedImage = display.getBufferedImage();
        final Graphics graphics = bufferedImage.createGraphics();

        JPanel panel = new JPanel();
        panel.setSize(display.getDimension());
        new ComponentDisplayAdapter(panel, graphics, eventBus);
        
        final PiStormsDisplayImpl displayImpl = new PiStormsDisplayImpl(touchScreen, panel, display, eventBus,
                new DisplayRepaintManager(display, eventBus, 100, TimeUnit.MILLISECONDS));
        
        Runtime.getRuntime().addShutdownHook(new Thread(
            new Runnable() {
                
                @Override
                public void run() {
                    try {
                        displayImpl.close();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }));
        
        return displayImpl;
    }
    
    private static final class PiStormsDisplayImpl implements AutoCloseable, PiStormsDisplay {
        private final NotifyingTouchScreen touchScreen;
        private final JPanel panel;
        private final ILI9341 display;
        private final EventBus eventBus;
        private final DisplayRepaintManager displayRepaintManager;
        
        public PiStormsDisplayImpl(NotifyingTouchScreen touchScreen, JPanel panel, ILI9341 display, EventBus eventBus, DisplayRepaintManager displayRepaintManager) {
            this.touchScreen = touchScreen;
            this.panel = panel;
            this.display = display;
            this.eventBus = eventBus;
            this.displayRepaintManager = displayRepaintManager;
        }

        @Override
        public NotifyingTouchScreen getTouchScreen() {
            return touchScreen;
        }

        @Override
        public JPanel getPanel() {
            return panel;
        }

        @Override
        public Display getDisplay() {
            return display;
        }

        @Override
        public EventBus getEventBus() {
            return eventBus;
        }

        @Override
        public void close() throws Exception {
            display.clear(Color.BLACK);
            display.display();
            
            touchScreen.close();
            displayRepaintManager.close();
        }
    }
}
