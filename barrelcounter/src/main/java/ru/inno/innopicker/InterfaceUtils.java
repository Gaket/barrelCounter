package ru.inno.innopicker;

import android.content.Context;
import android.util.TypedValue;

/**
 * @author Artur Badretdinov (Gaket)
 *         20.12.2016.
 */
class InterfaceUtils {

    static int spToPx(Context context, int sp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
