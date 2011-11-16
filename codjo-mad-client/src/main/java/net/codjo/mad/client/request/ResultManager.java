package net.codjo.mad.client.request;
import java.util.Iterator;
/**
 * Description of the Class
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public class ResultManager {
    private ErrorResult errorResult;
    private java.util.Collection results;

    public ResultManager() {}

    public void setErrorResult(ErrorResult errorResult) {
        this.errorResult = errorResult;
    }


    public void setResults(java.util.Collection results) {
        this.results = results;
    }


    public ErrorResult getErrorResult() {
        return errorResult;
    }


    public Result getResult(String resultId) {
        for (Iterator i = getResults().iterator(); i.hasNext();) {
            Result item = (Result)i.next();
            if (resultId.equals(item.getRequestId())) {
                return item;
            }
        }
        throw new IllegalArgumentException("Aucun résultat pour l'id = " + resultId);
    }


    public java.util.Collection getResults() {
        return results;
    }


    public int getResultsCount() {
        if (getResults() == null) {
            return 0;
        }
        return getResults().size();
    }


    public boolean hasError() {
        return getErrorResult() != null;
    }
}
