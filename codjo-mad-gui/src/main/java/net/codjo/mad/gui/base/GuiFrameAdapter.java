/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.gui.base;
import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
/**
 * TODO.
 */
class GuiFrameAdapter extends JDesktopPane {
    @Override
    public Component add(Component comp) {
        if (comp instanceof JInternalFrame) {
            JInternalFrame internalFrame = (JInternalFrame)comp;

            internalFrame.addInternalFrameListener(new SetVisibleListener(internalFrame));
        }
        return super.add(comp);
    }


    private static class SetVisibleListener extends InternalFrameAdapter implements WindowListener {
        private final JInternalFrame internalFrame;
        private JFrame frame;


        SetVisibleListener(JInternalFrame internalFrame) {
            this.internalFrame = internalFrame;
            if (internalFrame.isVisible()) {
                internalFrameOpened(null);
            }
        }


        private void setIconImage() {
            Icon icon = internalFrame.getFrameIcon();
            if (icon == null) {
                return;
            }
            BufferedImage image =
                  new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
            icon.paintIcon(null, image.getGraphics(), 0, 0);
            frame.setIconImage(image);
        }


        @Override
        public void internalFrameOpened(InternalFrameEvent event) {
            frame = new JFrame(internalFrame.getTitle());

            setIconImage();

            frame.addWindowListener(this);
            frame.setContentPane(internalFrame.getContentPane());
            frame.pack();
            frame.setVisible(true);
        }


        public void windowOpened(WindowEvent event) {
        }


        public void windowClosing(WindowEvent event) {
            for (InternalFrameListener listener : internalFrame.getListeners(InternalFrameListener.class)) {
                listener.internalFrameClosing(buildEvent(event));
            }
        }


        public void windowClosed(WindowEvent event) {
            for (InternalFrameListener listener : internalFrame.getListeners(InternalFrameListener.class)) {
                listener.internalFrameClosed(buildEvent(event));
            }
        }


        public void windowIconified(WindowEvent event) {
            for (InternalFrameListener listener : internalFrame.getListeners(InternalFrameListener.class)) {
                listener.internalFrameIconified(buildEvent(event));
            }
        }


        public void windowDeiconified(WindowEvent event) {
            for (InternalFrameListener listener : internalFrame.getListeners(InternalFrameListener.class)) {
                listener.internalFrameDeiconified(buildEvent(event));
            }
        }


        public void windowActivated(WindowEvent event) {
            for (InternalFrameListener listener : internalFrame.getListeners(InternalFrameListener.class)) {
                listener.internalFrameActivated(buildEvent(event));
            }
        }


        public void windowDeactivated(WindowEvent event) {
            for (InternalFrameListener listener : internalFrame.getListeners(InternalFrameListener.class)) {
                listener.internalFrameDeactivated(buildEvent(event));
            }
        }


        private InternalFrameEvent buildEvent(WindowEvent event) {
            return new InternalFrameEvent(internalFrame, event.getID());
        }
    }
}
