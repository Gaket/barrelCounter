package ru.inno.innopicker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Artur Badretdinov (Gaket)
 *         20.12.2016.
 */
public class ExtendedNumberPicker extends NumberPicker {

    public ExtendedNumberPicker(Context context) {
        super(context);
    }

    public ExtendedNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void increment() {
        changeValueByOne(true);
    }

    public void decrement() {
        changeValueByOne(false);
    }

    /**
     * Using reflection to change the value because
     * changeValueByOne is a private function and setValue
     * doesn't call the onValueChange listener.
     *
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
            e.printStackTrace();
        }
    }
}
