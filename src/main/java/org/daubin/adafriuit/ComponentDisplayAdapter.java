package org.daubin.adafriuit;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class ComponentDisplayAdapter {
    /**
     * Tracks components which have been pressed so that their state can be reset when they
     * are no longer pressed.
     */
    private final Set<Component> interactions = Sets.newConcurrentHashSet();
    private final Container container;
    private final EventBus eventBus;
    private final Graphics graphics;
    
    private final ComponentListener componentListener = new ComponentListener() {
        
        @Override
        public void componentShown(ComponentEvent e) {
            fireRepaint(e.getComponent().getBounds());
        }
        
        @Override
        public void componentResized(ComponentEvent e) {
            fireRepaint(e.getComponent().getBounds());
        }
        
        @Override
        public void componentMoved(ComponentEvent e) {
            fireRepaint(e.getComponent().getBounds());
        }
        
        @Override
        public void componentHidden(ComponentEvent e) {
            fireRepaint(e.getComponent().getBounds());
        }
    };
    
    private final Map<Object, AutoCloseable> componentCleanupTasks = Maps.newConcurrentMap();
    
    private final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
        
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            // the paintingForPrint event fires every time we call container.paint.  we must ignore it or 
            // infinitely loop
            if (!"paintingForPrint".equals(e.getPropertyName()) && e.getSource() instanceof Component) {
                fireRepaint(((Component)e.getSource()).getBounds());
            }
        }
    };
    
    public ComponentDisplayAdapter(Container container, final Graphics graphics, EventBus eventBus) {
        this.container = container;
        this.eventBus = eventBus;
        this.graphics = graphics;
        
        // this container listener notices every component added to the container and adds
        // listeners to notice changes and fire redraws
        ContainerListener containerListener = new ContainerListener() {

            @Override
            public void componentRemoved(ContainerEvent e) {
                removeComponent(e.getChild());
                if (e.getChild() instanceof Container) {
                    ((Container)e.getChild()).removeContainerListener(this);
                }
            }
            
            @Override
            public void componentAdded(ContainerEvent e) {
                addComponent(e.getChild());
                if (e.getChild() instanceof Container) {
                    ((Container)e.getChild()).addContainerListener(this);
                }
            }
        };
        container.addContainerListener(containerListener);
        
        eventBus.register(this);
    }
    
    private void removeComponent(Component component) {
        fireRepaint(component.getBounds());
        AutoCloseable closeable = componentCleanupTasks.get(component);
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void addComponent(final Component component) {
        fireRepaint(component.getBounds());
        
        if (component instanceof Container) {
            for (Component c : ((Container)component).getComponents()) {
                addComponent(c);
            }
        }
        
        final List<AutoCloseable> closeables = Lists.newArrayList();
        component.addComponentListener(componentListener);
        component.addPropertyChangeListener(propertyChangeListener);
        
        closeables.add(new AutoCloseable() {
            
            @Override
            public void close() throws Exception {
                component.removeComponentListener(componentListener);
                component.removePropertyChangeListener(propertyChangeListener);
            }
        });
        
        final DocumentListener documentListener = new DocumentListener() {
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void insertUpdate(DocumentEvent e) {
                fireRepaint(component.getBounds());
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
//                System.err.println("changedUpdate");
            }
        };
        
        if (component instanceof JTextComponent) {
            ((JTextComponent)component).getDocument().addDocumentListener(documentListener);
            closeables.add(new AutoCloseable() {
                
                @Override
                public void close() throws Exception {
                    ((JTextComponent)component).getDocument().removeDocumentListener(documentListener);
                }
            });
        }
        componentCleanupTasks.put(component, new AutoCloseable() {
            
            @Override
            public void close() throws Exception {
                for (AutoCloseable c : closeables) {
                    c.close();
                }
            }
        });
    }

    @Subscribe
    public void touchEvent(RawTouchEvent event) {
        int x = event.getX(), y = event.getY();        
        for (Component c : interactions.toArray(new Component[0])) {
            if (c instanceof AbstractButton) {
                if (!buttonPressed(x, y, (AbstractButton) c)) {
                    interactions.remove(c);
                }
            }   
        }
        Component pressedComponent = container.getComponentAt(x, y);
        
//            System.out.println("Touch event: " + x + ", " + y + " " + pressedComponent);
        if (pressedComponent instanceof AbstractButton) {
            buttonPressed(x, y, (AbstractButton) pressedComponent);                                
        }
        if (null != pressedComponent && container != pressedComponent) {
            interactions.add(pressedComponent);
        }
    }
    

    private boolean buttonPressed(int x, int y, AbstractButton b) {
        boolean pressed = b.getBounds().contains(x,y);
        if (b.getModel().isPressed() != pressed) {
            b.getModel().setPressed(pressed);
                                
            if (pressed) {
                ActionEvent event = new ActionEvent(b, 666, b.getText());
                for (ActionListener l : b.getActionListeners()) {
                    l.actionPerformed(event);
                }
            }
            
            fireRepaint(b.getBounds());
        }
        return pressed;
    }

    private void fireRepaint(Rectangle bounds) {
        this.container.print(graphics);
        eventBus.post(new RepaintEvent(bounds));
    }
    
}
