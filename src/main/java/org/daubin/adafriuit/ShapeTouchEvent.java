package org.daubin.adafriuit;

import java.awt.Shape;

public class ShapeTouchEvent {

    private final Shape shape;
    private final boolean pressed;    

    public ShapeTouchEvent(Shape shape, boolean pressed) {
        this.shape = shape;
        this.pressed = pressed;
    }

    public Shape getShape() {
        return shape;
    }

    public boolean isPressed() {
        return pressed;
    }

}
