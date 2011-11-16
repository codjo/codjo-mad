package net.codjo.mad.gui.plugin;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.mad.client.plugin.MadConnectionPluginMock;
import net.codjo.mad.gui.base.GuiConfigurationMock;
import net.codjo.mad.gui.request.PreferenceFactory;
import java.lang.reflect.Field;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class MadGuiPluginTest {
    private MadGuiPlugin guiPlugin;


    @Before
    public void setUp() throws Exception {
        resetPreferenceFactorySingleton();
        guiPlugin = new MadGuiPlugin(new MadConnectionPluginMock());
    }


    @Test
    public void test_configuration_preferenceFileName() throws Exception {
        String preferenceFileName = guiPlugin.getConfiguration().getDefaultPreferenceFileName();
        assertEquals("preference.xml", preferenceFileName);
    }


    @Test
    public void test_initContainer_doNothing() throws Exception {
        guiPlugin.initContainer(new ContainerConfiguration());
    }


    @Test
    public void test_initGui_initializePreference() throws Exception {
        checkPreferenceFailure("PreferenceFactory n'est pas initialisé, appelez la methode initFactory()");

        guiPlugin.initGui(new GuiConfigurationMock());

        checkPreferenceFailure("L'identifiant 'unknow-id' est inconnu.");
    }


    private static void checkPreferenceFailure(String expected) {
        try {

            PreferenceFactory.getPreference("unknow-id");
            fail();
        }
        catch (Exception ex) {
            assertEquals(expected, ex.getMessage());
        }
    }


    private static void resetPreferenceFactorySingleton() throws Exception {
        Field singletonField = PreferenceFactory.class.getDeclaredField("preferenceManager");
        singletonField.setAccessible(true);
        singletonField.set(null, null);
    }
}
