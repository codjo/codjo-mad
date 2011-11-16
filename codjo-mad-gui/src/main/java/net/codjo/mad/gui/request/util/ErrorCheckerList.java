package net.codjo.mad.gui.request.util;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * TODO.
 *
 * @version $Revision: 1.4 $
 */
public class ErrorCheckerList {
    private List list = new ArrayList();

    public boolean hasError() {
        Iterator iter = list.iterator();

        while (iter.hasNext()) {
            ErrorChecker checker = (ErrorChecker)iter.next();

            if (checker.hasError()) {
                return true;
            }
        }

        return false;
    }


    public void clearError() {
        Iterator iter = list.iterator();

        while (iter.hasNext()) {
            ErrorChecker checker = (ErrorChecker)iter.next();

            checker.clearError();
        }
    }


    public void add(ErrorChecker checker) {
        list.add(checker);
    }
}
