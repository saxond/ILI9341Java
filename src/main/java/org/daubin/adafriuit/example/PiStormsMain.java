package org.daubin.adafriuit.example;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import org.daubin.adafriuit.ILI9341;
import org.daubin.adafriuit.ILI9341Builder;
import org.daubin.adafriuit.NotifyingTouchScreen;
import org.daubin.adafriuit.ShapeTouchEvent;
import org.daubin.adafriuit.TouchScreenBuilder;
import org.daubin.adafriuit.image.ImageRotation;

import com.google.common.base.Predicate;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.io.spi.SpiMode;

public class PiStormsMain {
    
    private final NotifyingTouchScreen touchScreen;

    public PiStormsMain(String text, ImageRotation rotation) throws Exception {

        // Create LCD display class.
        SpiDevice spiDevice = SpiFactory.getInstance(
                SpiChannel.CS0, 64000000, SpiMode.MODE_0);
        
        final I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);

        I2CDevice i2cDevice = bus.getDevice(0x34 >> 1); 

        // http://pinout.xyz/pinout/pin18_gpio24
        ILI9341 disp = ILI9341Builder.newBuilder().
                // wiring pin 5, BCM pin 24, physical pin 18
                setDcPin(RaspiPin.GPIO_05).
                setResetPin(RaspiPin.GPIO_06).
                setImageRotation(rotation).
                build(spiDevice);
        
        EventBus eventBus = new EventBus();
        touchScreen = new NotifyingTouchScreen(
        TouchScreenBuilder.newBuilder().setImageRotation(rotation).createTouchScreen(i2cDevice),
        eventBus, 100, TimeUnit.MILLISECONDS);

        System.err.println("After begin");
        
        disp.clear(Color.BLACK);
        
        BufferedImage bufferedImage = disp.getBufferedImage();
        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial Black", Font.BOLD, 20));
        graphics.drawString(text, 20, 40);
        
//        Canvas canvas = new Canvas();
//        canvas.setBackground(Color.BLACK);        
//        Button button = new Button("Btn 1");
//        
//        canvas.paint(graphics);
        
        Rectangle button1 = new Rectangle(20, 60, 40, 40);
        Rectangle button2 = new Rectangle(80, 140, 60, 40);
        
        graphics.fillRect(button1.x, button1.y, button1.width, button1.height);
        graphics.fillRect(button2.x, button2.y, button2.width, button2.height);
        
        disp.display();
        
        touchScreen.listenToShape(button1, getButtonHandler("Button 1"));
        touchScreen.listenToShape(button2, getButtonHandler("Button 2"));
        
        eventBus.register(this);
    }

    private Predicate<ShapeTouchEvent> getButtonHandler(final String text) {
        return new Predicate<ShapeTouchEvent>() {

            @Override
            public boolean apply(ShapeTouchEvent event) {
                System.err.println(text + (event.isPressed() ? " pressed" : " depressed"));
                return false;
            }
        };
    }

//    @Subscribe
//    public void touched(RawTouchEvent event) {
//        System.err.println(event.getX() + " " + event.getY());
//    }

    @Subscribe
    public void touched(ShapeTouchEvent event) {
        System.err.println(event.getShape());
    }

    public static void main(String[] args) throws Exception {
        
        ImageRotation rotation = ImageRotation.LEFT_90;
        if (args.length >= 2) {            
            rotation = parseRotation(args[1]);
        }
        
        PiStormsMain piStormsMain = new PiStormsMain(args[0], rotation);
        // the touch display starts a thread which emits events when the screen is touched
        Thread.sleep(TimeUnit.HOURS.toMillis(1));
        
        piStormsMain.touchScreen.close();
    }

    private static ImageRotation parseRotation(String rotationValue) {
        int intRotation = Integer.parseInt(rotationValue);
        switch (intRotation) {
        case 1:
            return ImageRotation.RIGHT_90;
        case 2:
            return ImageRotation.RIGHT_180;
        case 3:
            return ImageRotation.LEFT_90;
        default:
            return ImageRotation.NONE;
        }
    }
}
