package main.java.pt.ipp.isep.dei.util;

/**
 * Utility class providing static methods for common object manipulations and checks.
 * <p>
 * This class is designed to simplify validation and comparisons across various objects.
 * </p>
 */
public class ObjectUtils {

    /** Placeholder for a constant, though not used in the current public methods. */
    private static final char AT_SIGN = '@';


    /**
     * Checks if all provided objects are non-null.
     * <p>
     * Returns {@code false} if the array of values itself is null, or if any value
     * within the array is null.
     * </p>
     *
     * @param values a variable number of objects to check
     * @return {@code true} if all objects are not null, {@code false} otherwise
     */
    public static boolean allNotNull(Object... values) {
        if (values == null) {
            return false;
        } else {
            for(Object val : values) {
                if (val == null) {
                    return false;
                }
            }

            return true;
        }
    }



}