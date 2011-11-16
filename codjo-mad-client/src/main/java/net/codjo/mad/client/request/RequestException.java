package net.codjo.mad.client.request;
/**
 *
 */
public class RequestException extends Exception {
    private String type;


    public RequestException(String msg) {
        super(msg);
    }


    public RequestException(String msg, String type) {
        super(msg);
        this.type = type;
    }


    public RequestException(ErrorResult result) {
        super(result.getLabel());
        this.type = result.getType();
    }


    @Override
    public String toString() {
        if (type != null) {
            return super.toString() + "(" + type + ")";
        }
        else {
            return super.toString();
        }
    }
}
