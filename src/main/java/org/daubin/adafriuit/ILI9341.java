package org.daubin.adafriuit;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.daubin.adafriuit.image.ImageUtils;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.spi.SpiDevice;

/**
 * Use the {@link ILI9341Builder} to create an instance of this class.
 * @author sdaubin
 *
 */
public class ILI9341 {
    
    public final static int ILI9341_TFTWIDTH    = 240;
    public final static int ILI9341_TFTHEIGHT   = 320;
    
    public final static byte ILI9341_NOP         = 0x00;
    public final static byte ILI9341_SWRESET     = 0x01;
    public final static byte ILI9341_RDDID       = 0x04;
    public final static byte ILI9341_RDDST       = 0x09;

    public final static byte ILI9341_SLPIN       = 0x10;
    public final static byte ILI9341_SLPOUT      = 0x11;
    public final static byte ILI9341_PTLON       = 0x12;
    public final static byte ILI9341_NORON       = 0x13;

    public final static byte ILI9341_RDMODE      = 0x0A;
    public final static byte ILI9341_RDMADCTL    = 0x0B;
    public final static byte ILI9341_RDPIXFMT    = 0x0C;
    public final static byte ILI9341_RDIMGFMT    = 0x0A;
    public final static byte ILI9341_RDSELFDIAG  = 0x0F;

    public final static byte ILI9341_INVOFF      = 0x20;
    public final static byte ILI9341_INVON       = 0x21;
    public final static byte ILI9341_GAMMASET    = 0x26;
    public final static byte ILI9341_DISPOFF     = 0x28;
    public final static byte ILI9341_DISPON      = 0x29;

    public final static byte ILI9341_CASET       = 0x2A;
    public final static byte ILI9341_PASET       = 0x2B;
    public final static byte ILI9341_RAMWR       = 0x2C;
    public final static byte ILI9341_RAMRD       = 0x2E;

    public final static byte ILI9341_PTLAR       = 0x30;
    public final static byte ILI9341_MADCTL      = 0x36;
    public final static byte ILI9341_PIXFMT      = 0x3A;

    public final static int ILI9341_FRMCTR1     = 0xB1;
    public final static int ILI9341_FRMCTR2     = 0xB2;
    public final static int ILI9341_FRMCTR3     = 0xB3;
    public final static int ILI9341_INVCTR      = 0xB4;
    public final static int ILI9341_DFUNCTR     = 0xB6;

    public final static int ILI9341_PWCTR1      = 0xC0;
    public final static int ILI9341_PWCTR2      = 0xC1;
    public final static int ILI9341_PWCTR3      = 0xC2;
    public final static int ILI9341_PWCTR4      = 0xC3;
    public final static int ILI9341_PWCTR5      = 0xC4;
    public final static int ILI9341_VMCTR1      = 0xC5;
    public final static int ILI9341_VMCTR2      = 0xC7;

    public final static int ILI9341_RDID1       = 0xDA;
    public final static int ILI9341_RDID2       = 0xDB;
    public final static int ILI9341_RDID3       = 0xDC;
    public final static int ILI9341_RDID4       = 0xDD;

    public final static int ILI9341_GMCTRP1     = 0xE0;
    public final static int ILI9341_GMCTRN1     = 0xE1;

    public final static int ILI9341_PWCTR6      = 0xFC;

    public final static int ILI9341_BLACK       = 0x0000;
    public final static int ILI9341_BLUE        = 0x001F;
    public final static int ILI9341_RED         = 0xF800;
    public final static int ILI9341_GREEN       = 0x07E0;
    public final static int ILI9341_CYAN        = 0x07FF;
    public final static int ILI9341_MAGENTA     = 0xF81F;
    public final static int ILI9341_YELLOW      = 0xFFE0; 
    public final static int ILI9341_WHITE       = 0xFFFF;

    private final GpioPinDigitalOutput dcPin;
    private final GpioPinDigitalOutput resetPin;
    private final BufferedImage buffer;
    private final SpiDevice spiDevice;

    ILI9341(Pin dc, Pin resetPin, SpiDevice spiDevice, GpioController gpioController, BufferedImage image) {
        
        this.spiDevice = spiDevice;
        buffer = image;
        
        this.dcPin = gpioController.provisionDigitalOutputPin(dc, "dc", PinState.HIGH);
        
        this.dcPin.setShutdownOptions(true, PinState.LOW);
        
        this.resetPin = resetPin == null ? null : gpioController.provisionDigitalOutputPin(resetPin, "reset", PinState.HIGH);
        
        if (this.resetPin != null) {
            this.resetPin.setShutdownOptions(true, PinState.LOW);
        }
         
    }

