package org.daubin.adafriuit;

public class RawTouchEvent {
    private final int x;
    private final int y;
    
    public RawTouchEvent(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "RawTouchEvent [x=" + x + ", y=" + y + "]";
    }
}
