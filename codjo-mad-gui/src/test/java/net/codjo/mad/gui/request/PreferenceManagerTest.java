package net.codjo.mad.gui.request;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
/**
 * Teste le manager des préférences.
 */
public class PreferenceManagerTest {

    @Test
    public void testPreferenceManager() {
        Preference prefA = new Preference();
        prefA.setId("A");

        PreferenceManager manager = new PreferenceManager();
        manager.addPreference(prefA);

        assertEquals(prefA.getId(), manager.getPreferenceById("A").getId());
    }


    /**
     * Verifie que le PreferenceManager renvoie une copie des preferences
     */
    @Test
    public void test_preferenceManager_copy() {
        Preference prefA = new Preference();
        prefA.setId("A");
        prefA.setDeleteId("deleteA");

        PreferenceManager manager = new PreferenceManager();
        manager.addPreference(prefA);

        Preference copyDeA = manager.getPreferenceById("A");
        copyDeA.setDelete(null);
        assertNotNull(manager.getPreferenceById("A").getDelete());
    }
}
