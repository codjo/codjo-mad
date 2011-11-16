package net.codjo.mad.client.plugin;
import net.codjo.agent.AclMessage.Performative;
import static net.codjo.agent.test.MessageBuilder.message;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import org.junit.Test;
/**
 *
 */
public class InstitueEventTest {
    @Test
    public void test_success() throws Exception {
        InstitueEvent event = InstitueEvent.successEvent();

        assertThat(event.hasFailed(), is(false));
        assertThat(event.getErrorMessage(), nullValue());
    }


    @Test
    public void test_failure() throws Exception {
        InstitueEvent event = InstitueEvent.failureEvent(
              message(Performative.FAILURE)
                    .withContent(new RuntimeException("erreur")).get());

        assertThat(event.getErrorMessage(), is("erreur"));
        assertThat(event.hasFailed(), is(true));
    }


    @Test
    public void test_failureBadContent() throws Exception {
        InstitueEvent event = InstitueEvent.failureEvent(
              message(Performative.FAILURE)
                    .withContent("badcontent").get());

        assertThat(event.hasFailed(), is(true));
        assertThat(event.getErrorMessage(), is(InstitueEvent.UNEXPECTED_INTERNAL_ERROR));
    }
}
