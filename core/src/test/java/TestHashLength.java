import static filldb.core.Util.*;

public class TestHashLength {

    public static void main(final String... args) {
        System.out.println(bytesToHex(sha256("boe")).length());
        System.out.println(bytesToHex(sha1("boe")).length());
        System.out.println(bytesToHex(md5("boe")).length());
    }
}
