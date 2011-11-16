package net.codjo.mad.gui.request.action;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.factory.SelectFactory;
import net.codjo.mad.gui.request.factory.UpdateFactory;

public class EditActionTest extends WindowActionTestCase {

    @Override
    protected void checkDetailDataSource(final DetailDataSource dataSource) {
        assertNotNull(dataSource.getLoadFactory());
        assertTrue(dataSource.getLoadFactory() instanceof SelectFactory);

        assertNotNull(dataSource.getSaveFactory());
        assertTrue(dataSource.getSaveFactory() instanceof UpdateFactory);

        assertEquals("myEntity", dataSource.getEntityName());
    }


    @Override
    protected String getActionName() {
        return "EditAction";
    }
}
