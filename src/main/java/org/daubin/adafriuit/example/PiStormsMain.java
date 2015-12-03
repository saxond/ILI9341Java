package org.daubin.adafriuit.example;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.daubin.adafriuit.ILI9341;
import org.daubin.adafriuit.ILI9341Builder;

import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.io.spi.SpiMode;

public class PiStormsMain {
    

    public static void main(String[] args) throws Exception {

        // Create LCD display class.
        SpiDevice spiDevice = SpiFactory.getInstance(//SpiChannel.CS0, SpiMode.MODE_0);
                SpiChannel.CS0, 64000000, SpiMode.MODE_0);
        ILI9341 disp = ILI9341Builder.newBuilder().
                setDcPin(RaspiPin.GPIO_24).
                setResetPin(RaspiPin.GPIO_25).
                setRotation(3).
                build(spiDevice);
        
                //spi=SPI.SpiDev(SPI_PORT, SPI_DEVICE, max_speed_hz=64000000))


        // Initialize display.
        disp.begin();
        
        System.err.println("After begin");
        
        BufferedImage bufferedImage = new BufferedImage(
                ILI9341.ILI9341_TFTWIDTH,
                ILI9341.ILI9341_TFTHEIGHT,
                BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(Color.RED);
        graphics.fillRect(0, 0, ILI9341.ILI9341_TFTWIDTH, ILI9341.ILI9341_TFTHEIGHT);
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial Black", Font.BOLD, 20));
        graphics.drawString(args[0], 10, 25);
        
        disp.display(bufferedImage);
    }
}
