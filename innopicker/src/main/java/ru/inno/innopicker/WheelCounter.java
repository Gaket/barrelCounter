package ru.inno.innopicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
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

    /**
     * Identifier for the state to save the selected index of
     * the side spinner.
     */
    private static String STATE_SELECTED_VALUE = "SelectedNumber";

    /**
     * Identifier for the state of the super class.
     */
    private static String STATE_SUPER_CLASS = "SuperClass";

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


    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this.setOrientation(HORIZONTAL);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Picker, defStyleAttr, 0);
        try {
            if (attrs == null) {
                length = 1;
            } else {
                length = attributes.getInteger(R.styleable.Picker_length, 0);
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

    public void setNumber(Integer number) {
        String numberString = getStringForNumber(length, number);
        setPickersValues(numberString);
    }
}
