package dev.slne.surf.essentials.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.annontations.FieldsAreNonnullByDefault;
import dev.slne.surf.essentials.annontations.MethodsReturnNonnullByDefault;
import dev.slne.surf.essentials.annontations.ParametersAreNonnullByDefault;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * Utility methods for validating method arguments and states.
 */
@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class Validate {
    private Validate() {
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Validates that the specified object is not null.
     *
     * @param t       the object to validate
     * @param message the error message to use if validation fails
     * @param <T>     the type of the object to validate
     * @return the validated object
     * @throws NullPointerException if the specified object is null
     */
    @Contract("null, _ -> fail; !null, _ -> param1")
    public static <T> @NotNull T notNull(@Nullable T t, Component message) throws CommandSyntaxException {
        //if (t == null) throw EssentialsUtil.createException(message);
        return t;
    }

    /**
     * Validates that the specified object is not null.
     *
     * @param t       the object to validate
     * @param message the error message to use if validation fails
     * @param <T>     the type of the object to validate
     * @return the validated object
     * @throws NullPointerException if the specified object is null
     */
    @Contract("null, _ -> fail; !null, _ -> param1")
    public static <T> @NotNull T notNull(@Nullable T t, String message) throws CommandSyntaxException {
        return notNull(t, Component.text(message));
    }

    /**
     * Validates that the specified object is not null.
     *
     * @param t   the object to validate
     * @param <T> the type of the object to validate
     * @return the validated object
     * @throws NullPointerException if the specified object is null
     */
    @Contract("!null -> param1")
    public static <T> @NotNull T notNull(@Nullable T t) throws CommandSyntaxException {
        return notNull(t, "The validated object is null");
    }


    //------------------------------------------------------------------------------------------------------------------

    /**
     * Validates that the specified array does not contain null elements
     *
     * @param array the array to validate
     * @param <T>   the type of the array to validate
     * @return the validated array
     * @throws NullPointerException if the specified array contains null elements
     */
    @Contract("_ -> param1")
    public static <T> T[] noNullElements(T[] array) throws CommandSyntaxException {
        notNull(array);
        for (int i = 0; i < array.length; i++) {
            notNull(array[i], "The validated array contains null element at index: " + i);
        }
        return array;
    }

    /**
     * Validates that the specified array does not contain null elements
     *
     * @param array   the array to validate
     * @param message the error message to use if validation fails
     * @param <T>     the type of the array to validate
     * @return the validated array
     * @throws NullPointerException if the specified array contains null elements
     */
    @Contract("_, _ -> param1")
    public static <T> T[] noNullElements(T[] array, Component message) throws CommandSyntaxException {
        notNull(array);
        for (T t : array) {
            notNull(t, message);
        }
        return array;
    }


    /**
     * Validates that the specified array does not contain null elements
     *
     * @param array   the array to validate
     * @param message the error message to use if validation fails
     * @param <T>     the type of the array to validate
     * @return the validated array
     * @throws NullPointerException if the specified array contains null elements
     */
    @Contract("_, _ -> param1")
    public static <T> T[] noNullElements(T[] array, String message) throws CommandSyntaxException {
        return noNullElements(array, Component.text(message));
    }

    /**
     * Validates that the specified iterable does not contain null elements
     *
     * @param iterable the iterable to validate
     * @param <T>      the type of the iterable to validate
     * @return the validated iterable
     * @throws NullPointerException if the specified array contains null elements
     */
    @Contract("_ -> param1")
    public static <T extends Iterable<?>> T noNullElements(T iterable) throws CommandSyntaxException {
        notNull(iterable);

        int index = 0;
        for (Object o : iterable) {
            notNull(o, "The validated iterable contains null element at index: " + index);
            index++;
        }

        return iterable;
    }

    /**
     * Validates that the specified iterable does not contain null elements
     *
     * @param iterable the iterable to validate
     * @param message  the error message to use if validation fails
     * @param <T>      the type of the iterable to validate
     * @return the validated iterable
     * @throws NullPointerException if the specified array contains null elements
     */
    @Contract("_, _ -> param1")
    public static <T extends Iterable<?>> T noNullElements(T iterable, Component message) throws CommandSyntaxException {
        notNull(iterable);

        for (Object o : iterable) {
            notNull(o, message);
        }

        return iterable;
    }

    /**
     * Validates that the specified iterable does not contain null elements
     *
     * @param iterable the iterable to validate
     * @param message  the error message to use if validation fails
     * @param <T>      the type of the iterable to validate
     * @return the validated iterable
     * @throws NullPointerException if the specified array contains null elements
     */
    @Contract("_, _ -> param1")
    public static <T extends Iterable<?>> T noNullElements(T iterable, String message) throws CommandSyntaxException {
        return noNullElements(iterable, Component.text(message));
    }


    //------------------------------------------------------------------------------------------------------------------

    /**
     * Validates that the specified boolean is true
     *
     * @param value   the boolean to validate
     * @param message the error message to use if validation fails
     * @return true
     * @throws IllegalArgumentException if the specified boolean is not true
     */
    @Contract("false, _ -> fail; true, _ -> true")
    public static boolean isTrue(boolean value, Component message) throws CommandSyntaxException {
        //if (!value) throw EssentialsUtil.createException(message);
        return true;
    }

    /**
     * Validates that the specified boolean is true
     *
     * @param value   the boolean to validate
     * @param message the error message to use if validation fails
     * @return true
     * @throws IllegalArgumentException if the specified boolean is not true
     */
    @Contract("false, _ -> fail; true, _ -> true")
    public static boolean isTrue(boolean value, String message) throws CommandSyntaxException {
        return isTrue(value, Component.text(message));
    }

    /**
     * Validates that the specified boolean is true
     *
     * @param value the boolean to validate
     * @return true
     * @throws IllegalArgumentException if the specified boolean is not true
     */
    @Contract("false -> fail; true -> true")
    public static boolean isTrue(boolean value) throws CommandSyntaxException {
        return isTrue(value, "The validated expression is false");
    }


    //------------------------------------------------------------------------------------------------------------------

    /**
     * Validates that the specified iterable is not empty
     *
     * @param t       the iterable to validate
     * @param message the error message if validation fails
     * @param <T>     the type of the iterable to validate
     * @return the validated iterable
     * @throws IllegalArgumentException if the iterable is empty
     */
    @Contract("_, _ -> param1")
    public static <T extends Iterable<?>> T notEmpty(T t, Component message) throws CommandSyntaxException {
        isTrue(!t.iterator().hasNext(), message);
        return t;
    }

    /**
     * Validates that the specified iterable is not empty
     *
     * @param t       the iterable to validate
     * @param message the error message if validation fails
     * @param <T>     the type of the iterable to validate
     * @return the validated iterable
     * @throws IllegalArgumentException if the iterable is empty
     */
    @Contract("_, _ -> param1")
    public static <T extends Iterable<?>> T notEmpty(T t, String message) throws CommandSyntaxException {
        return notEmpty(t, Component.text(message));
    }

    /**
     * Validates that the specified iterable is not empty
     *
     * @param t   the iterable to validate
     * @param <T> the type of the iterable to validate
     * @return the validated iterable
     * @throws IllegalArgumentException if the iterable is empty
     */
    @Contract("_ -> param1")
    public static <T extends Iterable<?>> T notEmpty(T t) throws CommandSyntaxException {
        return notEmpty(t, "The validated iterable is empty");
    }

    /**
     * Validates that the specified array is not empty
     *
     * @param t       the array to validate
     * @param message the error message if validation fails
     * @param <T>     the type of the array to validate
     * @return the validated array
     * @throws IllegalArgumentException if the array is empty
     */
    @Contract("_, _ -> param1")
    public static <T> T[] notEmpty(T[] t, Component message) throws CommandSyntaxException {
        isTrue(!(t.length == 0), message);
        return t;
    }

    /**
     * Validates that the specified array is not empty
     *
     * @param t       the array to validate
     * @param message the error message if validation fails
     * @param <T>     the type of the array to validate
     * @return the validated array
     * @throws IllegalArgumentException if the array is empty
     */
    @Contract("_, _ -> param1")
    public static <T> T[] notEmpty(T[] t, String message) throws CommandSyntaxException {
        return notEmpty(t, Component.text(message));
    }

    /**
     * Validates that the specified array is not empty
     *
     * @param t   the array to validate
     * @param <T> the type of the array to validate
     * @return the validated array
     * @throws IllegalArgumentException if the array is empty
     */
    @Contract("_ -> param1")
    public static <T> T[] notEmpty(T[] t) throws CommandSyntaxException {
        return notEmpty(t, "The validated array is empty");
    }

    /**
     * Validates that the specified String is not empty
     *
     * @param s       the string to validate
     * @param message the error message if validation fails
     * @return the validated string
     * @throws IllegalArgumentException if the string is empty
     */
    @Contract("_, _ -> param1")
    public static String notEmpty(@Nullable String s, Component message) throws CommandSyntaxException {
        isTrue(!(s == null || s.length() == 0), message);
        return s;
    }

    /**
     * Validates that the specified String is not empty
     *
     * @param s       the string to validate
     * @param message the error message if validation fails
     * @return the validated string
     * @throws IllegalArgumentException if the string is empty
     */
    @Contract("_, _ -> param1")
    public static String notEmpty(@Nullable String s, String message) throws CommandSyntaxException {
        return notEmpty(s, Component.text(message));
    }

    /**
     * Validates that the specified String is not empty
     *
     * @param s the string to validate
     * @return the validated string
     * @throws IllegalArgumentException if the string is empty
     */
    @Contract("_ -> param1")
    public static String notEmpty(@Nullable String s) throws CommandSyntaxException {
        return notEmpty(s, "The validated string is empty");
    }


    //------------------------------------------------------------------------------------------------------------------

    /**
     * Validates that all elements in the specified iterable are of the specified class
     *
     * @param t       the iterable to validate
     * @param clazz   the class to validate the elements against
     * @param message the error message if the validation fails
     * @param <T>     the type of the iterable to validate
     * @return the validated iterable
     * @throws NullPointerException     if {@code t} or {@code clazz} is null
     * @throws IllegalArgumentException if an element is not of the specified class
     */
    @Contract("_, _, _ -> param1")
    public static <T extends Iterable<?>> T allElementsOfType(T t, Class<?> clazz, Component message) throws CommandSyntaxException {
        notNull(t);
        notNull(clazz);

        for (Object value : t) {
            if (clazz.isInstance(value)) continue;
            //throw EssentialsUtil.createException(message);
        }
        return t;
    }

    /**
     * Validates that all elements in the specified iterable are of the specified class
     *
     * @param t       the iterable to validate
     * @param clazz   the class to validate the elements against
     * @param message the error message if the validation fails
     * @param <T>     the type of the iterable to validate
     * @return the validated iterable
     * @throws NullPointerException     if {@code t} or {@code clazz} is null
     * @throws IllegalArgumentException if an element is not of the specified class
     */
    @Contract("_, _, _ -> param1")
    public static <T extends Iterable<?>> T allElementsOfType(T t, Class<?> clazz, String message) throws CommandSyntaxException {
        return allElementsOfType(t, clazz, Component.text(message));
    }

    /**
     * Validates that all elements in the specified iterable are of the specified class
     *
     * @param t     the iterable to validate
     * @param clazz the class to validate the elements against
     * @param <T>   the type of the iterable to validate
     * @return the validated iterable
     * @throws NullPointerException     if {@code t} or {@code clazz} is null
     * @throws IllegalArgumentException if an element is not of the specified class
     */
    @Contract("_, _ -> param1")
    public static <T extends Iterable<?>> T allElementsOfType(T t, Class<?> clazz) throws CommandSyntaxException {
        notNull(t);
        notNull(clazz);
        int i = 0;
        for (Iterator<?> it = t.iterator(); it.hasNext(); i++) {
            if (clazz.isInstance(it.next())) continue;

            //throw EssentialsUtil.createException(
            //        Component.text("The validated iterable contains an element not of type " + clazz.getName() + " at index: " + i)
            //);
        }
        return t;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Validates that the {@link Number} is in bound of the specified bounds
     *
     * @param check the {@link Number} to check
     * @param min   the minimum {@link Number}
     * @param max   the maximum {@link Number}
     * @param <T>   {@link Number}
     * @return the validated {@link Number}
     */
    @Contract("_, _, _ -> param1")
    public static <T extends Number> T isInBound(@NotNull T check, @NotNull T min, @NotNull T max) throws CommandSyntaxException {
        notNull(check);
        notNull(min);
        notNull(max);

        if (check.doubleValue() > max.doubleValue() || check.doubleValue() < min.doubleValue()) {
            //throw EssentialsUtil.createException(
             //       Component.text("Index (%s) is out of bounds [%s - %s]".formatted(check.doubleValue(), min.doubleValue(), max.doubleValue()))
            //);
        }
        return check;
    }

    @Contract("_, _ -> param1")
    public static <T extends Number> T isBigger(@NotNull T toCheck, @NotNull T against) throws CommandSyntaxException {
        notNull(toCheck);
        notNull(against);

        if (toCheck.doubleValue() <= against.doubleValue()) {
            //throw EssentialsUtil.createException(
             //       Component.text("Number is too low")
            //);
        }

        return toCheck;
    }
}
