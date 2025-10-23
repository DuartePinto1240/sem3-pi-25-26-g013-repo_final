package main.java.pt.ipp.isep.dei.util;

public class ObjectUtils {
    private static final char AT_SIGN = '@';


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

