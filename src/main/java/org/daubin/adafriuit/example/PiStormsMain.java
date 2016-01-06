package org.daubin.adafriuit.example;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.daubin.adafriuit.ILI9341;
import org.daubin.adafriuit.ILI9341Builder;
import org.daubin.adafriuit.image.ImageRotation;

import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.io.spi.SpiMode;

public class PiStormsMain {
    

    public static void main(String[] args) throws Exception {

        // Create LCD display class.
        SpiDevice spiDevice = SpiFactory.getInstance(
                SpiChannel.CS0, 64000000, SpiMode.MODE_0);

        // http://pinout.xyz/pinout/pin18_gpio24
        ILI9341 disp = ILI9341Builder.newBuilder().
                // wiring pin 5, BCM pin 24, physical pin 18
                setDcPin(RaspiPin.GPIO_05).
                setResetPin(RaspiPin.GPIO_06).
                setImageRotation(ImageRotation.LEFT_90).
                build(spiDevice);

        System.err.println("After begin");
        
        disp.clear(Color.BLACK);
        
        BufferedImage bufferedImage = disp.getBufferedImage();
        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial Black", Font.BOLD, 20));
        graphics.drawString(args[0], 20, 40);
        
        disp.display();
    }
}
