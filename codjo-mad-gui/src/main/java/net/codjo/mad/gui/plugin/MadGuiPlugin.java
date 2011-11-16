package net.codjo.mad.gui.plugin;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.client.plugin.MadConnectionPlugin;
import net.codjo.mad.gui.base.GuiConfiguration;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.mad.gui.framework.Sender;
import net.codjo.mad.gui.i18n.AbstractInternationalizableGuiPlugin;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.mad.gui.request.requetor.RequetorLayerFactory;
import java.io.InputStream;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
/**
 *
 */
public class MadGuiPlugin extends AbstractInternationalizableGuiPlugin {
    private String configurationDirectoryPath;
    private Class windowClass;
    private MadGuiPluginConfiguration configuration = new MadGuiPluginConfiguration();
    private Logger logger = Logger.getLogger(MadGuiPlugin.class.getName());
    private MadConnectionPlugin madConnectionPlugin;


    public MadGuiPlugin(MadConnectionPlugin madConnectionPlugin) {
        this("/conf", MadGuiPlugin.class);
        this.madConnectionPlugin = madConnectionPlugin;
    }


    public MadGuiPlugin(String configurationDirectoryPath, Class mainClass) {
        this.configurationDirectoryPath = configurationDirectoryPath;
        this.windowClass = mainClass;
    }


    @Override
    protected void registerLanguageBundles(TranslationManager translationManager) {
        translationManager.addBundle("net.codjo.mad.gui.i18n", Language.FR);
        translationManager.addBundle("net.codjo.mad.gui.i18n", Language.EN);
    }


    @Override
    public void initGui(GuiConfiguration guiConfiguration) throws Exception {
        super.initGui(guiConfiguration);

        MutableGuiContext ctxt = guiConfiguration.getGuiContext();
        MadConnectionOperations connectionOperations = madConnectionPlugin.getOperations();
        ctxt.setSender(new Sender(connectionOperations));

        String fileName = computeFilePath(getConfiguration().getDefaultPreferenceFileName());
        InputStream inputStream = windowClass.getResourceAsStream(fileName);
        PreferenceFactory.initFactory();
        if (inputStream == null) {
            logger.warn("Impossible d'accéder au fichier de préférences " + fileName + ".");
        }
        else {
            PreferenceFactory.addMapping(new InputSource(inputStream));
        }
        RequetorLayerFactory.initFactory(computeFilePath("RequetorDef.xml"),
                                         guiConfiguration.getStructureReader());
    }


    public MadGuiPluginConfiguration getConfiguration() {
        return configuration;
    }


    private String computeFilePath(String fileName) {
        return configurationDirectoryPath + "/" + fileName;
    }


    public class MadGuiPluginConfiguration {
        private String defaultPreferenceFileName = "preference.xml";


        public String getDefaultPreferenceFileName() {
            return defaultPreferenceFileName;
        }


        public void addPreferenceMapping(String preferenceFileName) {
            InputSource inputSource =
                  new InputSource(windowClass.getResourceAsStream(computeFilePath(preferenceFileName)));
            addPreferenceMapping(inputSource);
        }


        public void addPreferenceMapping(InputSource inputSource) {
            PreferenceFactory.addMapping(inputSource);
        }
    }
}
