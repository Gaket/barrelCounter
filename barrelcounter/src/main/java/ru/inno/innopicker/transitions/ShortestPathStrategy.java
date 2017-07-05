package ru.inno.innopicker.transitions;

import ru.inno.innopicker.AnimationStrategy;
import ru.inno.innopicker.BarrelCounter;
import ru.inno.innopicker.ExtendedNumberPicker;

/**
 * @author Artur Badretdinov (Gaket)
 *         20.12.2016.
 */
public class ShortestPathStrategy implements AnimationStrategy {

    private BarrelCounter counter;

    @Override
    public void setPicker(BarrelCounter counter) {
        this.counter = counter;
    }

    @Override
    public void execute(int[] diff, ExtendedNumberPicker[] numberPickers) {
        for (int i = 0; i < counter.getLength(); i++) {
            if (diff[i] > 0) {
                numberPickers[i].increment(diff[i]);
            } else {
                numberPickers[i].decrement(Math.abs(diff[i]));
            }
        }
    }
}
