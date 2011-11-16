package net.codjo.mad.server.plugin;
import net.codjo.aspect.AspectConfigException;
import net.codjo.mad.server.MadRequestContext;
import net.codjo.mad.server.handler.DefaultHandlerMap;
import net.codjo.mad.server.handler.Processor;
import java.util.HashMap;
import java.util.Map;
/**
 *
 */
public class ProcessorMock extends Processor {
    MadRequestContext lastContext;
    String lastRequest;
    private RuntimeException mockProceedError;
    private final Map<String, String> results = new HashMap<String, String>();


    public ProcessorMock() throws AspectConfigException {
        super(new DefaultHandlerMap(), BackPackBuilder.init().get());
    }


    @Override
    public String proceed(String xmlRequests, MadRequestContext madRequestContext) {
        String result = results.get(xmlRequests);
        if (result != null) {
            return result;
        }

        lastContext = madRequestContext;
        lastRequest = xmlRequests;
        if (mockProceedError != null) {
            mockProceedError.fillInStackTrace();
            throw mockProceedError;
        }
        return RequestParticipantTest.RESULT_MOCK;
    }


    public void mockProceedError(RuntimeException proceedError) {
        mockProceedError = proceedError;
    }


    public ProcessorMock mockProceed(String request, String expectedResult) {
        results.put(request, expectedResult);
        return this;
    }
}
