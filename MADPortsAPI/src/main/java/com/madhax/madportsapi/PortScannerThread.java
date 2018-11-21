package com.madhax.madportsapi;

import com.madhax.madportsapi.model.PortResult;
import java.util.LinkedList;
import java.util.concurrent.Callable;

public class PortScannerThread implements Callable<LinkedList<PortResult>> {

    // Instance variables:
    private final String hostname;
    private final int startPort;
    private final int endPort;
    private final int timeout;

    // Overloaded constructors:
    
    /**
     * Constructor with no timeout specified
     * @param hostname
     * @param startPort
     * @param endPort 
     */
    public PortScannerThread(String hostname, int startPort, int endPort) {
        this(hostname, startPort, endPort, 200);
    }
    /**
     * Constructor with timeout specified
     * @param hostname
     * @param startPort
     * @param endPort
     * @param timeout 
     */
    public PortScannerThread(String hostname, int startPort, int endPort, int timeout) {
        this.hostname = hostname;
        this.startPort = startPort;
        this.endPort = endPort;
        this.timeout = timeout;
    }
    
    /**
     * Returns the hostname from the scan.
     * @return 
     */
    public String getHostName() {
        return this.hostname;
    }

    /**
     * Returns the starting port for the scan.
     * @return 
     */
    public int getStartPort() {
        return this.startPort;
    }
   
    /**
     * Returns the ending port for the scan.
     * @return 
     */
    public int getEndPort() {
        return this.endPort;
    }
    
    /**
     * Returns the timeout value used for the scan.
     * @return 
     */
    public int getTimeout() {
        return this.timeout;
    }
    
    /**
     * This is the method that will execute when the thread is created and is the
     *  actually port scanning process.
     * @return
     * @throws Exception 
     */
    @Override
    public LinkedList<PortResult> call() throws Exception {
        return PortScanner.scanPortRange(hostname, startPort, endPort, timeout);
        // return scanGivenPorts(this.startPort, this.endPort, this.hostname);
    }
    
} // END PortScannerThread class