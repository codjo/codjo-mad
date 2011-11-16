package net.codjo.mad.gui.request.action;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.factory.InsertFactory;
/**
 * Description of the Class
 *
 * @author $Author: palmont $
 * @version $Revision: 1.4 $
 */
public class AddActionTest extends WindowActionTestCase {

    @Override
    protected void checkDetailDataSource(final DetailDataSource dataSource) {
        assertNull(dataSource.getLoadFactory());
        assertTrue(dataSource.getSaveFactory() instanceof InsertFactory);
        assertEquals("myEntity", dataSource.getEntityName());
    }


    @Override
    protected String getActionName() {
        return "AddAction";
    }
}
