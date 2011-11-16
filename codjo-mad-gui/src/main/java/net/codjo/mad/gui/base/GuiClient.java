package net.codjo.mad.gui.base;

import java.net.URL;

/**
 * @deprecated use MadGuiCore
 */
@Deprecated
public class GuiClient extends MadGuiCore {
    public GuiClient() {
    }


    public GuiClient(URL menuConfigUrl, URL toolbarConfigUrl) {
        super(menuConfigUrl, toolbarConfigUrl);
    }
}
