package net.codjo.mad.gui.menu;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.xml.XmlException;
import net.codjo.xml.easyxml.EasyXMLMapper;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
/**
 * Factory permettant de creer un menu ou une toolbar a partir d'un fichier de
 * description XML.
 * 
 * <p>
 * La factory utilise des GuiContext modifiable car les actions ne sont crées qu'une
 * seule fois et son mis dans contexte pour le cas d'une eventuelle réutilisation.
 * </p>
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.10 $
 */
public final class MenuFactory {
    private MenuFactory() {}

    public static JToolBar buildToolBar(URL configMenu, MutableGuiContext ctxt)
            throws BuildException {
        try {
            URL rulesFile = MenuFactory.class.getResource("ToolbarRules.xml");
            EasyXMLMapper easyXMLMapper = new EasyXMLMapper(configMenu, rulesFile);
            ToolBarBuilder builder = (ToolBarBuilder)easyXMLMapper.load();

            return builder.buildToolBar(ctxt);
        }
        catch (IOException ex) {
            throw new BuildException(ex);
        }
        catch (XmlException ex) {
            throw new BuildException(ex);
        }
    }


    public static JMenuBar buildMenuBar(URL configMenu, MutableGuiContext ctxt)
            throws BuildException {
        try {
            URL rulesFile = MenuFactory.class.getResource("MenuRules.xml");
            EasyXMLMapper easyXMLMapper = new EasyXMLMapper(configMenu, rulesFile);
            MenuBarBuilder builder = (MenuBarBuilder)easyXMLMapper.load();
            return builder.buildMenuBar(ctxt);
        }
        catch (IOException ex) {
            throw new BuildException(ex);
        }
        catch (XmlException ex) {
            throw new BuildException(ex);
        }
    }

    /**
     * Exception lancé lors de l'echec de la construction d'un menu {@link
     * MenuFactory#buildMenuBar(java.net.URL,
     * net.codjo.mad.gui.framework.MutableGuiContext)} et {@link
     * MenuFactory#buildToolBar(java.net.URL,
     * net.codjo.mad.gui.framework.MutableGuiContext)}.
     */
    public static final class BuildException extends Exception {
        private Exception causedBy;

        public BuildException(Exception causedBy) {
            super(causedBy.getLocalizedMessage());
            this.causedBy = causedBy;
        }

        public void printStackTrace(PrintWriter writer) {
            super.printStackTrace(writer);
            if (getCausedBy() != null) {
                writer.println(" ---- cause ---- ");
                getCausedBy().printStackTrace(writer);
            }
        }


        public Exception getCausedBy() {
            return causedBy;
        }


        public void printStackTrace() {
            super.printStackTrace();
            if (getCausedBy() != null) {
                System.err.println(" ---- cause ---- ");
                getCausedBy().printStackTrace();
            }
        }


        public void printStackTrace(PrintStream stream) {
            super.printStackTrace(stream);
            if (getCausedBy() != null) {
                stream.println(" ---- cause ---- ");
                getCausedBy().printStackTrace(stream);
            }
        }
    }
}
