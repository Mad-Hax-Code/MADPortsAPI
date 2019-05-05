package com.madhax.madportsapi.utility;

import com.madhax.madportsapi.exception.InvalidPortRangeException;
import com.madhax.madportsapi.model.PortResult;
import com.madhax.madportsapi.model.PortState;
import static com.madhax.madportsapi.utility.Verify.validPortRange;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class PortScanner {

    public static PortResult scanPort(String hostname, int port) {
        return scanPort(hostname, port, 150, 0);
    }

    public static PortResult scanPort(String hostname, int port, int timeout) {
        return scanPort(hostname, port, timeout, 0);
    }

    public static PortResult scanPort(String hostname, int port, int timeout, int sleep) {

        try (Socket socket = new Socket()) {

            if (sleep > 0) {
                Thread.sleep(sleep);
            }
            socket.connect(new InetSocketAddress(hostname, port), timeout);
            return new PortResult(port, PortState.OPEN);

        } catch (IOException e) {

            return new PortResult(port, PortState.CLOSED);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<PortResult> scanRange(String hostname, int startPort, int endPort, int timeout, boolean parallel, int sleep) {

        if(!validPortRange(startPort, endPort)) {
            throw new InvalidPortRangeException(
                    String.format("Start: %d End %d -> Range must be between 0-65535", startPort, endPort)
            );
        }

        if (parallel) {
            return IntStream.rangeClosed(startPort, endPort)
                    .parallel()
                    .mapToObj(port -> scanPort(hostname, port, timeout))
                    .collect(Collectors.toList());
        } else {
            return IntStream.rangeClosed(startPort, endPort)
                    .mapToObj(port -> scanPort(hostname, port, timeout, sleep))
                    .collect(Collectors.toList());
        }
    }

    public static String hostnameLookup(String hostAddress) {
        try {
            InetAddress address = InetAddress.getByName(hostAddress);
            return address.getCanonicalHostName(); // force DNS lookup
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}