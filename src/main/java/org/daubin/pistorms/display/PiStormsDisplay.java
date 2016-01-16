package org.daubin.pistorms.display;

import javax.swing.JPanel;

import org.daubin.adafriuit.Display;
import org.daubin.adafriuit.NotifyingTouchScreen;

import com.google.common.eventbus.EventBus;

public interface PiStormsDisplay extends AutoCloseable {
    NotifyingTouchScreen getTouchScreen();
    JPanel getPanel();
    Display getDisplay();
    EventBus getEventBus();
}
