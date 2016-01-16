package org.daubin.adafriuit.example;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.daubin.adafriuit.RawTouchEvent;
import org.daubin.adafriuit.image.ImageRotation;
import org.daubin.pistorms.display.PiStormsDisplay;

/**
 * A button test app for PiStorms.
 * @author sdaubin
 *
 */
public class ButtonExample extends BaseExample {
    
    public ButtonExample(PiStormsDisplay display, final String text, ImageRotation rotation) throws Exception {
        super(display);
        
        setupPanel(text);
        
//        panel.doLayout();
//        panel.print(display.getBufferedImage().createGraphics());
//        display.display();
    }

    public ButtonExample(final String text, ImageRotation rotation) throws Exception {
        super(rotation);
        
        setupPanel(text);
        
//        panel.doLayout();
//        panel.print(display.getBufferedImage().createGraphics());
//        display.display();
    }

    private void setupPanel(final String text) {
        final JPanel panel = this.piStormsDisplay.getPanel();
        
        panel.setLayout(new FlowLayout());
        panel.setBackground(Color.CYAN);
        
        final JLabel label = new JLabel(text);
        label.setBounds(20, 20, 80, 40);
        panel.add(label);
        
        JButton button1 = new JButton("Button1");
        panel.add(button1);
        button1.setBounds(20, 60, 125, 40);
        button1.addActionListener(getButtonListener("btn1", label));
        
        JButton button2 = new JButton("Button2");
        panel.add(button2);
        button2.setBounds(20, 125, 125, 40);
        button2.addActionListener(getButtonListener("btn2", label));
        
        // periodically reset the label text to verify that the display refreshes
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                label.setText(text);
            }
            
        }, 10, 5, TimeUnit.SECONDS);

        panel.setVisible(true);
    }
    
    private static ActionListener getButtonListener(final String text, final JLabel label) {
        return new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                System.err.println(text + " " + ((AbstractButton)e.getSource()).getModel().isPressed());
                label.setText(text);
            }
        };
    }


//    @Subscribe
    public void touched(RawTouchEvent event) {
        System.err.println(event.getX() + " " + event.getY());
    }


    public static void main(String[] args) throws Exception {
        
        ButtonExample piStormsMain = new ButtonExample(args[0], parseRotation(args, 1));
        // the touch display starts a thread which emits events when the screen is touched
        Thread.sleep(TimeUnit.HOURS.toMillis(1));
        
        piStormsMain.piStormsDisplay.close();
    }
}
