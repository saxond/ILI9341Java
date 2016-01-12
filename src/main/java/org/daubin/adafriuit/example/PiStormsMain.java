package org.daubin.adafriuit.example;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.daubin.adafriuit.ComponentDisplayAdapter;
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

public class PiStormsMain {
    
    private final NotifyingTouchScreen touchScreen;

    public PiStormsMain(final String text, ImageRotation rotation) throws Exception {

        // Create LCD display class.
        SpiDevice spiDevice = SpiFactory.getInstance(
                SpiChannel.CS0, 64000000, SpiMode.MODE_0);
        
        final I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);

        I2CDevice i2cDevice = bus.getDevice(0x34 >> 1); 

        final EventBus eventBus = new EventBus();
        
        // http://pinout.xyz/pinout/pin18_gpio24
        ILI9341 disp = ILI9341Builder.newBuilder().
                // wiring pin 5, BCM pin 24, physical pin 18
                setDcPin(RaspiPin.GPIO_05).
                setResetPin(RaspiPin.GPIO_06).
                setImageRotation(rotation).
                setEventBus(eventBus).
                build(spiDevice);
        
        touchScreen = new NotifyingTouchScreen(
            TouchScreenBuilder.newBuilder().setImageRotation(rotation).createTouchScreen(i2cDevice),
            eventBus, 100, TimeUnit.MILLISECONDS);

        System.err.println("After begin");
        
        disp.clear(Color.BLACK);
        
        BufferedImage bufferedImage = disp.getBufferedImage();
        final Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial Black", Font.BOLD, 20));
        graphics.drawString(text, 20, 40);

        final JPanel panel = new JPanel();
        new ComponentDisplayAdapter(panel, graphics, eventBus);
        
        panel.setLayout(new FlowLayout());
        panel.setBackground(Color.WHITE);
        panel.setSize(disp.getDimension());
        
        final JLabel label = new JLabel(text);
        label.setBounds(20, 20, 80, 40);
        panel.add(label);
        
        JButton button1 = new JButton("Button1");
        panel.add(button1);
        button1.setBounds(20, 60, 125, 40);
        button1.addActionListener(getButtonListener("btn1", label));
        
        JButton button2 = new JButton("Button2");
        panel.add(button2);
        button2.setBounds(20, 125, 125, 40);
        button2.addActionListener(getButtonListener("btn2", label));
        
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                label.setText(text);
            }
            
        }, 10, 5, TimeUnit.SECONDS);

        panel.setVisible(true);
        
        panel.print(graphics);
        disp.display();
        
        DisplayRepaintManager displayRepaintManager = new DisplayRepaintManager(disp, eventBus, 100, TimeUnit.MILLISECONDS);
        
//        touchScreen.registerButton(button1);
//        touchScreen.registerButton(button2);
        
        //touchScreen.listenToShape(button1.getBounds(), getButtonHandler("Button 1"));
        //touchScreen.listenToShape(button2.getBounds(), getButtonHandler("Button 2"));
        
        eventBus.register(this);
    }
    
    private ActionListener getButtonListener(final String text, final JLabel label) {
        return new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                System.err.println(text);
                label.setText(text);
            }
        };
    }


//    @Subscribe
//    public void touched(RawTouchEvent event) {
//        System.err.println(event.getX() + " " + event.getY());
//    }


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
