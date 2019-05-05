package com.madhax.madportsapi.model;

public class PortResult implements Comparable<PortResult> {

    private final int port;
    private final PortState portState; // 1 for open and -1 for closed

    public PortResult(int port, PortState portState) {
        this.port = port;
        this.portState = portState;
    }

    public int getPort() {
        return port;
    }

    public PortState getPortState() {
        return portState;
    }

    @Override
    public String toString() {
        String output = "";
        output += "Port:" + this.port + ",";
        output += "State:" + this.portState.name();
        return output;
    }

    @Override
    public int compareTo(PortResult other) {
        return Integer.compare(this.port, other.port);
    }
}