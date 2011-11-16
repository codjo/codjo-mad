package net.codjo.mad.client.request;
/**
 * Classe decrivant la balise erreur du resultat.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public class ErrorResult {
    private String label;
    private String requestId;
    private String type;

    public ErrorResult() {}

    public void setLabel(String label) {
        this.label = label;
    }


    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getLabel() {
        return label;
    }


    public String getRequestId() {
        return requestId;
    }


    public String getType() {
        return type;
    }
}
