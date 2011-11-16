package net.codjo.mad.gui.request.action;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestToolBar;
import java.lang.reflect.Method;
import javax.swing.JInternalFrame;
import org.apache.log4j.Logger;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
/**
 * Constructeur d'écran détail.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.10 $
 */
public class DetailWindowBuilder {
    private static final Logger LOGGER = Logger.getLogger(DetailWindowBuilder.class);
    protected final FatherContainer fatherContainer;


    public DetailWindowBuilder() {
        this((FatherContainer)null);
    }


    /**
     * DEPRECATED.
     *
     * @param toolbar
     *
     * @see #DetailWindowBuilder(FatherContainer)
     * @deprecated Utiliser la version avec {@link FatherContainer}.
     */
    @Deprecated
    public DetailWindowBuilder(RequestToolBar toolbar) {
        this((FatherContainer)toolbar);
    }


    public DetailWindowBuilder(FatherContainer fatherContainer) {
        this.fatherContainer = fatherContainer;
    }


    /**
     * Construit un écran détail.
     *
     * @param ds     la data source de l'écran détail
     * @param dsPref les préférences de la data source
     *
     * @return un écran détail
     *
     * @throws Exception             lors d'une erreur d'introspection
     * @throws NoSuchMethodException si la classe DetailWindow configurée dans dsPref n'a pas de constructeur
     *                               approprié
     */
    public JInternalFrame buildFrame(DetailDataSource ds, Preference dsPref) throws Exception {
        Class dwClass = dsPref.getDetailWindowClass();

        LOGGER.debug("instanciation d'un nouvelle fenêtre\npour la classe : " + dwClass
                     + "\navec le datasource : " + ds + "\net les preferences : " + dsPref);

        MutablePicoContainer pico = new DefaultPicoContainer();
        fillPicoContainer(pico, ds, dsPref);

        Object detailStuff = buildDetail(pico, dwClass);

        if (detailStuff instanceof JInternalFrame) {
            return (JInternalFrame)detailStuff;
        }
        else {
            // Puisque ce n'est pas une classe GUI, ce doit être une classe LOGIC.
            Method getGui;
            try {
                getGui = dwClass.getMethod("getGui");
            }
            catch (NoSuchMethodException ex) {
                throw new NoSuchMethodException("Manque méthode getGui() sur " + dwClass);
            }

            return (JInternalFrame)getGui.invoke(detailStuff);
        }
    }


    protected void fillPicoContainer(MutablePicoContainer pico, DetailDataSource ds, Preference dsPref) {
        pico.registerComponentInstance(DetailDataSource.class, ds);
        pico.registerComponentInstance(Preference.class, dsPref);
        if (null != fatherContainer) {
            pico.registerComponentInstance(FatherContainer.class, fatherContainer);

            if (fatherContainer.getFatherDataSource() != null) {
                Row selectedRow = fatherContainer.getFatherDataSource().getSelectedRow();
                pico.registerComponentInstance(Row.class, selectedRow);
            }
        }
    }


    private Object buildDetail(MutablePicoContainer pico, Class dwClass) {
        pico.registerComponentImplementation(dwClass);
        return pico.getComponentInstance(dwClass);
    }
}
