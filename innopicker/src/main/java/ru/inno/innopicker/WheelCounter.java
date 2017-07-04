package ru.inno.innopicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

/**
 * @author Artur Badretdinov (Gaket)
 *         20.12.2016.
 */
public class WheelCounter extends LinearLayout {

    public static final int SCROLL_DELAY_MS = 150;
    /**
     * Identifier for the state to save the selected index of
     * the side spinner.
     */
    private static String STATE_SELECTED_VALUE = "SelectedNumber";

    /**
     * Identifier for the state of the super class.
     */
    private static String STATE_SUPER_CLASS = "SuperClass";

    Handler handler;
    ExtendedNumberPicker[] numberPickers;
    private int length;


    public WheelCounter(Context context) {
        this(context, null);
    }

    public WheelCounter(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelCounter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void increment() {
        int cursor = numberPickers.length - 1;
        increment(cursor);
    }

    private void increment(int position) {
        if (position < 0) {
            return;
        }
        int curValue = numberPickers[position].getValue();
        if (curValue == 9) {
            increment(position - 1);
        }
        numberPickers[position].increment();
    }

    public void decrement() {
        int cursor = numberPickers.length - 1;
        decrement(cursor);
    }

    private void decrement(int position) {
        if (position < 0) {
            return;
        }
        int curValue = numberPickers[position].getValue();
        if (curValue == 0) {
            decrement(position - 1);
        }
        numberPickers[position].decrement();
    }

    public void setValue(Integer number) {
        String numberString = getStringForNumber(length, number);
        String currentNumberString = getValueString();

        int[] diff = calculateDiff(numberString, currentNumberString);
        for (int i = 0; i < numberPickers.length; i++) {
            update(numberPickers[i], diff[i]);
        }
    }

    private void update(ExtendedNumberPicker numberPicker, int times) {
        Runnable runnable = times > 0 ? new IncrementDigit(numberPicker) : new DecrementDigit(numberPicker);
        times = Math.abs(times);
        for (int i = 0; i < times; i++) {
            handler.postDelayed(runnable, SCROLL_DELAY_MS * i);
        }
    }

    private int[] calculateDiff(String numberString, String currentNumberString) {
        int[] diffs = new int[currentNumberString.length()];
        for (int i = currentNumberString.length() - 1; i >= 0; i--) {
            diffs[i] = calculateDiff(numberString.charAt(i), currentNumberString.charAt(i));
        }
        return diffs;
    }

    private int calculateDiff(char goalDigit, char curDigit) {
        int goal = Character.getNumericValue(goalDigit);
        int cur = Character.getNumericValue(curDigit);
        return (goal - cur) % 10;
    }

    public String getValueString() {
        StringBuilder builder = new StringBuilder();
        for (NumberPicker numberPicker : numberPickers) {
            builder.append(numberPicker.getValue());
        }
        return builder.toString();
    }

    public int getValue() {
        return Integer.valueOf(getValueString());
    }

    public int getLength() {
        return length;
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this.setOrientation(HORIZONTAL);

        handler = new Handler();

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Picker, defStyleAttr, 0);
        try {
            if (attrs == null) {
                length = 1;
            } else {
                length = attributes.getInteger(R.styleable.Picker_length, 0);
                if (length > 6) {
                    throw new IllegalArgumentException("Only 6 digits work well on 360dp device in current implementation. You should update this widget if you want to use more");
                }
            }
        } finally {
            if (attributes != null) {
                attributes.recycle();
            }
        }
        numberPickers = new ExtendedNumberPicker[length];

        String numberString = getStringForNumber(length, 0);

        for (int i = 0; i < length; i++) {
            ExtendedNumberPicker picker = new ExtendedNumberPicker(context);
            picker.setMinValue(0);
            picker.setMaxValue(9);
            picker.setValue(Character.getNumericValue(numberString.charAt(i)));
            picker.setWrapSelectorWheel(true);
            this.addView(picker);
            numberPickers[i] = picker;
        }
    }

    @NonNull
    private String getStringForNumber(int length, int number) {
        StringBuilder builder = new StringBuilder();
        String numberString = String.valueOf(number);
        if (numberString.length() < length) {
            for (int i = 0; i < length - numberString.length(); i++) {
                builder.append("0");
            }
        }
        builder.append(numberString);
        return builder.toString();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_SUPER_CLASS, super.onSaveInstanceState());
        bundle.putString(STATE_SELECTED_VALUE, getValueString());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER_CLASS));
            setPickersValues(bundle.getString(STATE_SELECTED_VALUE));
        } else
            super.onRestoreInstanceState(state);
    }

    private void setPickersValues(String value) {
        for (int i = 0; i < numberPickers.length; i++) {
            numberPickers[i].setValue(Character.getNumericValue(value.charAt(i)));
        }
    }

    private static class IncrementDigit implements Runnable {

        private final ExtendedNumberPicker picker;

        IncrementDigit(ExtendedNumberPicker picker) {
            this.picker = picker;
        }

        @Override
        public void run() {
            picker.increment();
        }
    }

    private class DecrementDigit implements Runnable {
        private final ExtendedNumberPicker picker;

        DecrementDigit(ExtendedNumberPicker picker) {
            this.picker = picker;
        }

        @Override
        public void run() {
            picker.decrement();
        }
    }
}
