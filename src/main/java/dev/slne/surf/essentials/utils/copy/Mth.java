package dev.slne.surf.essentials.utils.copy;

import dev.slne.surf.essentials.utils.EssentialsUtil;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

/**
 * A copy of the net.minecraft.util.Mth class.
 *
 * @author Mojang Studios
 */
@UtilityClass
public class Mth {
    private final long UUID_VERSION = 0xF000L;
    private final long UUID_VERSION_TYPE_4 = 0x4000L;
    private final long UUID_VARIANT = 0x4000L;
    private final long UUID_VARIANT_2 = Long.MIN_VALUE;
    public final float PI = 3.1415927F;
    public final float HALF_PI = 1.5707964F;
    public final float TWO_PI = 6.2831855F;
    public final float DEG_TO_RAD = 0.017453292F;
    public final float RAD_TO_DEG = 57.295776F;
    public final float EPSILON = 1.0E-5F;
    public final float SQRT_OF_TWO = sqrt(2.0F);
    private final float SIN_SCALE = 10430.378F;
    private final float[] SIN = EssentialsUtil.make(new float[65536], (sineTable) -> {
        for (int i = 0; i < sineTable.length; ++i) {
            sineTable[i] = (float) Math.sin((double) i * Math.PI * 2.0 / 65536.0);
        }

    });
    private final Random RANDOM = new Random();
    private final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    private final double ONE_SIXTH = 0.16666666666666666;
    private final int FRAC_EXP = 8;
    private final int LUT_SIZE = 257;
    private final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
    private final double[] ASIN_TAB = new double[LUT_SIZE];
    private final double[] COS_TAB = new double[LUT_SIZE];

    /**
     * Computes the sine of the given value.
     *
     * @param value The input value.
     * @return The sine of the input value.
     */
    public float sin(float value) {
        return SIN[(int) (value * SIN_SCALE) & 0xFFFF];
    }

    /**
     * Computes the cosine of the given value.
     *
     * @param value The input value.
     * @return The cosine of the input value.
     */
    public float cos(float value) {
        return SIN[(int) (value * SIN_SCALE + 16384.0F) & 0xFFFF];
    }

    /**
     * Computes the square root of the given value.
     *
     * @param value The input value.
     * @return The square root of the input value.
     */
    public float sqrt(float value) {
        return (float) Math.sqrt(value);
    }

    /**
     * Rounds the given value down to the nearest integer.
     *
     * @param value The input value.
     * @return The rounded down value.
     */
    public int floor(float value) {
        return (int) value;
    }

    /**
     * Rounds the given value down to the nearest integer.
     *
     * @param value The input value.
     * @return The rounded down value.
     */
    public int floor(double value) {
        int integerValue = (int) value;
        return value < (double) integerValue ? integerValue - 1 : integerValue;
    }

    /**
     * Rounds the given value down to the nearest long integer.
     *
     * @param value The input value.
     * @return The rounded down value.
     */
    public long lfloor(double value) {
        long longValue = (long) value;
        return value < (double) longValue ? longValue - 1L : longValue;
    }

    /**
     * Computes the absolute value of the given value.
     *
     * @param value The input value.
     * @return The absolute value of the input value.
     */
    public float abs(float value) {
        return Math.abs(value);
    }

    /**
     * Computes the absolute value of the given value.
     *
     * @param value The input value.
     * @return The absolute value of the input value.
     */
    public int abs(int value) {
        return Math.abs(value);
    }

    /**
     * Rounds the given value up to the nearest integer.
     *
     * @param value The input value.
     * @return The rounded up value.
     */
    public int ceil(float value) {
        return (int) Math.ceil(value);
    }

    /**
     * Rounds the given value up to the nearest integer.
     *
     * @param value The input value.
     * @return The rounded up value.
     */
    public int ceil(double value) {
        return (int) Math.ceil(value);
    }

