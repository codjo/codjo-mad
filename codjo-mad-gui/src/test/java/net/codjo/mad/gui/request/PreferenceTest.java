package net.codjo.mad.gui.request;
import junit.framework.TestCase;
/**
 *
 */
public class PreferenceTest extends TestCase {
    public void test_constructor() throws Exception {
        Preference preference = new Preference();

        assertNull(preference.getSelectAll());
        assertNull(preference.getSelectByPk());
        assertNull(preference.getUpdate());
        assertNull(preference.getDelete());
        assertNull(preference.getInsert());
        assertNull(preference.getRequetor());

        assertNull(preference.getDetailWindowClass());

        assertNull(preference.getId());

        assertNotNull(preference.getColumns());
        assertNotNull(preference.getHiddenColumns());
    }


    public void test_copyConstructor() throws Exception {
        Preference preference = new Preference();
        preference.setId("id");
        preference.setSelectAllId("setSelectAll");
        preference.setSelectByPkId("setSelectByPk");
        preference.setUpdateId("update");
        preference.setInsertId("insert");
        preference.setDeleteId("delete");
        preference.setRequetorId("requetor");
        preference.getColumns().add(new Column("colunm", "libellé"));
        preference.getHiddenColumns().add(new Column("hidden colunm", "caché"));

        preference.setDetailWindowClass(getClass());
        Preference copy = new Preference(preference);

        assertEquals(preference.getId(), copy.getId());
        assertEquals(preference.getSelectAll().getId(), copy.getSelectAll().getId());
        assertEquals(preference.getSelectByPk().getId(), copy.getSelectByPk().getId());
        assertEquals(preference.getUpdate().getId(), copy.getUpdate().getId());
        assertEquals(preference.getInsert().getId(), copy.getInsert().getId());
        assertEquals(preference.getDelete().getId(), copy.getDelete().getId());
        assertEquals(preference.getRequetor().getId(), copy.getRequetor().getId());
        assertEquals(1, copy.getColumnsName().length);
        assertEquals("colunm", copy.getColumnsName()[0]);
        assertEquals(preference.getDetailWindowClass(), copy.getDetailWindowClass());

        assertEquals(preference.getHiddenColumns(), copy.getHiddenColumns());
    }


    public void test_getColumnsName() throws Exception {
        Preference preference = new Preference();

        preference.getColumns().add(new Column("column", "libellé"));
        preference.getHiddenColumns().add(new Column("hidden colunm", "caché"));

        assertEquals(1, preference.getColumnsName().length);
        assertEquals("column", preference.getColumnsName()[0]);
    }
}
