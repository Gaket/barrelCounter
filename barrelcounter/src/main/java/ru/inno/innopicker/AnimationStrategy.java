package ru.inno.innopicker;

/**
 * @author Artur Badretdinov (Gaket)
 *         20.12.2016.
 */
public interface AnimationStrategy {

    void setPicker(BarrelCounter counter);

    void execute(int[] diff, ExtendedNumberPicker[] numberPickers);

}
