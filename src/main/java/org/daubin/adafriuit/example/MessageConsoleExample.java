package org.daubin.adafriuit.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.daubin.adafriuit.RawTouchEvent;
import org.daubin.adafriuit.image.ImageRotation;
import org.daubin.pistorms.display.PiStormsDisplay;

import com.google.common.eventbus.Subscribe;

/**
 * A button test app for PiStorms.
 * @author sdaubin
 *
 */
public class MessageConsoleExample extends BaseExample {
    
    public MessageConsoleExample(PiStormsDisplay display) throws Exception {
        super(display);
        
        setupPanel();
    }

    public MessageConsoleExample(ImageRotation rotation) throws Exception {
        super(rotation);
        
        setupPanel();
        
        //panel.print(display.getBufferedImage().getGraphics());
        //display.display();
    }

    protected void setupPanel() {
        JPanel panel = this.piStormsDisplay.getPanel();
        
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.GREEN);
        //panel.setBounds(0, 0, panel.getWidth(), panel.getHeight());
        
        JTextArea textComponent = new JTextArea();
        textComponent.setBackground(Color.BLUE);
        
        JScrollPane scrollPane = new JScrollPane(textComponent);
        Dimension size = panel.getSize();
        //scrollPane.getViewport().setSize(size);
        //scrollPane.setSize(size);
        scrollPane.setBackground(Color.GRAY);
        panel.add(scrollPane, BorderLayout.CENTER);
        //panel.add(textComponent, BorderLayout.CENTER);
        
        //textComponent.setSize(size);
        
        //final MessageConsole mc = new MessageConsole(textComponent);
        //mc.redirectOut();
        //mc.redirectErr(Color.RED, null);
        //mc.setMessageLines(100);
        //mc.redirectOut(null, System.out);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            builder.append("test\n");
            builder.append("test ").append(Integer.toHexString(i)).append('\n');
        }
        textComponent.setText(builder.toString());
        
        panel.doLayout();
        panel.setVisible(true);
    }


    @Subscribe
    public void touched(RawTouchEvent event) {
        System.err.println(event.getX() + " " + event.getY());
    }


    public static void main(String[] args) throws Exception {
        
        MessageConsoleExample piStormsMain = new MessageConsoleExample(parseRotation(args, 0));
        
        System.out.println("test");
        
        // the touch display starts a thread which emits events when the screen is touched
        Thread.sleep(TimeUnit.HOURS.toMillis(1));
        
        piStormsMain.piStormsDisplay.close();
    }
}
