package org.daubin.adafriuit;

import org.junit.Assert;
import org.junit.Test;

public class TouchScreenImplTest {

    @Test
    public void test() {
        byte[] bytes = new byte[] {45, 1};
        Assert.assertEquals(301, TouchScreenImpl.toInt(bytes, bytes.length));
    }
    
    @Test
    public void test2() {
        byte[] bytes = new byte[] {(byte)0xff, 0};
        Assert.assertEquals(255, TouchScreenImpl.toInt(bytes, bytes.length));
    }
}
