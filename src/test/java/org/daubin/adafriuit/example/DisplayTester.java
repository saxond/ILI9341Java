package org.daubin.adafriuit.example;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.daubin.adafriuit.ComponentDisplayAdapter;
import org.daubin.adafriuit.Display;
import org.daubin.adafriuit.DisplayRepaintManager;
import org.daubin.adafriuit.ILI9341;
import org.daubin.adafriuit.NotifyingTouchScreen;
import org.daubin.adafriuit.RepaintEvent;
import org.daubin.adafriuit.TouchScreen;
import org.daubin.pistorms.display.PiStormsDisplay;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * This thing is used to create test applications which mimic a ILI9341 display
 * hooked up as a {@link PiStormsDisplay} instance.  This allows one to run a local swing
 * application to test component layouts.
 * 
 * A normal PiStormsDisplay paints a JPanel to a BufferedImage and writes it to a 
 * ILI9341 display.
 * 
 * This tester works in a similar way, painting components to a buffered image and 
 * rendering the image.
 * 
 * @author sdaubin
 *
 */
public abstract class DisplayTester {
    
    private DisplayTester() {}
    
    public static final PiStormsDisplay createDisplay() throws IOException { 
        
        final EventBus eventBus = new EventBus();
        final JPanel panel = new JPanel();
        final BufferedImage image = createImage();
        panel.setSize(new Dimension(image.getWidth(), image.getHeight()));
        new ComponentDisplayAdapter(panel, image.getGraphics(), eventBus);
        
        eventBus.register(new Object() {
            @Subscribe
            public void redraw(RepaintEvent e) {
                EventQueue.invokeLater(new Runnable()
                {
                    public void run() {
                        System.err.println("Paint panel");
                        panel.paint(image.getGraphics());
                    }
                });
            }
        });
        
        //displayImageFrameAsync(image, eventBus);
        
        final JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setSize(image.getWidth(), image.getHeight());

        ImageIcon imageIcon = new ImageIcon(image);
        final JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);
        


        frame.getContentPane().add(jLabel, BorderLayout.CENTER);
        
        final Display display = new Display() {
            
            @Override
            public void display() throws IOException {
                EventQueue.invokeLater(new Runnable()
                {
                    public void run(){
                        ImageIcon imageIcon = new ImageIcon(image);
                        System.err.println("Refresh image");
                        jLabel.setIcon(imageIcon);
                        
                    }
                });
            }
        };
        final DisplayRepaintManager displayRepaintManager = new DisplayRepaintManager(display, eventBus, 100, TimeUnit.MILLISECONDS);

        frame.pack();
        frame.setLocationRelativeTo(null);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        FrameTouchScreen touchScreen = new FrameTouchScreen(frame);
        frame.addMouseListener(touchScreen);
        final NotifyingTouchScreen notifyingTouchScreen = new NotifyingTouchScreen(touchScreen, eventBus, 
                100, TimeUnit.MILLISECONDS);
        
        return new PiStormsDisplay() {
            
            @Override
            public void close() throws Exception {
                displayRepaintManager.close();
                notifyingTouchScreen.close();
            }
            
            @Override
            public NotifyingTouchScreen getTouchScreen() {
                return notifyingTouchScreen;
            }
            
            @Override
            public JPanel getPanel() {
                return panel;
            }
            
            @Override
            public EventBus getEventBus() {
                return eventBus;
            }
            
            @Override
            public Display getDisplay() {
                return display;
            }
        };
    }

    public static BufferedImage createImage() {
        return new BufferedImage(
                ILI9341.ILI9341_TFTWIDTH,
                ILI9341.ILI9341_TFTHEIGHT,
                BufferedImage.TYPE_USHORT_565_RGB);
    }

    private static final class FrameTouchScreen implements TouchScreen, MouseListener {
        private static final Point NO_TOUCH = new Point(-1, -1);
        private final JFrame frame;
        private volatile boolean pressed;
    
        private FrameTouchScreen(JFrame frame) {
            this.frame = frame;
        }
    
        @Override
        public int getTouchY() throws IOException {
            return getMousePosition().y;
        }
    
        private Point getMousePosition() {
            return !pressed || frame.getMousePosition() == null ? NO_TOUCH : frame.getMousePosition();
        }
    
        @Override
        public int getTouchX() throws IOException {
            return getMousePosition().x;
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            pressed = false;
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            pressed = true;
        }
    
        @Override
        public void mouseClicked(MouseEvent e) {
        }
    
        @Override
        public void mouseEntered(MouseEvent e) {
        }
    
        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
}
