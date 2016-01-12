package org.daubin.adafriuit;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

/**
 * A notifying touch screen wraps a {@link TouchScreen} and provides callbacks for 
 * touch events.
 * @author sdaubin
 *
 */
public class NotifyingTouchScreen implements TouchScreen, AutoCloseable {
    
    private final Logger logger = LoggerFactory.getLogger(NotifyingTouchScreen.class);
    
    private final TouchScreen touchScreen;
    
    /**
     * Touches are detected on a timer task.
     */
    private final ScheduledExecutorService executor;

    public NotifyingTouchScreen(final TouchScreen touchScreen, final EventBus eventBus, int frequency, TimeUnit timeUnit) throws IOException {
        this.touchScreen = touchScreen;
        
        this.executor = Executors.newSingleThreadScheduledExecutor();
        Runnable runLoop = new Runnable() {
            
            private int lastX = touchScreen.getTouchX();
            private int lastY = touchScreen.getTouchY();
            
            public void run() {
                try {
                    int x = touchScreen.getTouchX();
                    int y = touchScreen.getTouchY();
                    
                    if (x != lastX || y != lastY) {
                        RawTouchEvent event = new RawTouchEvent(x, y);
                        eventBus.post(event);
                        
                        lastX = x;
                        lastY = y;
                    }
                    
                } catch (Exception e) {
                    logger.debug(e.getMessage(), e);
                }
            }
        };
        executor.scheduleAtFixedRate(runLoop, frequency, frequency, timeUnit);
    }

    public int getTouchX() throws IOException {
        return touchScreen.getTouchX();
    }

    public int getTouchY() throws IOException {
        return touchScreen.getTouchY();
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}
