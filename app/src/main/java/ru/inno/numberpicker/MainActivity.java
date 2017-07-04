package ru.inno.numberpicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ru.inno.innopicker.WheelCounter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WheelCounter counterView = (WheelCounter) findViewById(R.id.numberPicker1);


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
    }
}
