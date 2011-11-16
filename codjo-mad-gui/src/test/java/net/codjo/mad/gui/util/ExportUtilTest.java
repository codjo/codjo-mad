package net.codjo.mad.gui.util;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import static net.codjo.mad.gui.util.ExportUtil.JAVA_IO_TMPDIR;
import static net.codjo.mad.gui.util.ExportUtil.computeTempFileName;
import static net.codjo.mad.gui.util.ExportUtil.generateFile;
import static net.codjo.mad.gui.util.ExportUtil.generateFileName;
import java.io.File;
import java.io.IOException;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class ExportUtilTest {
    private String tempDirProperty;


    @Before
    public void setUp() {
        tempDirProperty = System.getProperty(JAVA_IO_TMPDIR);
        System.setProperty(JAVA_IO_TMPDIR, ExportUtilTest.class.getResource("").getPath());
    }


    @After
    public void tearDown() {
        System.setProperty(JAVA_IO_TMPDIR, tempDirProperty);
    }


    @Test
    public void test_generateFile() throws IOException {
        assertEquals("fichier3.xls", generateFile(createGuiContext(), 4).getName());
    }


    @Test
    public void test_cannotGenerateFile() {
        File file = null;
        try {
            file = generateFile(createGuiContext(), 3);
            fail("The file " + file + " should not have been created !");
        }
        catch (IOException ex) {
            assertEquals("Cannot open the file : " + computeTempFileName("fichier3.xls"),
                         ex.getMessage());
            assertNull(file);
        }
    }


    @Test
    public void test_generateFileName() {
        assertEquals(computeTempFileName("fileBaseNamefileExt"),
                     generateFileName("fileBaseName", "fileExt", 0));
        assertEquals(computeTempFileName("fileBaseName9fileExt"),
                     generateFileName("fileBaseName", "fileExt", 9));
    }


    private static DefaultGuiContext createGuiContext() {
        DefaultGuiContext guiContext = new DefaultGuiContext();
        guiContext.putProperty(ExportUtil.FILE_BASE_NAME, "fichier");
        guiContext.putProperty(ExportUtil.FILE_EXT, ".xls");

        return guiContext;
    }
}
