package org.daubin.adafriuit.image;

import java.awt.Dimension;

public enum ImageRotation {
    NONE, 
    RIGHT_90(true), 
    RIGHT_180, 
    LEFT_90(true);
    
    private final boolean invertDimension;
    
    private ImageRotation() {
        this(false);
    }
    
    private ImageRotation(boolean invert) {
        this.invertDimension = invert;
    }
    
    public Dimension getDimension(int width, int height) {
        return invertDimension ? new Dimension(height, width) : new Dimension(width, height);
    }
}
