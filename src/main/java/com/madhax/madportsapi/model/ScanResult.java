package com.madhax.madportsapi.model;

import java.util.LinkedList;
import java.util.List;

public class ScanResult {
    
    public final String HOSTNAME;
    public final int START_PORT;
    public final int END_PORT;
    public final int TIMEOUT;
    public final long START_TIME;
    public final long END_TIME;
    
    private final List<PortResult> portResults;
    
    public ScanResult(String hostname,
                      int startPort,
                      int endPort,
                      int timeout,
                      long startTime,
                      long endTime,
                      List<PortResult> portResults) {
        
        this.HOSTNAME = hostname;
        this.START_PORT = startPort;
        this.END_PORT = endPort;
        this.TIMEOUT = timeout;
        this.START_TIME = startTime;
        this.END_TIME = endTime;
        this.portResults = portResults;
        
    } // END constructor method

    public int getTotalPortsScanned() {
        return this.portResults.size();
    }

    public int getScanTimeSeconds() {
        int seconds = (int) ((this.END_TIME - this.START_TIME) / 1000);
        return seconds;
    }

    public long getScanTimeMS() {
        long milliseconds = this.END_TIME - this.START_TIME;
        return milliseconds;
    }

    public List<PortResult> getPortResults() {
        return this.portResults;
    }

    public void printPortResults() {
        this.portResults.forEach(System.out::println);
    }

    @Override
    public String toString() {
        String output = "Host:" + this.HOSTNAME + ","
                + "StartPort:" + this.START_PORT + ","
                + "EndPort:" + this.END_PORT + ","
                + "Timeout:" + this.TIMEOUT + ","
                + "StartTime:" + this.START_TIME + ","
                + "EndTime:" + this.END_TIME + "," 
                + "TotalPorts:" + this.getTotalPortsScanned() + ","
                + "ScanTime:" + this.getScanTimeMS();
        
        return output;
    } // END toString() method   
}