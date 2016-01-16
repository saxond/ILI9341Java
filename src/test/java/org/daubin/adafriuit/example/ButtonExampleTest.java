package org.daubin.adafriuit.example;

import org.daubin.adafriuit.RawTouchEvent;
import org.daubin.adafriuit.image.ImageRotation;

import com.google.common.eventbus.Subscribe;

public class ButtonExampleTest  {
    
    public static void main(String[] args) throws Exception {
        ButtonExample buttonExample = new ButtonExample(DisplayTester.createDisplay(), "Test", ImageRotation.LEFT_90);
    }
    
    @Subscribe
    public void touch(RawTouchEvent e) {
        System.err.println(e);
    }
}
