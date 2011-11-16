package net.codjo.mad.gui.base;
import net.codjo.gui.toolkit.progressbar.ProgressBarLabel;
import net.codjo.mad.gui.util.ApplicationData;
import net.codjo.mad.gui.MadGuiContext;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.uispec4j.TextBox;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
/**
 * Classe de test de {@link ApplicationWindow}.
 */
public class ApplicationWindowTest extends UISpecTestCase {
    private static final String APPLICATION_TITLE = "Application-Title";


    public void test_constructor() throws Exception {
        ApplicationWindow applicationWindow =
              new ApplicationWindow(APPLICATION_TITLE, createApplicationData());

        assertEquals(APPLICATION_TITLE, applicationWindow.getTitle());
        assertEquals(new Dimension(1200, 800), applicationWindow.getSize());
        assertEquals(JFrame.DISPOSE_ON_CLOSE, applicationWindow.getDefaultCloseOperation());

        assertIcon("exit", ".png");
        assertIcon("cut", ".png");
        assertIcon("copy", ".png");
        assertIcon("paste", ".png");
        assertIcon("undo", ".png");
        assertIcon("redo", ".png");
        assertIcon("find", ".png");
        assertIcon("delete", ".png");
    }


    public void test_statusbar() throws Exception {
        ApplicationWindow applicationWindow =
              new ApplicationWindow(APPLICATION_TITLE, createApplicationData());
        applicationWindow.initI18nComponents(new MadGuiContext());
        Window window = new Window(applicationWindow);

        assertNotNull(applicationWindow.getStatusBar());

        TextBox textBox = window.getTextBox("progressBar");
        ProgressBarLabel progressBar = (ProgressBarLabel)textBox.getAwtComponent();

        assertEquals("Barre d'état de l'application", progressBar.getToolTipText());
        assertFalse(progressBar.isRequestFocusEnabled());
        assertEquals("Bienvenue dans " + APPLICATION_TITLE, textBox.getText());
    }


    public void test_applicationIconIsSetFromApplicationData()
          throws Exception {
        new ApplicationWindow(APPLICATION_TITLE, createApplicationData());
        assertNull(UIManager.get("icon"));

        Properties properties = new Properties();
        properties.setProperty("application.icon", "/resources/images/icon.gif");
        ApplicationData applicationData = new ApplicationData(properties);

        new ApplicationWindow(APPLICATION_TITLE, applicationData);
        assertIcon("icon", ".gif");
    }


    public static void main(String[] args) {
        ApplicationWindow applicationWindow = new ApplicationWindow("myFrame", createApplicationData());
        JButton button1 = new JButton("myButton");
        button1.setBorderPainted(false);
        button1.setFocusable(false);
        applicationWindow.getStatusBar().add(button1);
        JButton button2 = new JButton(
              new ImageIcon(ApplicationWindowTest.class.getResource("/images/mad.load.gif")));
        button2.setBorderPainted(false);
        button2.setFocusable(false);
        button2.setMargin(new Insets(0, 0, 0, 0));
        applicationWindow.getStatusBar().add(button2);
        applicationWindow.setSize(640, 480);
        applicationWindow.setLocationRelativeTo(null);
        applicationWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        applicationWindow.setVisible(true);
    }


    private static ApplicationData createApplicationData() {
        Properties properties = new Properties();
        properties.setProperty("application.name", "GABI");
        return new ApplicationData(properties);
    }


    private void assertIcon(String name, String extension) {
        ImageIcon imageIcon =
              new ImageIcon(ApplicationWindow.class.getResource("/resources/images/" + name
                                                                + extension));
        assertEquals(imageIcon.toString(), UIManager.get(name).toString());
    }
}
