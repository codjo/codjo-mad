package net.codjo.mad.client.request;
/**
 */
public class CommandRequest extends AbstractRequest {
    private FieldsList args;


    public CommandRequest() {
    }


    public CommandRequest(String handlerId) {
        setId(handlerId);
    }


    public CommandRequest(String handlerId, FieldsList arguments) {
        setId(handlerId);
        setArgs(arguments);
    }


    public void setArgs(FieldsList args) {
        this.args = args;
    }


    public FieldsList getArgs() {
        return args;
    }


    public void addSelector(String pkName, String value) {
        if (getArgs() == null) {
            setArgs(new FieldsList());
        }
        getArgs().addField(pkName, value);
    }


    public String toXml() {
        return toXml("commandMapping.xml");
    }
}
