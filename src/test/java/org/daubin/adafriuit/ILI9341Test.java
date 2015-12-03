package org.daubin.adafriuit;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.daubin.adafriuit.ILI9341.State;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.spi.SpiDevice;

public class ILI9341Test {
    
    @Test
    public void send() throws IOException {
        byte[] bytes = new byte[3000];
        
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)i;
        }
        
        MockSpiDevice spiDevice = new MockSpiDevice();
        ILI9341 ili9341 = new ILI9341(Mockito.mock(Pin.class), Mockito.mock(Pin.class), spiDevice, createGpio(),
                Mockito.mock(BufferedImage.class));
        
        ili9341.sendBytes(State.Data, bytes);
        
        byte[] bytesAfter = spiDevice.getBytes();
        Assert.assertEquals(bytes.length, bytesAfter.length);
        
        for (int i = 0; i < bytes.length; i++) {
            Assert.assertEquals("Index: " + i, bytes[i], bytesAfter[i]);
        }
    }
    
    @Test
    public void sendMultiInt() throws IOException {
        
        MockSpiDevice spiDevice = new MockSpiDevice();
        ILI9341 ili9341 = new ILI9341(Mockito.mock(Pin.class), Mockito.mock(Pin.class), spiDevice, createGpio(), Mockito.mock(BufferedImage.class));
        
        ili9341.sendShort(State.Data, 0x265535);
        
        byte[] bytesAfter = spiDevice.getBytes();
        Assert.assertEquals(2, bytesAfter.length);
                
        Assert.assertEquals(0x55, bytesAfter[0]);
        Assert.assertEquals(0x35, bytesAfter[1]);
    }

    private GpioController createGpio() {
        GpioController gpio = Mockito.mock(GpioController.class);
        
        Mockito.when(gpio.provisionDigitalOutputPin(Mockito.<Pin>any(), Mockito.anyString(), Mockito.<PinState>any())).thenReturn(
                Mockito.mock(GpioPinDigitalOutput.class));
        
        return gpio;
    }
    
    private static class MockSpiDevice implements SpiDevice {
        
        ByteBuffer bytes = ByteBuffer.allocate(10000);

        @Override
        public String write(String data, Charset charset) throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

        public byte[] getBytes() {
            byte[] bytes = new byte[this.bytes.position()];
            System.arraycopy(this.bytes.array(), 0, bytes, 0, this.bytes.position());
            return bytes;
        }

        @Override
        public String write(String data, String charset) throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ByteBuffer write(ByteBuffer data) throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public byte[] write(InputStream input) throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int write(InputStream input, OutputStream output) throws IOException {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public byte[] write(byte[] data, int start, int length) throws IOException {
            byte[] buffer = new byte[length];
            System.arraycopy(data, start, buffer, 0, length);
            
            this.bytes.put(buffer);
            return data;
        }

        @Override
        public byte[] write(byte... data) throws IOException {
            this.bytes.put(data);
            return data;
        }

        @Override
        public short[] write(short[] data, int start, int length) throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public short[] write(short... data) throws IOException {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
}
