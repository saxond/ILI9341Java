package org.daubin.adafriuit;

import java.io.IOException;

/**
 * An interface to the ILI9341 touch screen.  Use the {@link TouchScreenBuilder} to get an instance. 
 * @author sdaubin
 *
 */
public interface TouchScreen {

    /**
     * Reads the x-coordinate of the touchscreen press
     * @throws IOException 
     */
    int getTouchX() throws IOException;
    
    /**
     * Reads the x-coordinate of the touchscreen press
     * @throws IOException 
     */
    int getTouchY() throws IOException;
}
