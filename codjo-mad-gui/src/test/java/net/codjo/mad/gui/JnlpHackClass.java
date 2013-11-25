package net.codjo.mad.gui;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
/**
 *
 */
public class JnlpHackClass {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    System.out.println(" Classloader:  " + Thread.currentThread().getContextClassLoader());
                    UIManager.setLookAndFeel(
                          "net.codjo.mad.gui.JnlpHackClass$MyLookAndFeel");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                JTextArea textArea = new JTextArea(" The quick brown fox jumped over the lazy dog. How rude. ", 50, 10);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);

                JFrame frame = new JFrame(" Java Web Start Look & Feel Test ");
                frame.setBounds(50, 50, 200, 200);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                Container contentPane = frame.getContentPane();
                contentPane.setLayout(new BorderLayout());
                contentPane.add(textArea, BorderLayout.CENTER);

                frame.setVisible(true);
            }
        });
    }


    public static class MyLookAndFeel extends WindowsLookAndFeel {
        private static final long serialVersionUID = -7686127811595151510L;
    }
}


