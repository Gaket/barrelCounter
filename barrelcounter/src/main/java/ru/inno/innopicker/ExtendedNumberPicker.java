package ru.inno.innopicker;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Artur Badretdinov (Gaket)
 *         20.12.2016.
 */
public class ExtendedNumberPicker extends NumberPicker {

    public static final String TAG = "ExtendedNumberPicker";
    public static final int SCROLL_DELAY_MS = 150;

    private Handler handler = new Handler();

    public ExtendedNumberPicker(Context context) {
        super(context);
    }

    public ExtendedNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Increment value showing animation
     */
    public void increment() {
        changeValueByOne(true);
    }

    /**
     * Increment value showing animation several times
     *
     * @param times - number of increments
     */
    public void increment(int times) {
        Runnable runnable = new IncrementDigit(this);
        times = Math.abs(times);
        for (int i = 0; i < times; i++) {
            handler.postDelayed(runnable, SCROLL_DELAY_MS * i);
        }
    }

    /**
     * Decrement value showing animation
     */
    public void decrement() {
        changeValueByOne(false);
    }


    /**
     * Decrement value showing animation several times
     *
     * @param times - number of decrements
     */
    public void decrement(int times) {
        Runnable runnable = new DecrementDigit(this);
        times = Math.abs(times);
        for (int i = 0; i < times; i++) {
            handler.postDelayed(runnable, SCROLL_DELAY_MS * i);
        }
    }

    /**
     * Using reflection to change the value because
     * changeValueByOne is a private function and setValue
     * doesn't call the onValueChange listener.
     * <p>
     * Rationale: https://stackoverflow.com/questions/13500852/how-to-change-numberpickers-value-with-animation
     *
     * @param increment - true if it should be incremented, else - decremented
     */
    private void changeValueByOne(final boolean increment) {

        Method method;
        try {
            // refelction call for
            // higherPicker.changeValueByOne(true);
            method = this.getClass().getSuperclass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(this, increment);

            // Here would be a call to Timber that would send trace to Crashlytics, if it is real app and not the test one
        } catch (final NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            Log.e(TAG, "change value by one error", e);
        }
    }

    /**
     * Set text size in pixels
     *
     * Using reflection to change the value because
     * there is no such setter in NumberPicker
     *
     * @param textSize - text size in pixels
     */
    public void setTextSize(float textSize) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof EditText) {
                setTextSize(textSize, (EditText) child);
            } else {
                Log.e(TAG, "Unexpected view type");
            }
        }
    }

    private void setTextSize(float textSize, EditText numberView) {
        try {
            Field selectorWheelPaintField = this.getClass().getSuperclass().getDeclaredField("mSelectorWheelPaint");
            selectorWheelPaintField.setAccessible(true);
            Paint paint = (Paint) selectorWheelPaintField.get(this);
            paint.setTextSize(textSize);
            numberView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            this.invalidate();
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            Log.e(TAG, "Problem getting view field", e);
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
