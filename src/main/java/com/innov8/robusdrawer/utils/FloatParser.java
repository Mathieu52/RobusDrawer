package com.innov8.robusdrawer.utils;
import static java.lang.Math.max;

public class FloatParser {
    public static boolean isFloat(String str) {
        //DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        //char decimalSeparator = symbols.getDecimalSeparator();

        // Regular expression to check if the string is a valid float with the current decimal separator
        String regex = String.format("(-?\\d*([%s]\\d*)?)?", '.');

        return str.matches(regex);
    }

    public static String normalizeNumberString(String text) {
        text = text.replaceAll("[\\D&&[^,.-]]", "");

        boolean isNegative = text.startsWith("-");

        if (isNegative) {
            text = text.substring(1);
        }

        int integerPartSize = max(text.indexOf(","), text.indexOf("."));
        boolean hasDecimalCharacter = text.contains(".") || text.contains(",");
        if (integerPartSize == -1) {
            integerPartSize = text.length();
        }

        String integralString = text.substring(0, integerPartSize);
        String decimalString = hasDecimalCharacter ? text.substring(integerPartSize + 1) : "";
        decimalString = decimalString.replaceAll("[.,]", "");

        text = integralString + (hasDecimalCharacter ? ("." + decimalString) : "");

        if (integerPartSize == 0) {
            text = "0" + text;
            integerPartSize++;
        }

        if (integerPartSize > 1) {
            while (integerPartSize > 1 && text.charAt(0) == '0') {
                text = text.substring(1);
                integerPartSize--;
            }
        }

        if (isNegative) {
            text = "-" + text;
        }

        return text;
    }
}