    public void begin() throws IOException {
        reset();
        
        command(0xEF);
        data(0x03);
        data(0x80);
        data(0x02);
        command(0xCF);
        data(0x00);
        data(0XC1);
        data(0X30);
        command(0xED);
        data(0x64);
        data(0x03);
        data(0X12);
        data(0X81);
        command(0xE8);
        data(0x85);
        data(0x00);
        data(0x78);
        command(0xCB);
        data(0x39);
        data(0x2C);
        data(0x00);
        data(0x34);
        data(0x02);
        command(0xF7);
        data(0x20);
        command(0xEA);
        data(0x00);
        data(0x00);
        command(ILI9341_PWCTR1);    // Power control 
        data(0x23);                 // VRH[5:0] 
        command(ILI9341_PWCTR2);    // Power control 
        data(0x10);                 // SAP[2:0];BT[3:0] 
        command(ILI9341_VMCTR1);    // VCM control 
        data(0x3e);
        data(0x28);
        command(ILI9341_VMCTR2);    // VCM control2 
        data(0x86);                 // --
        command(ILI9341_MADCTL);    //  Memory Access Control 
        data(0x48);
        command(ILI9341_PIXFMT);
        data(0x55);
        command(ILI9341_FRMCTR1);
        data(0x00);
        data(0x18);
        command(ILI9341_DFUNCTR);   //  Display Function Control 
        data(0x08);
        data(0x82);
        data(0x27);
        command(0xF2);              //  3Gamma Function Disable 
        data(0x00);
        command(ILI9341_GAMMASET);  // Gamma curve selected 
        data(0x01);
        command(ILI9341_GMCTRP1);   // Set Gamma
        data(0x0F);
        data(0x31);
        data(0x2B);
        data(0x0C);
        data(0x0E);
        data(0x08);
        data(0x4E);
        data(0xF1);
        data(0x37);
        data(0x07);
        data(0x10);
        data(0x03);
        data(0x0E);
        data(0x09);
        data(0x00);
        command(ILI9341_GMCTRN1);   // Set Gamma 
        data(0x00);
        data(0x0E);
        data(0x14);
        data(0x03);
        data(0x11);
        data(0x07);
        data(0x31);
        data(0xC1);
        data(0x48);
        data(0x08);
        data(0x0F);
        data(0x0C);
        data(0x31);
        data(0x36);
        data(0x0F);
        command(ILI9341_SLPOUT);    // Exit Sleep
        sleep(0.120f);
        command(ILI9341_DISPON);    // Display on 
    }

    private void data(int data) throws IOException {
        sendByte(State.Data, data);
    }
    
    /**
     * Write a byte or array of bytes to the display as command data.
     * @param i
     * @throws IOException 
     */
    private void command(int data) throws IOException {
        sendByte(State.Command, data);
    }
    
    void sendByte(State state, int data) throws IOException {
        sendBytes(state, new byte[] { (byte) data});
    }
    
    void sendShort(State state, int data) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort((short) data);
        sendBytes(state, buffer.array());
    }

    /**
     * Write a byte or array of bytes to the display. 
     */
    void sendBytes(State state, byte[] data) throws IOException {
        
        // Set DC low for command, high for data.
        if (!state.state.equals(dcPin.getState())) {
            dcPin.setState(state.state);
        }
        
        if (data.length < SpiDevice.MAX_SUPPORTED_BYTES) {
            spiDevice.write(data);
        } else {
        
            int length;
            // Write data a chunk at a time.
            for (int start = 0; start < data.length; start+= SpiDevice.MAX_SUPPORTED_BYTES) {
                length = Math.min(SpiDevice.MAX_SUPPORTED_BYTES, data.length - start);
                spiDevice.write(data, start, length);
            }
        }
        
    }

    public void reset() {

        if (resetPin != null) {
            resetPin.setState(PinState.HIGH);
            sleep(0.005f);
            resetPin.setState(PinState.LOW);
            sleep(0.02f);
            resetPin.setState(PinState.HIGH);
            sleep(0.150f);
        }
    }

    private void sleep(float seconds) {
        long timeInMillis = (long) (seconds * 1000);
        try {
            Thread.sleep(timeInMillis);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Write the provided image to the hardware.  If no
     * image parameter is provided the display buffer will be written to the
     * hardware.  If an image is provided, it should be RGB format and the
     * same dimensions as the display hardware.
     * @param image
     */
    public void display(BufferedImage image) throws IOException {
        // Set address bounds to entire display.
        setWindow(0, 0, -1, -1);
        
        byte[] data = ImageUtils.to565RGBBytes(image);
        
        sendBytes(State.Data, data);
    }
    
    /**
     * Write the display buffer to the hardware.
     * @throws IOException
     */
    public void display() throws IOException {
        display(buffer);
    }
    
    public void clear(Color color) {
        buffer.getGraphics().setColor(color);
        buffer.getGraphics().fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
    }

    /**
     * Set the pixel address window for proceeding drawing commands. x0 and
     *  x1 should define the minimum and maximum x pixel bounds.  y0 and y1 
     *  should define the minimum and maximum y pixel bound.  If no parameters 
     *  are specified the default will be to update the entire display from 0,0
     *  to 239,319.
     * @throws IOException 
     */
    private void setWindow(int x0, int y0, int x1, int y1) throws IOException {
        if (x1 < 0) {
            x1 = buffer.getWidth() - 1;
        }
        if (y1 < 0) {
            y1 = buffer.getHeight() - 1;
        }
        command(ILI9341_CASET);     // Column addr set
        sendShort(State.Data, x0);
        sendShort(State.Data, x1);
        command(ILI9341_PASET);     // Row addr set
        sendShort(State.Data, y0);
        sendShort(State.Data, y1);
        command(ILI9341_RAMWR);     // write to RAM
    }
    
    static enum State {
        Command(PinState.LOW),
        Data(PinState.HIGH);
        
        final PinState state;

        private State(PinState state) {
            this.state = state;
        }
    }
    
}
