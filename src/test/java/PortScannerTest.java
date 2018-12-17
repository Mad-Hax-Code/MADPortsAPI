import com.madhax.madportsapi.PortScanner;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PortScannerTest {

    @Test
    public void totalPortsInRangeTest() {
        int startPort = 1;
        int endPort = 1000;
        int expectedResult = 1000;
        int actualResult = PortScanner.totalPortsInRange(startPort, endPort);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void determinePortIntervalTest1() {
        int startPort = 1;
        int endPort = 1000;
        int threadCount = 100;

        int expectedResult = 10;
        int actualResult = PortScanner.determinePortInterval(startPort, endPort, threadCount);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void determinePortIntervalTest2() {
        int startPort = 1;
        int endPort = 975;
        int threadCount = 100;

        int expectedResult = 10;
        int actualResult = PortScanner.determinePortInterval(startPort, endPort, threadCount);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validPortTest1() {
        int port = -1;
        boolean actualResult = PortScanner.validPort(port);

        assertEquals(false, actualResult);
    }

    @Test
    public void validPortTest2() {
        int port = 65536;
        boolean actualResult = PortScanner.validPort(port);

        assertEquals(false, actualResult);
    }

    @Test
    public void validPortTest3() {
        int port = 100;
        boolean actualResult = PortScanner.validPort(port);

        assertEquals(true, actualResult);
    }

    @Test
    public void validThreadCountTest1() {
        int threadCount = 100;
        int totalPorts = 1000;

        int actualResult = PortScanner.validateThreadCount(totalPorts, threadCount);

        assertEquals(100, actualResult);
    }

    @Test
    public void validThreadCountTest2() {
        int threadCount = 100;
        int totalPorts = 50;

        int actualResult = PortScanner.validateThreadCount(totalPorts, threadCount);

        assertEquals(50, actualResult);
    }

    @Test
    public void validPortRangeTest1() {
        int startPort = 1;
        int endPort = 100;

        boolean actualResult = PortScanner.validPortRange(startPort, endPort);

        assertEquals(true, actualResult);
    }

    @Test
    public void validPortRangeTest2() {
        int startPort = 100;
        int endPort = 99;

        boolean actualResult = PortScanner.validPortRange(startPort, endPort);

        assertEquals(false, actualResult);
    }

}
