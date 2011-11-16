package net.codjo.mad.server.handler.util;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import org.junit.Test;
public class QueryUtilTest {
    @Test
    public void test_replaceUser() throws Exception {
        String result = QueryUtil.replaceUser("select $user$", "gonnot");
        assertThat(result, equalTo("select 'gonnot'"));
    }
}

