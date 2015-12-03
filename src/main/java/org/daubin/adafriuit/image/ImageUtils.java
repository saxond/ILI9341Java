package org.daubin.adafriuit.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageUtils {
    
    public static byte[] to565RGBBytes(BufferedImage image) throws IOException {
        return to565RGBBytes(image, 0);
    }

    /**
     * Generator function to convert an image to 16-bit 565 RGB bytes.
     * @return
     * @throws IOException 
     */
    public static byte[] to565RGBBytes(BufferedImage image, int rotation) throws IOException {
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        ByteBuffer buffer = ByteBuffer.allocate(width * height * 2);   
//        for (int x=width-1;x>= 0;x--) {
//            for (int y=0;y<height;y++) {

        for (int y=0;y<height;y++) {
            for (int x=0;x<width;x++) {
                
//                int ax = getScreenXFromImageCoords(rotation, x, y, width);
//                int ay = getScreenYFromImageCoords(rotation, x, y, height);
    
                Color c = new Color(image.getRGB(x, y));
                int pixelToSend = getColor565(c);
        
                buffer.putShort((short)pixelToSend);
            }
        }
        return buffer.array();
    }
    

    /**
     * Calculates the x-coordinate of the screen upon rotation.
     */
    public static int getScreenXFromImageCoords(int rotation, int x, int y, int width) {
        switch (rotation) {
        case 0:
            return x;
        case 1:
            return width-y;
        case 2:
            return width-x;
        case 3:
            return y;
        }
        return 0;
    }
    
    public static int getScreenYFromImageCoords(int rotation, int x, int y, int height) {
        switch (rotation) {
        case 0:
            return y;
        case 1:
            return x;
        case 2:
            return height-y;
        case 3:
            return height-x;
        }
        return 0;
    }


    /**
     * Convert red, green, blue components to a 16-bit 565 RGB value. Components
     * should be values 0 to 255
     */
    public static int getColor565(Color c) {
        int val = ((c.getRed() & 0xF8) << 8) | ((c.getGreen() & 0xFC) << 3) | (c.getBlue() >> 3);
        return val;
    }

}
