package lets.cool.util;

public class ByteUtil {

    public static byte[] concat(byte[] bytes1, byte[] bytes2) {
        byte[] outBytes = new byte[bytes1.length + bytes2.length];

        System.arraycopy(bytes1, 0, outBytes, 0, bytes1.length);
        System.arraycopy(bytes2, 0, outBytes, bytes1.length, bytes2.length);

        return outBytes;
    }
}