    /**
     * Clamps the given value within the specified range.
     *
     * @param value The value to clamp.
     * @param min   The minimum value of the range.
     * @param max   The maximum value of the range.
     * @return The clamped value.
     */
    public int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Clamps the given value within the specified range.
     *
     * @param value The value to clamp.
     * @param min   The minimum value of the range.
     * @param max   The maximum value of the range.
     * @return The clamped value.
     */
    public float clamp(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Clamps the given value within the specified range.
     *
     * @param value The value to clamp.
     * @param min   The minimum value of the range.
     * @param max   The maximum value of the range.
     * @return The clamped value.
     */
    public double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Linearly interpolates between two values and clamps the result within the range of [start, end].
     *
     * @param start The starting value.
     * @param end   The ending value.
     * @param delta The interpolation factor.
     * @return The interpolated value.
     */
    public double clampedLerp(double start, double end, double delta) {
        if (delta <= 0.0) {
            return start;
        } else if (delta >= 1.0) {
            return end;
        } else {
            return lerp(delta, start, end);
        }
    }

    /**
     * Linearly interpolates between two values and clamps the result within the range of [start, end].
     *
     * @param start The starting value.
     * @param end   The ending value.
     * @param delta The interpolation factor.
     * @return The interpolated value.
     */
    public float clampedLerp(float start, float end, float delta) {
        if (delta <= 0.0F) {
            return start;
        } else if (delta >= 1.0F) {
            return end;
        } else {
            return lerp(delta, start, end);
        }
    }

    /**
     * Returns the maximum value between the absolute values of two doubles.
     *
     * @param a The first double value.
     * @param b The second double value.
     * @return The maximum absolute value.
     */
    public double absMax(double a, double b) {
        if (a < 0.0) {
            a = -a;
        }

        if (b < 0.0) {
            b = -b;
        }

        return Math.max(a, b);
    }

    /**
     * Performs integer division between two integers, rounding the result towards negative infinity.
     *
     * @param dividend The dividend.
     * @param divisor  The divisor.
     * @return The quotient.
     */
    public int floorDiv(int dividend, int divisor) {
        return Math.floorDiv(dividend, divisor);
    }

    /**
     * Generates a random integer between the specified minimum and maximum values (inclusive).
     *
     * @param random The random number generator.
     * @param min    The minimum value.
     * @param max    The maximum value.
     * @return The random integer.
     */
    public int nextInt(Random random, int min, int max) {
        return min >= max ? min : random.nextInt(max - min + 1) + min;
    }

    /**
     * Generates a random float between the specified minimum and maximum values (inclusive).
     *
     * @param random The random number generator.
     * @param min    The minimum value.
     * @param max    The maximum value.
     * @return The random float.
     */
    public float nextFloat(Random random, float min, float max) {
        return min >= max ? min : random.nextFloat() * (max - min) + min;
    }


    /**
     * Generates a random double between the specified minimum and maximum values (inclusive).
     *
     * @param random The random number generator.
     * @param min    The minimum value.
     * @param max    The maximum value.
     * @return The random double.
     */
    public double nextDouble(Random random, double min, double max) {
        return min >= max ? min : random.nextDouble() * (max - min) + min;
    }

    /**
     * Determines if two float values are approximately equal within a small epsilon.
     *
     * @param a The first float value.
     * @param b The second float value.
     * @return {@code true} if the values are approximately equal, {@code false} otherwise.
     */
    public boolean equal(float a, float b) {
        return Math.abs(b - a) < EPSILON;
    }

    /**
     * Determines if two double values are approximately equal within a small epsilon.
     *
     * @param a The first double value.
     * @param b The second double value.
     * @return {@code true} if the values are approximately equal, {@code false} otherwise.
     */
    public boolean equal(double a, double b) {
        return Math.abs(b - a) < 9.999999747378752E-6;
    }


    public int positiveModulo(int dividend, int divisor) {
        return Math.floorMod(dividend, divisor);
    }

    public float positiveModulo(float dividend, float divisor) {
        return (dividend % divisor + divisor) % divisor;
    }

    public double positiveModulo(double dividend, double divisor) {
        return (dividend % divisor + divisor) % divisor;
    }

    public boolean isMultipleOf(int a, int b) {
        return a % b == 0;
    }

    public int wrapDegrees(int degrees) {
        int i = degrees % 360;
        if (i >= 180) {
            i -= 360;
        }

        if (i < -180) {
            i += 360;
        }

        return i;
    }

    public float wrapDegrees(float degrees) {
        float f = degrees % 360.0F;
        if (f >= 180.0F) {
            f -= 360.0F;
        }

        if (f < -180.0F) {
            f += 360.0F;
        }

        return f;
    }

    public double wrapDegrees(double degrees) {
        double d = degrees % 360.0;
        if (d >= 180.0) {
            d -= 360.0;
        }

        if (d < -180.0) {
            d += 360.0;
        }

        return d;
    }

    public float degreesDifference(float start, float end) {
        return wrapDegrees(end - start);
    }

    public float degreesDifferenceAbs(float first, float second) {
        return abs(degreesDifference(first, second));
    }

    public float rotateIfNecessary(float value, float mean, float delta) {
        float f = degreesDifference(value, mean);
        float g = clamp(f, -delta, delta);
        return mean - g;
    }

    public float approach(float from, float to, float step) {
        step = abs(step);
        return from < to ? clamp(from + step, from, to) : clamp(from - step, to, from);
    }

    public float approachDegrees(float from, float to, float step) {
        float f = degreesDifference(from, to);
        return approach(from, from + f, step);
    }

    public int getInt(String string, int fallback) {
        return NumberUtils.toInt(string, fallback);
    }

    public int smallestEncompassingPowerOfTwo(int value) {
        int i = value - 1;
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        return i + 1;
    }

    public boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    public int ceillog2(int value) {
        value = isPowerOfTwo(value) ? value : smallestEncompassingPowerOfTwo(value);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int) ((long) value * 125613361L >> 27) & 31];
    }

    public int log2(int value) {
        return ceillog2(value) - (isPowerOfTwo(value) ? 0 : 1);
    }

    public float frac(float value) {
        return value - (float) floor(value);
    }

    public double frac(double value) {
        return value - (double) lfloor(value);
    }

    public UUID createInsecureUUID(Random random) {
        long l = random.nextLong() & -UUID_VERSION | UUID_VERSION_TYPE_4;
        long m = random.nextLong() & 4611686018427387903L | UUID_VARIANT_2;
        return new UUID(l, m);
    }

    public UUID createInsecureUUID() {
        return createInsecureUUID(RANDOM);
    }

    public double inverseLerp(double value, double start, double end) {
        return (value - start) / (end - start);
    }

    public float inverseLerp(float value, float start, float end) {
        return (value - start) / (end - start);
    }


    public boolean rayIntersectsAABB(Vector origin, Vector direction, BoundingBox box) {
        double d = (box.getMinX() + box.getMaxX()) * 0.5;
        double e = (box.getMaxX() - box.getMinX()) * 0.5;
        double f = origin.getX() - d;
        if (Math.abs(f) > e && f * direction.getX() >= 0.0) {
            return false;
        } else {
            double g = (box.getMinY() + box.getMaxY()) * 0.5;
            double h = (box.getMaxY() - box.getMinY()) * 0.5;
            double i = origin.getY() - g;
            if (Math.abs(i) > h && i * direction.getY() >= 0.0) {
                return false;
            } else {
                double j = (box.getMinZ() + box.getMaxZ()) * 0.5;
                double k = (box.getMaxZ() - box.getMinZ()) * 0.5;
                double l = origin.getZ() - j;
                if (Math.abs(l) > k && l * direction.getZ() >= 0.0) {
                    return false;
                } else {
                    double m = Math.abs(direction.getX());
                    double n = Math.abs(direction.getY());
                    double o = Math.abs(direction.getZ());
                    double p = direction.getY() * l - direction.getZ() * i;
                    if (Math.abs(p) > h * o + k * n) {
                        return false;
                    } else {
                        p = direction.getZ() * f - direction.getX() * l;
                        if (Math.abs(p) > e * o + k * m) {
                            return false;
                        } else {
                            p = direction.getX() * i - direction.getY() * f;
                            return Math.abs(p) < e * n + h * m;
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public double atan2(double y, double x) {
        double d = x * x + y * y;
        if (Double.isNaN(d)) {
            return Double.NaN;
        } else {
            boolean bl = y < 0.0;
            if (bl) {
                y = -y;
            }

            boolean bl2 = x < 0.0;
            if (bl2) {
                x = -x;
            }

            boolean bl3 = y > x;
            double f;
            if (bl3) {
                f = x;
                x = y;
                y = f;
            }

            f = fastInvSqrt(d);
            x *= f;
            y *= f;
            double g = FRAC_BIAS + y;
            int i = (int) Double.doubleToRawLongBits(g);
            double h = ASIN_TAB[i];
            double j = COS_TAB[i];
            double k = g - FRAC_BIAS;
            double l = y * j - x * k;
            double m = (6.0 + l * l) * l * ONE_SIXTH;
            double n = h + m;
            if (bl3) {
                n = 1.5707963267948966 - n;
            }

            if (bl2) {
                n = Math.PI - n;
            }

            if (bl) {
                n = -n;
            }

            return n;
        }
    }

    public float invSqrt(float x) {
        return org.joml.Math.invsqrt(x);
    }

    public double invSqrt(double x) {
        return org.joml.Math.invsqrt(x);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public double fastInvSqrt(double x) {
        double d = 0.5 * x;
        long l = Double.doubleToRawLongBits(x);
        l = 6910469410427058090L - (l >> 1);
        x = Double.longBitsToDouble(l);
        x *= 1.5 - d * x * x;
        return x;
    }

    public float fastInvCubeRoot(float x) {
        int i = Float.floatToIntBits(x);
        i = 1419967116 - i / 3;
        float f = Float.intBitsToFloat(i);
        f = 0.6666667F * f + 1.0F / (3.0F * f * f * x);
        f = 0.6666667F * f + 1.0F / (3.0F * f * f * x);
        return f;
    }

    public int murmurHash3Mixer(int value) {
        value ^= value >>> 16;
        value *= -2048144789;
        value ^= value >>> 13;
        value *= -1028477387;
        value ^= value >>> 16;
        return value;
    }

    public int binarySearch(int min, int max, IntPredicate predicate) {
        int i = max - min;

        while (i > 0) {
            int j = i / 2;
            int k = min + j;
            if (predicate.test(k)) {
                i = j;
            } else {
                min = k + 1;
                i -= j + 1;
            }
        }

        return min;
    }

    public int lerpInt(float delta, int start, int end) {
        return start + floor(delta * (float) (end - start));
    }

    public float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    public double lerp2(double deltaX, double deltaY, double x0y0, double x1y0, double x0y1, double x1y1) {
        return lerp(deltaY, lerp(deltaX, x0y0, x1y0), lerp(deltaX, x0y1, x1y1));
    }

    public double lerp3(double deltaX, double deltaY, double deltaZ, double x0y0z0, double x1y0z0, double x0y1z0, double x1y1z0, double x0y0z1, double x1y0z1, double x0y1z1, double x1y1z1) {
        return lerp(deltaZ, lerp2(deltaX, deltaY, x0y0z0, x1y0z0, x0y1z0, x1y1z0), lerp2(deltaX, deltaY, x0y0z1, x1y0z1, x0y1z1, x1y1z1));
    }

    public float catmullrom(float delta, float p0, float p1, float p2, float p3) {
        return 0.5F * (2.0F * p1 + (p2 - p0) * delta + (2.0F * p0 - 5.0F * p1 + 4.0F * p2 - p3) * delta * delta + (3.0F * p1 - p0 - 3.0F * p2 + p3) * delta * delta * delta);
    }

    public double smoothstep(double value) {
        return value * value * value * (value * (value * 6.0 - 15.0) + 10.0);
    }

    public double smoothstepDerivative(double value) {
        return 30.0 * value * value * (value - 1.0) * (value - 1.0);
    }

    public int sign(double value) {
        if (value == 0.0) {
            return 0;
        } else {
            return value > 0.0 ? 1 : -1;
        }
    }

    public float rotLerp(float delta, float start, float end) {
        return start + delta * wrapDegrees(end - start);
    }

    public float triangleWave(float value, float maxDeviation) {
        return (Math.abs(value % maxDeviation - maxDeviation * 0.5F) - maxDeviation * 0.25F) / (maxDeviation * 0.25F);
    }

    public float square(float n) {
        return n * n;
    }

    public double square(double n) {
        return n * n;
    }

    public int square(int n) {
        return n * n;
    }

    public long square(long n) {
        return n * n;
    }

    public double clampedMap(double value, double oldStart, double oldEnd, double newStart, double newEnd) {
        return clampedLerp(newStart, newEnd, inverseLerp(value, oldStart, oldEnd));
    }

    public float clampedMap(float value, float oldStart, float oldEnd, float newStart, float newEnd) {
        return clampedLerp(newStart, newEnd, inverseLerp(value, oldStart, oldEnd));
    }

    public double map(double value, double oldStart, double oldEnd, double newStart, double newEnd) {
        return lerp(inverseLerp(value, oldStart, oldEnd), newStart, newEnd);
    }

    public float map(float value, float oldStart, float oldEnd, float newStart, float newEnd) {
        return lerp(inverseLerp(value, oldStart, oldEnd), newStart, newEnd);
    }

    public double wobble(double d) {
        return d + (2.0 * new Random(floor(d * 3000.0)).nextDouble() - 1.0) * 1.0E-7 / 2.0;
    }

    public int roundToward(int value, int divisor) {
        return positiveCeilDiv(value, divisor) * divisor;
    }

    public int positiveCeilDiv(int a, int b) {
        return -Math.floorDiv(-a, b);
    }

    public int randomBetweenInclusive(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public float randomBetween(Random random, float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }

    public float normal(Random random, float mean, float deviation) {
        return mean + (float) random.nextGaussian() * deviation;
    }

    public double lengthSquared(double a, double b) {
        return a * a + b * b;
    }

    public double length(double a, double b) {
        return Math.sqrt(lengthSquared(a, b));
    }

    public double lengthSquared(double a, double b, double c) {
        return a * a + b * b + c * c;
    }

    public double length(double a, double b, double c) {
        return Math.sqrt(lengthSquared(a, b, c));
    }

    public int quantize(double a, int b) {
        return floor(a / (double) b) * b;
    }

    public IntStream outFromOrigin(int seed, int lowerBound, int upperBound) {
        return outFromOrigin(seed, lowerBound, upperBound, 1);
    }

    public IntStream outFromOrigin(int seed, int lowerBound, int upperBound, int steps) {
        if (lowerBound > upperBound) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "upperbound %d expected to be > lowerBound %d", upperBound, lowerBound));
        } else if (steps < 1) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "steps expected to be >= 1, was %d", steps));
        } else {
            return seed >= lowerBound && seed <= upperBound ? IntStream.iterate(seed, (i) -> {
                int m = Math.abs(seed - i);
                return seed - m >= lowerBound || seed + m <= upperBound;
            }, (i) -> {
                boolean bl = i <= seed;
                int n = Math.abs(seed - i);
                boolean bl2 = seed + n + steps <= upperBound;
                if (!bl || !bl2) {
                    int o = seed - n - (bl ? steps : 0);
                    if (o >= lowerBound) {
                        return o;
                    }
                }

                return seed + n + steps;
            }) : IntStream.empty();
        }
    }

    static {
        for (int i = 0; i < 257; ++i) {
            double d = (double) i / 256.0;
            double e = Math.asin(d);
            COS_TAB[i] = Math.cos(e);
            ASIN_TAB[i] = e;
        }
    }
}
