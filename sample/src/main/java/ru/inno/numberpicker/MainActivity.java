package ru.inno.numberpicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ru.inno.innopicker.BarrelCounter;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_ACTIVE_COUNTER = "active counter";
    private Timer timer;
    private ActiveCounter activeCounter = ActiveCounter.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BarrelCounter counterView = (BarrelCounter) findViewById(R.id.numberPicker1);

        initViews(counterView);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_ACTIVE_COUNTER)) {
            activeCounter = (ActiveCounter) savedInstanceState.getSerializable(KEY_ACTIVE_COUNTER);
            if (activeCounter == ActiveCounter.UP) {
                scheduleIncrementTask(counterView);
            } else if (activeCounter == ActiveCounter.DOWN) {
                scheduleDecrementTask(counterView);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_ACTIVE_COUNTER, activeCounter);
    }

    private void initViews(final BarrelCounter counterView) {
        final EditText numberInput = (EditText) findViewById(R.id.number_input_view);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(counterView.getLength()); //Filter to 10 characters
        numberInput.setFilters(filters);
        numberInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    counterView.setValue(Integer.valueOf(numberInput.getText().toString()));
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

        findViewById(R.id.stop_counter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTimer();
                activeCounter = ActiveCounter.NONE;
            }
        });

        findViewById(R.id.start_increment_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleIncrementTask(counterView);
            }
        });

        findViewById(R.id.start_decrement_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleDecrementTask(counterView);
            }
        });

        findViewById(R.id.set_number_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterView.setValue(numberInput.getText().toString());
            }
        });

        findViewById(R.id.random_value_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                int length = counterView.getValueString().length();
                int randValue = r.nextInt((int) Math.round(Math.pow(10, length)));
                counterView.setValue(randValue);
            }
        });
    }

    private void scheduleDecrementTask(BarrelCounter counterView) {
        cancelTimer();
        timer.schedule(new DecrementTask(counterView), 0, 1000);
        activeCounter = ActiveCounter.DOWN;
    }

    private void scheduleIncrementTask(BarrelCounter counterView) {
        cancelTimer();
        timer.schedule(new IncrementTask(counterView), 0, 1000);
        activeCounter = ActiveCounter.UP;
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
    }

    private class IncrementTask extends TimerTask {
        private final BarrelCounter counterView;

        IncrementTask(BarrelCounter counterView) {
            this.counterView = counterView;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    counterView.increment();
                }
            });
        }
    }

    private class DecrementTask extends TimerTask {
        private final BarrelCounter counterView;

        DecrementTask(BarrelCounter counterView) {
            this.counterView = counterView;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    counterView.decrement();
                }
            });
        }
    }

    private enum ActiveCounter {
        UP,
        DOWN,
        NONE
    }
}
