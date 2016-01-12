package org.daubin.adafriuit;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Notices {@link RepaintEvent}s and updates the display at the specified refresh interval.
 * @author sdaubin
 *
 */
public class DisplayRepaintManager implements AutoCloseable, Runnable {
    
    private final Logger logger = LoggerFactory.getLogger(DisplayRepaintManager.class);
    
    private final ILI9341 display;
    private final AtomicBoolean redraw = new AtomicBoolean(false);
    private final ScheduledExecutorService executor;

    public DisplayRepaintManager(ILI9341 display, EventBus eventBus, int refreshInterval, TimeUnit timeUnit) {
        this.display = display;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, refreshInterval, refreshInterval, timeUnit);
        eventBus.register(this);
    }
    
    @Subscribe
    public void repaint(RepaintEvent e) {
        redraw.set(true);
    }

    @Override
    public void run() {
        if (redraw.get()) {
            redraw.set(false);
            try {
                display.display();
            } catch (IOException e) {
                logger.debug(e.getMessage(), e);
            }
        }
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
    
    
}
