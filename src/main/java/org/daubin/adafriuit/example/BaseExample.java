package org.daubin.adafriuit.example;

import org.daubin.adafriuit.RawTouchEvent;
import org.daubin.adafriuit.image.ImageRotation;
import org.daubin.pistorms.display.PiStormsDisplay;
import org.daubin.pistorms.display.PiStormsDisplayBuilder;

public class BaseExample {

    protected final PiStormsDisplay piStormsDisplay;

    public BaseExample(ImageRotation imageRotation) throws Exception {
        this(PiStormsDisplayBuilder.newBuilder().setImageRotation(imageRotation).build());
    }

    public BaseExample(PiStormsDisplay piStormsDisplay) {
        super();
        this.piStormsDisplay = piStormsDisplay;
    }

//    @Subscribe
    public void touched(RawTouchEvent event) {
        System.err.println(event.getX() + " " + event.getY());
    }
    
    protected static ImageRotation parseRotation(String[] args, int index) {
        ImageRotation rotation = ImageRotation.LEFT_90;
        if (args.length >= index+1) {            
            rotation = parseRotation(args[index]);
        } 
       
        return rotation;
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
