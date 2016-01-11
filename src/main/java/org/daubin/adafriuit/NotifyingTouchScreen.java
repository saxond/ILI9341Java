package org.daubin.adafriuit;

import java.awt.Shape;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

/**
 * A notifying touch screen wraps a {@link TouchScreen} and provides callbacks for 
 * touch events.
 * @author sdaubin
 *
 */
public class NotifyingTouchScreen implements TouchScreen, AutoCloseable {
    private final TouchScreen touchScreen;
    private final ScheduledExecutorService executor;
    private final Map<Shape, Predicate<ShapeTouchEvent>> shapes = Maps.newConcurrentMap();
    private final Map<Shape, Boolean> shapeStates = Maps.newHashMap();
    private int lastX;
    private int lastY;

    public NotifyingTouchScreen(final TouchScreen touchScreen, final EventBus eventBus, int frequency, TimeUnit timeUnit) throws IOException {
        this.touchScreen = touchScreen;
        lastX = touchScreen.getTouchX();
        lastY = touchScreen.getTouchY();
        
        this.executor = Executors.newSingleThreadScheduledExecutor();
        Runnable runLoop = new Runnable() {
            public void run() {
                try {
                    int x = touchScreen.getTouchX();
                    int y = touchScreen.getTouchY();
                    
//                    System.out.println("Touch event: " + x + ", " + y);
                    
                    if (x != lastX || y != lastY) {
                        RawTouchEvent event = new RawTouchEvent(x, y);
                        eventBus.post(event);
                        
                        for (Entry<Shape,Predicate<ShapeTouchEvent>> entry : shapes.entrySet()) {
                            boolean pressed = entry.getKey().contains(x, y);
                            boolean lastState = shapeStates.get(entry.getKey()); 
                            if (pressed != lastState) {
                                ShapeTouchEvent shapeEvent = new ShapeTouchEvent(entry.getKey(), pressed);
                                try {
                                    entry.getValue().apply(shapeEvent);
                                } catch (Exception e) {
                                    // FIXME log
                                    e.printStackTrace();
                                }
                                shapeStates.put(entry.getKey(), pressed);
                            }
                        }
                        lastX = x;
                        lastY = y;
                    }
                    
                } catch (IOException e) {
                    // FIXME log
                    e.printStackTrace();
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

    /**
     * Registers a touch callback for a shape.  Every time the shape is pressed and de-pressed an
     * event will fire.
     * @param shape
     * @param callback
     */
    public void listenToShape(Shape shape, Predicate<ShapeTouchEvent> callback) {
        shapeStates.put(shape, Boolean.FALSE);
        shapes.put(shape, callback);
    }
}
