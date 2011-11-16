package net.codjo.mad.gui.base;
import java.awt.Dimension;
import java.net.URL;
/**
 *
 */
public interface MadGuiCoreConfiguration {

    URL getMenuConfigUrl();


    void setMenuConfigUrl(URL menuConfigUrl);


    URL getToolbarConfigUrl();


    void setToolbarConfigUrl(URL toolbarConfigUrl);


    Dimension getMainWindowSize();


    void setMainWindowSize(Dimension dimension);
}
