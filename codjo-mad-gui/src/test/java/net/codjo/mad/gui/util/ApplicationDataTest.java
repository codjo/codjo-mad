package net.codjo.mad.gui.util;
import java.util.Properties;
import junit.framework.TestCase;
/**
 * Test <code>ApplicationData</code> .
 */
public class ApplicationDataTest extends TestCase {
    private Properties props;
    private Properties systemProperties;


    public void test_getLDaps() throws Exception {
        props.put("server.ldap.default", "AM");
        props.put("server.ldap.gdo2", "gdoLdap");

        ApplicationData data = new ApplicationData(props);
        ApplicationData.LDap[] ldaps = data.getLdaps();

        assertEquals(2, ldaps.length);
        assertEquals("AM", ldaps[0].getLabel());
        assertEquals("gdoLdap", ldaps[1].getLabel());
    }


    /**
     * Test que getServers renvoie tous les serveurs definie lorsqu'aucun serveur par defaut n'est configuré.
     */
    public void test_getServers_NoDefault() throws Exception {
        props.put("server.default.url", "NONE");
        ApplicationData data = new ApplicationData(props);
        ApplicationData.Server[] servers = data.getServers();

        assertEquals(4, servers.length);
        assertEquals("Développement", servers[0].getName());
        assertEquals("Intégration", servers[1].getName());
        assertEquals("Production", servers[2].getName());
        assertEquals("Recette", servers[3].getName());
    }


    /**
     * Test que getServers renvoie que le serveur definie par defaut lorsque la property "server.default.url"
     * est definie.
     */
    public void test_getServers_OneDefault() throws Exception {
        props.put("server.default.url", "server.url.production");
        props.put("server.url.production", "Production, t3://a7we019:7001");
        ApplicationData data = new ApplicationData(props);
        ApplicationData.Server[] servers = data.getServers();

        assertEquals(1, servers.length);
        assertEquals("Production", servers[0].getName());
        assertEquals("t3://a7we019:7001", servers[0].getUrl());
    }


    /**
     * Verifie que les property definie dans Systeme outrepasse les réglages du fichier de configuration.
     */
    public void test_getServers_OneDefault_FromSystemProperty()
          throws Exception {
        props.put("server.default.url", "NONE");
        props.put("server.url.production", "Production, t3://a7we019:7001");
        System.setProperty("server.default.url", "server.url.production");

        ApplicationData data = new ApplicationData(props);
        ApplicationData.Server[] servers = data.getServers();

        assertEquals(1, servers.length);
        assertEquals("Production", servers[0].getName());
        assertEquals("t3://a7we019:7001", servers[0].getUrl());
    }


    /**
     * Test que getVersion renvoie la version + la version de test en mode dev ou recette.
     */
    public void test_getVersion() throws Exception {
        props.put("server.default.url", "NONE");
        ApplicationData data = new ApplicationData(props);
        assertEquals("1.00.00.00", data.getVersion());

        props.put("server.default.url", "server.url.recette");
        data = new ApplicationData(props);
        assertEquals("1.00.00.00", data.getVersion());
    }


    /**
     * Test que getVersion renvoie la version (seulement) en mode production.
     */
    public void test_getVersion_EnProduction() throws Exception {
        props.put("server.default.url", "server.url.production");
        ApplicationData data = new ApplicationData(props);

        assertEquals("1.00.00.00", data.getVersion());
    }


    public void test_getIconReturnsNullIfNotDefined()
          throws Exception {
        assertNull(new ApplicationData(new Properties()).getIcon());
    }


    @Override
    protected void setUp() throws Exception {
        systemProperties = new Properties();
        systemProperties.putAll(System.getProperties());
        props = new Properties();

        props.put("login.default.name", "pims");
        props.put("login.default.pwd", "pims");

        props.put("application.name", "PIMS");
        props.put("application.version", "1.00.00.00");
        props.put("application.icon", "/images/logo.jpg");

        props.put("server.url.production", "Production, t3://a7we019:7001");
        props.put("server.url.recette", "Recette, t3://a7we019:7001");
        props.put("server.url.integration", "Intégration, t3://a7we019:7001");
        props.put("server.url.developpement", "Développement, t3://localhost:7001");

        props.put("server.default.url", "server.url.production");
        props.put("server.initialContext.factory", "weblogic.jndi.WLInitialContextFactory");
    }


    @Override
    protected void tearDown() {
        System.setProperties(systemProperties);
    }
}
