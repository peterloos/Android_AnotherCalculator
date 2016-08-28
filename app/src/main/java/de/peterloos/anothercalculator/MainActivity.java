package de.peterloos.anothercalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TokenScanner scanner;

    private TextView textviewHistory;
    private TextView textviewInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        // create calculator object
        this.scanner = new TokenScanner();

        // establishing event handlers
        this.textviewHistory = (TextView) findViewById(R.id.textbox_history);
        this.textviewInput = (TextView) findViewById(R.id.textbox_input);
        this.textviewInput.setText("0");

        Button[] numericButtons = new Button[10];
        numericButtons[0] = (Button) findViewById(R.id.button0);
        numericButtons[1] = (Button) findViewById(R.id.button1);
        numericButtons[2] = (Button) findViewById(R.id.button2);
        numericButtons[3] = (Button) findViewById(R.id.button3);
        numericButtons[4] = (Button) findViewById(R.id.button4);
        numericButtons[5] = (Button) findViewById(R.id.button5);
        numericButtons[6] = (Button) findViewById(R.id.button6);
        numericButtons[7] = (Button) findViewById(R.id.button7);
        numericButtons[8] = (Button) findViewById(R.id.button8);
        numericButtons[9] = (Button) findViewById(R.id.button9);

        for (int i = 0; i < 10; i ++) {
            final int index = i;
            numericButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addDigit((char) ('0' + index));
                }
            });
        }

        Button buttonPlus = (Button) findViewById(R.id.button_plus);
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });

        Button buttonMinus = (Button) findViewById(R.id.button_minus);
        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sub();
            }
        });

        Button buttonTimes = (Button) findViewById(R.id.button_times);
        buttonTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mul();
            }
        });

        Button buttonDivide = (Button) findViewById(R.id.button_divide);
        buttonDivide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                div();
            }
        });

        Button buttonReset = (Button) findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        Button buttonBack = (Button) findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        Button buttonEqual = (Button) findViewById(R.id.button_equal);
        buttonEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assign();
            }
        });

        Button buttonNegate = (Button) findViewById(R.id.button_negate);
        buttonNegate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                negate();
            }
        });

        Button buttonComma = (Button) findViewById(R.id.button_comma);
        buttonComma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comma();
            }
        });
    }

    // arithmetic operations
    private void add () {
        this.scanner.pushOp(Operator.AddOp);
        this.textviewInput.setText(this.scanner.getCurrentInput());
        this.textviewHistory.setText(this.scanner.getCurrentHistory());
    }

    private void sub () {
        this.scanner.pushOp(Operator.SubOp);
        this.textviewInput.setText(this.scanner.getCurrentInput());
        this.textviewHistory.setText(this.scanner.getCurrentHistory());
    }

    private void mul () {
        this.scanner.pushOp(Operator.MulOp);
        this.textviewInput.setText(this.scanner.getCurrentInput());
        this.textviewHistory.setText(this.scanner.getCurrentHistory());
    }

    private void div () {
        this.scanner.pushOp(Operator.DivOp);
        this.textviewInput.setText(this.scanner.getCurrentInput());
        this.textviewHistory.setText(this.scanner.getCurrentHistory());
    }

    // miscellaneous calculator functions
    private void addDigit(char digit)
    {
        this.scanner.pushChar(digit);
        this.textviewInput.setText(this.scanner.getCurrentInput());
    }

    private void assign () {
        this.scanner.equal();
        this.textviewInput.setText(this.scanner.getCurrentInput());
        this.textviewHistory.setText(this.scanner.getCurrentHistory());
    }

    private void reset() {
        this.scanner.reset();
        this.textviewInput.setText(this.scanner.getCurrentInput());
        this.textviewHistory.setText(this.scanner.getCurrentHistory());
    }

    private void back() {
        this.scanner.back();
        this.textviewInput.setText(this.scanner.getCurrentInput());
    }

    private void negate() {
        this.scanner.negate();
        this.textviewInput.setText(this.scanner.getCurrentInput());
    }

    private void comma() {
        this.scanner.comma();
        this.textviewInput.setText(this.scanner.getCurrentInput());
    }
}
