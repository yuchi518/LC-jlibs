package lets.cool.util;

public class MathUtil {
    static public boolean isSameSign(int a, int b) {
        return ((a ^ b) & 0x80_00_00_00) == 0;
    }

    static public boolean isSameSign(long a, long b) {
        return ((a ^ b) & 0x80_00_00_00_00_00_00_00L) == 0;
    }
}
