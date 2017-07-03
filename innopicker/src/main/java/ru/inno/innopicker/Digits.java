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
public class Digits extends LinearLayout {

    /**
     * Identifier for the state to save the selected index of
     * the side spinner.
     */
    private static String STATE_SELECTED_VALUE = "SelectedNumber";

    /**
     * Identifier for the state of the super class.
     */
    private static String STATE_SUPER_CLASS = "SuperClass";

    NumberPicker[] numberPickers;


    public Digits(Context context) {
        this(context, null);
    }

    public Digits(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Digits(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }



    public String getValue() {
        StringBuilder builder = new StringBuilder();
        for (NumberPicker numberPicker : numberPickers) {
            builder.append(numberPicker.getValue());
        }
        return builder.toString();
    }

    public int getIntValue() {
        return Integer.valueOf(getValue());
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this.setOrientation(HORIZONTAL);
        int length;
        int number;

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Picker, defStyleAttr, 0);
        try {
            if (attrs == null) {
                length = 1;
                number = 0;
            } else {
                number = attributes.getInteger(R.styleable.Picker_value, 0);
                length = attributes.getInteger(R.styleable.Picker_length, 0);
                if (length == 0 && number > 0) {
                    length = String.valueOf(number).length();
                }
            }
        } finally {
            if (attributes != null) {
                attributes.recycle();
            }
        }
        numberPickers = new NumberPicker[length];


        String numberString = getStringForNumber(length, number);

        for (int i = 0; i < length; i++) {
            NumberPicker picker = new NumberPicker(context);
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
        String numberString = String.valueOf(number);
        if (numberString.length() < length) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length - numberString.length(); i++) {
                builder.append("0");
            }
            builder.append(numberString);
            numberString = builder.toString();
        }
        return numberString;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_SUPER_CLASS, super.onSaveInstanceState());
        bundle.putString(STATE_SELECTED_VALUE, getValue());
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


}
