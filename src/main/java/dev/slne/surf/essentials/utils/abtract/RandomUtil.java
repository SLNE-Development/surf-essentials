package dev.slne.surf.essentials.utils.abtract;

import dev.slne.surf.essentials.utils.Validate;
import org.jetbrains.annotations.Range;

import java.util.Random;

/**
 * A utility class for generating random integers and entity IDs.
 * @author twisti
 * @since 1.0.2
 */
@SuppressWarnings("unused")
public abstract class RandomUtil extends CommandUtil {
    private static final Random random;

    static {
        random = new Random();
    }

    /**
     * Generates a random integer.
     *
     * @return the random integer
     */
    public static int getRandomInt(){
        return random.nextInt();
    }

    /**
     * Generates a random integer between 0 (inclusive) and the specified bound (exclusive).
     *
     * @param bound the upper bound (exclusive) for the random integer to be generated
     * @return the random integer
     */
    public static int getRandomInt(@Range(from = 0, to = Integer.MAX_VALUE) int bound){
        return random.nextInt(Validate.isInBound(bound, 0, Integer.MAX_VALUE));
    }

    /**
     * Generates a random integer between the specified origin (inclusive) and bound (exclusive).
     *
     * @param origin the lower bound (inclusive) for the random integer to be generated
     * @param bound the upper bound (exclusive) for the random integer to be generated
     * @return the random integer
     */
    public static int getRandomInt(int origin, int bound){
        Validate.isBigger(origin, bound);
        return random.nextInt(origin, bound);
    }

    /**
     * Generates a custom entity ID in the range of [({@link Integer#MAX_VALUE} - {@code 100_000}) - {@link Integer#MAX_VALUE}].
     *
     * @return the custom entity ID
     */
    public static int getCustomEntityId(){
        return getRandomInt(Integer.MAX_VALUE - 100_000, Integer.MAX_VALUE);
    }
}
