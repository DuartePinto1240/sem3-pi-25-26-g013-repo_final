package main.java.pt.ipp.isep.dei.util;

/**
 * Utility class providing static methods for common operations on {@link String} and {@link CharSequence} objects.
 * <p>
 * This class is a helper for checking string properties like length and content (e.g., blankness).
 * </p>
 */
public class StringUtils {

    /**
     * Checks if a {@link CharSequence} is null, empty, or contains only whitespace characters.
     *
     * @param cs the {@link CharSequence} to check, may be null
     * @return {@code true} if the {@link CharSequence} is null, empty, or whitespace-only, {@code false} otherwise
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen = length(cs);
        if (strLen == 0) {
            return true;
        } else {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * Gets the length of a {@link CharSequence} or 0 if the sequence is null.
     *
     * @param cs the {@link CharSequence} to check, may be null
     * @return the length of the {@link CharSequence}, or 0 if the {@link CharSequence} is null
     */
    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }
}