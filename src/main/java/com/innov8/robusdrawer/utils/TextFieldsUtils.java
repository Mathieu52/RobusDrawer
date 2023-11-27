// 420-203-A22 – TP3 – RE
// By : Durand, Mathieu (2133200) & Bernier, Liam (2136435)
package com.innov8.robusdrawer.utils;

import javafx.scene.control.TextField;

import static com.innov8.robusdrawer.utils.FloatParser.normalizeNumberString;


/**
 * Utility for managing TextFields
 *
 * @author Mathieu Durand
 */
public class TextFieldsUtils {

    /**
     * Forces only numbers to be present in textField's text
     *
     * @param textField TextField which text will be forced to contain only numbers
     */
    public static void forceNumberOnlyFloat(TextField textField) {
        //  Adds a listener which removes all non digit characters from textField's text everytime it's modified
        textField.textProperty().addListener((observable, oldValue, newValue) -> textField.setText(normalizeNumberString(newValue)));
    }
    public static void forceNumberOnlyInt(TextField textField) {
        //  Adds a listener which removes all non digit characters from textField's text everytime it's modified
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("\\D", ""));
            }
        });
    }
}
