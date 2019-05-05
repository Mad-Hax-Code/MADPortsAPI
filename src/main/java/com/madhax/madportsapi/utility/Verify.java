package com.madhax.madportsapi.utility;

public class Verify {

    public static boolean validPort(int port) {
        if (port >= 0 && port < 65536) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validPortRange(int startPort, int endPort) {
        if (validPort(startPort) && validPort(endPort) && startPort < endPort) {
            return true;
        } else {
            return false;
        }
    }

    public static int totalPortsInRange(int startPort, int endPort) {
        return (endPort - startPort) + 1;
    }

}
