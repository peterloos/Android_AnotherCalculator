package de.peterloos.anothercalculator;

import android.util.Log;

public class TokenScanner {

    // input and history
    private StringBuilder currentInput;
    private StringBuilder currentHistory;

    // last recognized operand and operator
    private double lastOperand;
    private Operator lastOperator;

    // flags controlling interactive input
    private boolean twoOperandsExisting;
    private boolean replaceNextOperatorIfAny;
    private boolean resetInput;
    private boolean isBackspaceAllowed;
    private boolean isConsecutiveEqual;

    // c'tor
    public TokenScanner() {
        this.currentInput = new StringBuilder(32);
        this.currentHistory = new StringBuilder(32);
        this.reset();
    }

    // properties
    public String getCurrentInput() {
        StringBuilder copy = new StringBuilder(this.currentInput.toString());
        return this.rawToDisplay(copy);
    }

    public String getCurrentHistory() {
        return this.currentHistory.toString();
    }

    // public interface
    public void pushChar(char ch) {
        this.isBackspaceAllowed = true;

        if (this.resetInput) {
            this.currentInput.setLength(0);
            this.currentInput.append(ch);

            this.resetInput = false;
            this.replaceNextOperatorIfAny = false;
        } else if (this.currentInput.length() == 1) {
            if (this.currentInput.charAt(0) == '0') {
                this.currentInput.setCharAt(0, ch);
            } else {
                this.currentInput.append(ch);
            }
        } else {
            this.currentInput.append(ch);
        }

        this.dumpInputLine();
    }

    public void back() {
        if (!this.isBackspaceAllowed)
            return;

        boolean isNegative = false;
        if (this.currentInput.length() >= 2 && this.currentInput.charAt(0) == '-') {
            isNegative = true;
        }

        if (this.currentInput.length() == 1 && !isNegative) {
            this.currentInput.setLength(0);
            this.currentInput.append('0');
        } else if (this.currentInput.length() == 2 && isNegative) {
            this.currentInput.setLength(0);
            this.currentInput.append('0');
        } else {
            this.currentInput.deleteCharAt(this.currentInput.length() - 1);
        }

        this.dumpInputLine();
    }

    public void negate() {
        // should never occur
        if (this.currentInput.length() == 0)
            return;

        // don't negate zero
        if (this.currentInput.length() == 1 && this.currentInput.charAt(0) == '0')
            return;

        char sign = this.currentInput.charAt(0);
        if (sign != '-') {
            this.currentInput.insert(0, '-');
        } else {
            this.currentInput.deleteCharAt(0);
        }

        this.dumpInputLine();
    }

    public void comma() {
        if (this.resetInput) {
            this.resetInput = false;
            this.currentInput.setLength(0);
            this.currentInput.append("0,");
        } else if (this.currentInput.toString().indexOf(',') == -1) {
            this.currentInput.append(',');  // check for several commas
        }

        this.dumpInputLine();
    }

    public void pushOp(Operator op) {

        this.resetInput = true;           // input needs to be reset upon next input
        this.isBackspaceAllowed = false;  // prevent backspace key destroying current result
        this.isConsecutiveEqual = false;  // current operator isn't equal

        if (!this.replaceNextOperatorIfAny) {
            // convert input into numerical value
            double operand = this.parseInputAsDouble(this.currentInput.toString());

            // build new history
            this.currentHistory.append(this.operandToString(operand));
            this.currentHistory.append(this.operatorToString(op));

            if (this.twoOperandsExisting) {
                // evaluate operation
                this.lastOperand =
                        this.calculateValue(this.lastOperand, this.lastOperator, operand);
            } else {
                // no first operand: assign input to first operand
                this.lastOperand = operand;
                this.twoOperandsExisting = true;
            }

            // replace input with last operand or result of calculation
            this.currentInput.setLength(0);
            this.currentInput.append(this.operandToString(this.lastOperand));

            this.replaceNextOperatorIfAny = true;
        } else {
            this.currentHistory.delete(this.currentHistory.length() - 3, this.currentHistory.length());
            this.currentHistory.append(this.operatorToString(op));
        }

        // store current operator
        this.lastOperator = op;

        this.dumpInputLine();
    }

