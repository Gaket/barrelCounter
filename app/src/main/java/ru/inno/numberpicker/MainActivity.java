package ru.inno.numberpicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

import ru.inno.innopicker.WheelCounter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WheelCounter counterView = (WheelCounter) findViewById(R.id.numberPicker1);

        final EditText numberInput = (EditText) findViewById(R.id.number_input_view);
        numberInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    counterView.setNumber(Integer.valueOf(numberInput.getText().toString()));
                }
                return false;
            }
        });

        findViewById(R.id.increment_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterView.increment();
            }
        });
        findViewById(R.id.decrement_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterView.decrement();
            }
        });
        findViewById(R.id.random_value_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                int lenght = counterView.getValueString().length();
                int randValue = r.nextInt((int)Math.round(Math.pow(10, lenght)));
                counterView.setNumber(randValue);
            }
        });
    }
}
