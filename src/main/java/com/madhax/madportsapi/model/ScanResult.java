package com.madhax.madportsapi.model;

import com.madhax.madportsapi.model.PortResult;
import java.util.LinkedList;

public class ScanResult {
    
    public final String HOSTNAME;
    public final int START_PORT;
    public final int END_PORT;
    public final int TIMEOUT;
    public final int THREADS;
    public final long START_TIME;
    public final long END_TIME;
    
    private final LinkedList<PortResult> scanResults;
    
    public ScanResult(String hostname,
                      int startPort,
                      int endPort,
                      int timeout,
                      int threads,
                      long startTime,
                      long endTime,
                      LinkedList<PortResult> scanResults) {
        
        this.HOSTNAME = hostname;
        this.START_PORT = startPort;
        this.END_PORT = endPort;
        this.TIMEOUT = timeout;
        this.THREADS = threads;
        this.START_TIME = startTime;
        this.END_TIME = endTime;
        this.scanResults = scanResults;
        
    } // END constructor method
    
    /*
    Get total ports scanned
    */
    public int getTotalPortsScanned() {
        return this.scanResults.size();
    }

    /*
    Get the scan time (in seconds)
    */
    public int getScanTimeSeconds() {
        int seconds = (int) ((this.END_TIME - this.START_TIME) / 1000);
        return seconds;
    }
    
    /*
    Get the scan time (in milliseconds)
    */
    public long getScanTimeMS() {
        long milliseconds = this.END_TIME - this.START_TIME;
        return milliseconds;
    }
    
    /*
    Get scan results
    */
    public LinkedList<PortResult> getScanResults() {
        return this.scanResults;
    }
    
    /*
    Print all results
    */
    public void printAll() {
        this.scanResults.forEach((result) -> {
            System.out.print(result.toString() + "\n");
        });
    }
    /*
    Output the object data as a string
    */
    @Override
    public String toString() {
        String output = "Host:" + this.HOSTNAME + ","
                + "StartPort:" + this.START_PORT + ","
                + "EndPort:" + this.END_PORT + ","
                + "Timeout:" + this.TIMEOUT + "," 
                + "Threads:" + this.THREADS + ","
                + "StartTime:" + this.START_TIME + ","
                + "EndTime:" + this.END_TIME + "," 
                + "TotalPorts:" + this.getTotalPortsScanned() + ","
                + "ScanTime:" + this.getScanTimeMS();
        
        return output;
    } // END toString() method   
}