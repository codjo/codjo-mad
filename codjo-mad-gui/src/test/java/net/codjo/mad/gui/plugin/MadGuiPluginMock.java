package net.codjo.mad.gui.plugin;
import net.codjo.mad.gui.base.GuiConfiguration;
import net.codjo.mad.gui.request.PreferenceFactory;
import java.io.StringReader;
import org.xml.sax.InputSource;
/**
 *
 */
public class MadGuiPluginMock extends MadGuiPlugin {
    private String preference;


    public MadGuiPluginMock() {
        super(null);
    }


    public MadGuiPluginMock(String preference) {
        super(null);
        this.preference = preference;
    }


    @Override
    public void initGui(GuiConfiguration guiConfiguration) {
        if (preference != null) {
            PreferenceFactory.loadMapping(new InputSource(new StringReader(preference)));
        }
    }
}

