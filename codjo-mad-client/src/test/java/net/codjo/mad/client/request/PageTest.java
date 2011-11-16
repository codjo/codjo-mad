package net.codjo.mad.client.request;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
import org.junit.Test;

public class PageTest {
    @Test
    public void test_mokTest() throws Exception {
    }

/*

    @Test
    public void test_equals() throws Exception {
        Page page = createPage("2", "45");
        //noinspection ObjectEqualsNull
        assertThat(page.equals(null), equalTo(false));
        assertThat(page.equals(createPage(null, null)), equalTo(false));
        assertThat(page.equals(createPage("1", "78")), equalTo(false));
        assertThat(page.equals(createPage("2", "45")), equalTo(true));
    }


    @Test
    public void test_hashCode() throws Exception {
        Page page = createPage(null, null);
        assertThat(page.hashCode(), equalTo(0));
        page = createPage("40", "2");
        assertThat(page.hashCode(), equalTo(createPage("40", "2").hashCode()));
    }


    private static Page createPage(String num, String rows) {
        Page page = new Page();
        page.setNum(num);
        page.setRows(rows);
        return page;
    }
*/
}
