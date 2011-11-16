package net.codjo.mad.gui.request.util;
/**
 *
 */
public interface ButtonLogicValidator {
    ButtonLogicValidator ALWAYS_VALID = new ButtonLogicValidator() {
        public boolean isValid() {
            return true;
        }
    };


    boolean isValid();
}
