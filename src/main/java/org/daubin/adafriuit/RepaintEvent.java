package org.daubin.adafriuit;

import java.awt.Rectangle;

public class RepaintEvent {

    private final Rectangle bounds;

    public RepaintEvent(Rectangle bounds) {
        this.bounds = bounds;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
