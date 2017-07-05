package ru.inno.innopicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

/**
 * @author Artur Badretdinov (Gaket)
 *         20.12.2016.
 */
public class BarrelCounter extends LinearLayout {

    private static final int DEFAULT_FONT_SIZE_SP = 14;
    private static final int DEFAULT_LENGTH = 1;
    private static final int MAX_LENGTH = 6;

    /**
     * Identifier for the state to save the selected index of
     * the side spinner.
     */
    private static String STATE_SELECTED_VALUE = "SelectedNumber";

    /**
     * Identifier for the state of the super class.
     */
    private static String STATE_SUPER_CLASS = "SuperClass";

    private ExtendedNumberPicker[] numberPickers;
    private int length;

    public BarrelCounter(Context context) {
        this(context, null);
    }

    public BarrelCounter(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarrelCounter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
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
            setPickersValuesSilently(bundle.getString(STATE_SELECTED_VALUE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    /**
     * Increment the number in widget
     */
    public void increment() {
        int cursor = numberPickers.length - 1;
        increment(cursor);
    }

    /**
     * Decrement the number in widget
     */
    public void decrement() {
        int cursor = numberPickers.length - 1;
        decrement(cursor);
    }

    /**
     * Set new value with animations
     *
     * @param number to set
     */
    public void setValue(int number) {
        String numberString = getStringForNumber(length, number);
        String currentNumberString = getValueString();

        int[] diff = calculateDiff(numberString, currentNumberString);
        for (int i = 0; i < numberPickers.length; i++) {

            // This is a point for extension, we can add different transition strategies here
            // for example, increment all, decrement all, or use current (bidirectional) algorithm
            update(numberPickers[i], diff[i]);
        }
    }

    /**
     * Set new value with animations
     *
     * @param number to set
     */
    public void setValue(String number) {
        int num = Integer.parseInt(number);
        setValue(num);
    }

    /**
     * @return current value with leading zeros, if they exist
     */
    public String getValueString() {
        StringBuilder builder = new StringBuilder();
        for (NumberPicker numberPicker : numberPickers) {
            builder.append(numberPicker.getValue());
        }
        return builder.toString();
    }

    /**
     * @return current value
     */
    public int getValue() {
        return Integer.valueOf(getValueString());
    }

    /**
     * @return total number of digits
     */
    public int getLength() {
        return length;
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

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this.setOrientation(HORIZONTAL);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Picker, defStyleAttr, 0);
        int textSize = InterfaceUtils.spToPx(getContext(), DEFAULT_FONT_SIZE_SP);
        length = DEFAULT_LENGTH;
        try {
            if (attrs != null) {
                length = attributes.getInteger(R.styleable.Picker_length, DEFAULT_LENGTH);
                textSize = attributes.getDimensionPixelSize(R.styleable.Picker_textSize, InterfaceUtils.spToPx(getContext(), DEFAULT_FONT_SIZE_SP));
                if (length > MAX_LENGTH) {
                    throw new IllegalArgumentException("Only " + MAX_LENGTH + " digits work well on 360dp device in current implementation. You should update this widget if you want to use more");
                }
            }
        } finally {
            if (attributes != null) {
                attributes.recycle();
            }
        }
        numberPickers = new ExtendedNumberPicker[length];

        String digit = getStringForNumber(length, 0);

        for (int i = 0; i < length; i++) {
            ExtendedNumberPicker picker = createSinglePicker(context, textSize, digit, i);
            this.addView(picker);
            numberPickers[i] = picker;
        }
    }

    @NonNull
    private ExtendedNumberPicker createSinglePicker(Context context, int textSize, String numberString, int i) {
        ExtendedNumberPicker picker = new ExtendedNumberPicker(context);
        picker.setMinValue(0);
        picker.setMaxValue(9);
        picker.setValue(Character.getNumericValue(numberString.charAt(i)));
        picker.setWrapSelectorWheel(true);
        picker.setBackgroundResource(R.drawable.bg_number_picker);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(1, 0, 1, 0);
        picker.setLayoutParams(params);
        picker.setTextSize(textSize);
        return picker;
    }

    private void update(ExtendedNumberPicker numberPicker, int times) {
        if (times > 0) {
            numberPicker.increment(times);
        } else {
            numberPicker.decrement(Math.abs(times));
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

    private void setPickersValuesSilently(String value) {
        for (int i = 0; i < numberPickers.length; i++) {
            numberPickers[i].setValue(Character.getNumericValue(value.charAt(i)));
        }
    }
}
