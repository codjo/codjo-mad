package net.codjo.mad.common;
import junit.framework.TestCase;
/**
 *
 */
public class ZipUtilTest extends TestCase {
    public void test_bigZip() throws Exception {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 1000; i++) {
            buffer.append("aaaaaaaaaaaaaaaaaaaaaaaaaaaaazeeeeeeeeeaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        }
        assertEquals(buffer.toString(), ZipUtil.unzip(ZipUtil.zip(buffer.toString())));
    }
}
