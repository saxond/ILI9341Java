package org.daubin.adafriuit;

import java.io.IOException;

import org.daubin.adafriuit.image.ImageRotation;

import com.pi4j.io.i2c.I2CDevice;

public class TouchScreenBuilder {
    private ImageRotation imageRotation = ImageRotation.NONE;
    
    private TouchScreenBuilder() {
    }
    
    public static TouchScreenBuilder newBuilder() {
        return new TouchScreenBuilder();
    }
    
    /**
     * Create a new touch screen instance.
     * @param i2cDevice
     * @return
     */
    public TouchScreen createTouchScreen(I2CDevice i2cDevice) {
        final TouchScreenImpl touchScreen = new TouchScreenImpl(i2cDevice);
        if (ImageRotation.NONE == imageRotation) {
            return touchScreen;
        }
        
        return new RotatedTouchScreen(imageRotation, touchScreen);
    }

    public TouchScreenBuilder setImageRotation(ImageRotation imageRotation) {
        this.imageRotation = imageRotation;
        return this;
    }

    private final class RotatedTouchScreen implements TouchScreen {
        private final ImageRotation imageRotation;
        private final TouchScreenImpl touchScreen;
    
        private RotatedTouchScreen(ImageRotation imageRotation, TouchScreenImpl touchScreen) {
            this.imageRotation = imageRotation;
            this.touchScreen = touchScreen;
        }
    
        private int getInvertedY() throws IOException {
            return ILI9341.ILI9341_TFTHEIGHT - touchScreen.getTouchY();   
        }
    
        private int getInvertedX() throws IOException {
            return ILI9341.ILI9341_TFTWIDTH - touchScreen.getTouchX();   
        }
    
        @Override
        public int getTouchY() throws IOException {
            switch (imageRotation) {
            case RIGHT_90:
                return getInvertedX();
            case RIGHT_180:
                return getInvertedY();
            case LEFT_90:
                return touchScreen.getTouchX();
            default:
                return touchScreen.getTouchY();
            }
        }
    
        @Override
        public int getTouchX() throws IOException {
            switch (imageRotation) {
            case RIGHT_90:
                return touchScreen.getTouchY();
            case RIGHT_180:
                return getInvertedX();
            case LEFT_90:
                return getInvertedY();
            default:
                return touchScreen.getTouchX();
            }
        }
    }
}
