import gossip.app.listener.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MacIPTests {
    @Test
    void testIpToBin() {
        Assertions.assertEquals(Parser.IPToBin("165.34.234.12"), "10100101001000101110101000001100");
        Assertions.assertEquals(Parser.IPToBin("255.255.255.255"), "11111111111111111111111111111111");
        Assertions.assertEquals(Parser.IPToBin("0.0.0.0"), "00000000000000000000000000000000");
    }

    @Test
    void testMacToBin() {
        Assertions.assertEquals(Parser.MacToBin("ff:ff:ff:ff:ff:ff"), "111111111111111111111111111111111111111111111111");
    }

    @Test
    void testMulticast() {
        Assertions.assertTrue(Parser.IsMulticastMacCorrespondsToIP("224.127.0.1", "01:00:5e:7f:00:01"));
        Assertions.assertFalse(Parser.IsMulticastMacCorrespondsToIP("192.168.0.1", "01:00:5e:7f:00:01"));
    }
}