    public void equal() {
        if (!this.isConsecutiveEqual) {
            this.currentHistory.setLength(0);  // clear history buffer

            // calculate current calculation result
            double operand = this.parseInputAsDouble(this.currentInput.toString());
            double result = this.calculateValue(this.lastOperand, this.lastOperator, operand);

            // replace input buffer with result of operation
            this.currentInput.setLength(0);
            this.currentInput.append(this.operandToString(result));

            this.twoOperandsExisting = false; // clear last operand
            this.isBackspaceAllowed = false;  // prevent backspace key destroying current result
            this.isConsecutiveEqual = true;   // handle upcoming equal key, if any
            this.lastOperand = operand;       // and store second operator, if necessary
            this.replaceNextOperatorIfAny = false;  // in case of equal next operator should not be replaced
        } else {
            // calculate current calculation result
            double operand = this.parseInputAsDouble(this.currentInput.toString());
            double result = this.calculateValue(operand, this.lastOperator, this.lastOperand);

            // replace input buffer with result of operation
            this.currentInput.setLength(0);
            this.currentInput.append(this.operandToString(result));
        }

        this.dumpInputLine();
    }

    public void reset() {
        this.currentInput.setLength(0);
        this.currentInput.append('0');
        this.currentHistory.setLength(0);

        this.twoOperandsExisting = false;
        this.replaceNextOperatorIfAny = false;
        this.resetInput = false;
        this.isBackspaceAllowed = false;
        this.isConsecutiveEqual = false;

        this.lastOperand = 0.0;
        this.lastOperator = Operator.NoOp;

        this.dumpInputLine();
    }

    // private helper methods
    private double calculateValue(double firstOperand, Operator op, double secondOperand) {
        double result = 0.0;

        switch (op) {
            case AddOp:
                result = firstOperand + secondOperand;
                break;
            case SubOp:
                result = firstOperand - secondOperand;
                break;
            case MulOp:
                result = firstOperand * secondOperand;
                break;
            case DivOp:
                if (secondOperand != 0.0)
                    result = firstOperand / secondOperand;
                break;
        }

        return result;
    }

    private String rawToDisplay(StringBuilder sb) {
        if (sb.length() <= 3)
            return sb.toString();

        int exponentIndex = sb.toString().indexOf('e');
        if (exponentIndex == -1)
            exponentIndex = sb.toString().indexOf('E');

        if (exponentIndex >= 0)
            return sb.toString();

        int commaIndex = sb.toString().indexOf(',');
        int negativeSignIndex = sb.toString().indexOf('-');

        // retrieve part of number to extend with decimal points
        String result;
        if (commaIndex == -1) {
            if (negativeSignIndex == -1) {
                result = this.addDecimalSeparators(sb);
            } else {
                result = '-' + this.addDecimalSeparators(sb.delete(0, 1));
            }
        } else {
            if (negativeSignIndex == -1) {
                String integralPart = sb.substring(0, commaIndex);
                sb.delete(0, commaIndex);

                result = this.addDecimalSeparators(new StringBuilder(integralPart)) + sb.toString();
            } else {
                sb.delete(0, 1);  // remove '-' sign
                commaIndex--;     // comma index includes '-' sign

                String integralPart = sb.substring(0, commaIndex);
                sb.delete(0, commaIndex);

                result =
                        '-' +
                                this.addDecimalSeparators(new StringBuilder(integralPart)) +
                                sb.toString();
            }
        }

        return result;
    }

    private String addDecimalSeparators(StringBuilder sb) {
        String result = "";
        while (sb.length() > 3) {
            String triple = sb.substring(sb.length() - 3, sb.length());
            sb.delete(sb.length() - 3, sb.length());
            result = "." + triple + result;
        }

        result = sb.toString() + result;
        return result;
    }

    private String operatorToString(Operator op) {
        String result = "";

        switch (op) {
            case AddOp:
                result = " + ";
                break;
            case SubOp:
                result = " - ";
                break;
            case MulOp:
                result = " * ";
                break;
            case DivOp:
                result = " / ";
                break;
        }

        return result;
    }

    private double parseInputAsDouble(String s) {

        double d = 0.0;

        // need to replace comma with dot as decimal separator
        s = s.replace(',', '.');

        try {
            d = Double.parseDouble(s);
        } catch (Exception ex) {
            Log.e("Calculator", "NumberFormatException: WRONG INPUT: " + s);
        }

        return d;
    }

    private String operandToString(double operand) {

        Double d = Double.valueOf(operand);
        String result = d.toString();

        // string representation expects comma as a decimal separator
        result = result.replace('.', ',');

        // remove unnecessary decimal 0 at the end, if any
        if (result.length() >= 3) {
            if (result.substring(result.length() - 2).equals(",0")) {
                result = result.substring(0, result.length() - 2);
            }
        }

        return result;
    }

    private void dumpInputLine() {

        String msg = String.format("INPUT: %s", this.currentInput.toString());
        Log.v("Calculator", msg);
    }
}
