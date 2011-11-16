package net.codjo.mad.gui.request.util.selection;
import net.codjo.mad.gui.request.JoinKeys;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * Classe gérant les jointures entre les tables "from", "to" (son) et "father" pour la
 * sélection.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.10 $
 *
 * @see JoinKeys
 * @deprecated Utilisez la classe JoinKeys.
 */
public class DSKeys {
    private String[] fromKeys;
    private String[] toKeys;
    private Map sonKeyMap;

    public DSKeys(String fromKeyName, String toKeyName, String fatherSonKeyName) {
        fromKeys = new String[] {fromKeyName};
        toKeys = new String[] {toKeyName};

        sonKeyMap = new HashMap();
        sonKeyMap.put(fatherSonKeyName, fatherSonKeyName);
    }


    public DSKeys(String fromKeyName, String toKeyName, Map sonKeyMap) {
        fromKeys = new String[] {fromKeyName};
        toKeys = new String[] {toKeyName};
        this.sonKeyMap = sonKeyMap;
    }


    public DSKeys(String[] fromKeysName, String[] toKeysName, String fatherSonKeyName) {
        fromKeys = fromKeysName;
        toKeys = toKeysName;
        sonKeyMap = new HashMap();
        sonKeyMap.put(fatherSonKeyName, fatherSonKeyName);
    }


    public DSKeys(String[] fromKeysName, String[] toKeysName, Map sonKeyMap) {
        fromKeys = fromKeysName;
        toKeys = toKeysName;
        this.sonKeyMap = sonKeyMap;
    }

    public String[] getFromKeys() {
        return fromKeys;
    }


    public String[] getToKeys() {
        return toKeys;
    }


    public Map getSonKeyMap() {
        return sonKeyMap;
    }


    public String toString() {
        return "[fromKey : " + fromKeys + "]  [toKey : " + toKeys + "]  [sonKeyMap : "
        + sonKeyMap + "]";
    }


    public JoinKeys toJoinKeys() {
        JoinKeys joinKeys = new JoinKeys();

        for (Iterator iter = sonKeyMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry)iter.next();
            String fatherField = (String)entry.getKey();
            String sonField = (String)entry.getValue();
            joinKeys.addAssociation(fatherField, sonField);
        }

        return joinKeys;
    }
}
