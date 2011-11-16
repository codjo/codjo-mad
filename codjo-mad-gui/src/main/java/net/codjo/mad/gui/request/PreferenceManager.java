package net.codjo.mad.gui.request;
import java.util.HashMap;
import java.util.Map;
/**
 * Le gestionnaire des préférences pour les fenetres de liste
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.5 $
 */
public class PreferenceManager {
    private Map<String, Preference> idToPreference = new HashMap<String, Preference>();


    public PreferenceManager() {
    }


    public Preference getPreferenceById(String preferenceId) {
        Preference pref = idToPreference.get(preferenceId);
        if (pref != null) {
            return new Preference(pref);
        }
        throw new IllegalArgumentException("L'identifiant '" + preferenceId + "' est inconnu.");
    }


    public boolean containsPreferenceId(String preferenceId) {
        return idToPreference.containsKey(preferenceId);
    }


    public void addPreference(Preference preference) {
        idToPreference.put(preference.getId(), preference);
    }
}
