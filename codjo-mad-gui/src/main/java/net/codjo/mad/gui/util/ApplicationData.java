package net.codjo.mad.gui.util;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
/**
 * Cette classe contient le paramétrage d'une application (versionning,serveurs sybase,utilisateur par
 * défaut...) .
 *
 * <p> Cf le fichier se trouvant ici \Lib\Common\private\Application.properties pour un exemple. </p>
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.7 $
 */
public class ApplicationData {
    private Server[] servers = null;
    private Properties data;
    private LDap[] ldaps;


    public ApplicationData(InputStream confFile) throws IOException {
        Properties props = new Properties();
        props.load(confFile);
        init(props);
    }


    public ApplicationData(Properties props) {
        init(props);
    }


    /**
     * Retourne toutes les proprietes définie dans le fichier de configuration.
     *
     * @return Les properties
     */
    public Properties getData() {
        return data;
    }


    public String getDefaultLogin() {
        return data.getProperty("login.default.name");
    }


    public String getDefaultPassword() {
        return data.getProperty("login.default.pwd");
    }


    public javax.swing.Icon getIcon() {
        String iconProperty = data.getProperty("application.icon");
        if (iconProperty == null) {
            return null;
        }
        return new javax.swing.ImageIcon(ApplicationData.class.getResource(iconProperty));
    }


    public String getInitialContextFactory() {
        return data.getProperty("server.initialContext.factory");
    }


    public String getName() {
        return data.getProperty("application.name");
    }


    public Server[] getServers() {
        return servers;
    }


    public LDap[] getLdaps() {
        return ldaps;
    }


    public String getVersion() {
        return data.getProperty("application.version");
    }


    private Server buildServer(String serverData) {
        StringTokenizer tokenizer = new StringTokenizer(serverData, ",");

        if (tokenizer.countTokens() != 2) {
            throw new IllegalArgumentException();
        }
        return new Server(tokenizer.nextToken().trim(), tokenizer.nextToken().trim());
    }


    private void init(Properties props) {
        data = new Properties();
        data.putAll(props);
        data.putAll(System.getProperties());

        String defaultServerData = data.getProperty(data.getProperty("server.default.url", "NONE"));
        if (defaultServerData != null) {
            servers = new Server[]{buildServer(defaultServerData)};
        }
        else {
            Set<Server> serverSet = new TreeSet<Server>();
            for (Enumeration iter = data.keys(); iter.hasMoreElements();) {
                String key = (String)iter.nextElement();
                if (key.startsWith("server.url.")) {
                    serverSet.add(buildServer(data.getProperty(key)));
                }
            }
            servers = serverSet.toArray(new Server[serverSet.size()]);
        }

        buildLdapProperties();
    }


    private void buildLdapProperties() {
        Set<LDap> ldapSet = new TreeSet<LDap>();
        for (Enumeration iter = data.keys(); iter.hasMoreElements();) {
            String key = (String)iter.nextElement();
            if (key.startsWith("server.ldap.")) {
                String[] keySplitted = key.split("\\.");
                ldapSet.add(new LDap(keySplitted[keySplitted.length - 1], data.getProperty(key)));
            }
        }
        ldaps = ldapSet.toArray(new LDap[ldapSet.size()]);
    }


    /**
     * Classe deccrivant un serveur/base SYBASE.
     *
     * @author $Author: gaudefr $
     * @version $Revision: 1.7 $
     */
    public final class Server implements Comparable {
        private String name;
        private String url;


        Server(String name, String url) {
            if (name == null || url == null) {
                throw new IllegalArgumentException();
            }
            this.name = name;
            this.url = url;
        }


        public String getName() {
            return name;
        }


        public String getUrl() {
            return url;
        }


        public int compareTo(Object obj) {
            return name.compareTo(((Server)obj).getName());
        }


        @Override
        public String toString() {
            return name;
        }
    }
    public final class LDap implements Comparable {
        private String name;
        private String label;


        public LDap(String name, String label) {
            this.name = name;
            this.label = label;
        }


        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }


        public String getLabel() {
            return label;
        }


        public void setLabel(String label) {
            this.label = label;
        }


        public int compareTo(Object o) {
            return name.compareTo(((LDap)o).getName());
        }


        @Override
        public String toString() {
            return label;
        }
    }
}
