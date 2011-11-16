package net.codjo.mad.gui.base;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.util.ApplicationData;
import net.codjo.mad.gui.util.InternationalizableGuiContext;
import net.codjo.mad.gui.MadGuiContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.junit.Test;
import org.uispec4j.Panel;
/**
 *
 */
public class MainWindowTest {

    @Test
    public void test_componentBuilders() throws Exception {
        List<ComponentBuilder> componentBuilders = new ArrayList<ComponentBuilder>();

        componentBuilders.add(new ComponentBuilderMock("MyFirstStatusLabel"));
        componentBuilders.add(new ComponentBuilderMock("MySecondStatusLabel"));

        MainWindow mainWindow = new MainWindow(createApplicationData());
        mainWindow.finishDisplay(new LocalGuiContext(new MadGuiContext()), componentBuilders);

        Panel panel = new Panel(mainWindow.getStatusBar());

        panel.containsLabel("MyFirstStatusLabel").check();
        panel.containsLabel("MySecondStatusLabel").check();
    }


    private static ApplicationData createApplicationData() {
        Properties properties = new Properties();
        properties.setProperty("application.name", "GABI");
        return new ApplicationData(properties);
    }


    private static class ComponentBuilderMock implements ComponentBuilder {
        private String text;


        private ComponentBuilderMock(String text) {
            this.text = text;
        }


        public JComponent build() {
            return new JLabel(text);
        }
    }
}
