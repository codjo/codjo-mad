package net.codjo.mad.gui.request.factory;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.DataSource;
/**
 *
 */
public interface DataSourceFactory<T> {

    public DataSource buildDataSource(GuiContext guiContext, T component);
}
