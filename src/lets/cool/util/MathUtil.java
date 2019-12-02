package lets.cool.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {
    static public boolean isSameSign(int a, int b) {
        return ((a ^ b) & 0x80_00_00_00) == 0;
    }

    static public boolean isSameSign(long a, long b) {
        return ((a ^ b) & 0x80_00_00_00_00_00_00_00L) == 0;
    }

    static public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
