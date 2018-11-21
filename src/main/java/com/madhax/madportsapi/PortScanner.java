package com.madhax.madportsapi;

import com.madhax.madportsapi.model.ScanResult;
import com.madhax.madportsapi.model.PortResult;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.Date;

public abstract class PortScanner {
    
    /* Overloaded scanPort methods */
    
    /**
     * Without timeout
     * @param hostname
     * @param port
     * @return 
     */
    public static PortResult scanPort(String hostname, int port) {
        return scanPort(hostname, port, 150);
    }
    
    /**
     * This method scans the port by attempting to make a Socket connection. 
     * @param hostname
     * @param port
     * @param timeout
     * @return 
     */
    public static PortResult scanPort(String hostname, int port, int timeout) {
        if(validPort(port)) { // if port is in valid range
            // use try with resources to create a new Socket object and attemp to connect
            try (Socket socket = new Socket()) { // if the connection was successful
                // make the connection
                socket.connect(new InetSocketAddress(hostname, port), timeout);
                // return the result
                return new PortResult(port, 1);
            } catch (IOException e) { // if the connection was unsuccessful
                // return the result
                return new PortResult(port, 2);
            } // END try/catch
        } else { // if the port is out of range
            System.err.println("Error: Port out of range: " + port);
            return null;
        }
    }
    
    /**
     * Scan the given port range
     * @param startPort
     * @param endPort
     * @param hostname
     * @param timeout
     * @return 
     */
    public static LinkedList<PortResult> scanPortRange(String hostname, int startPort, int endPort, int timeout) {
        
        LinkedList<PortResult> portResults = new LinkedList<>();
        
        // Ensure input is valid
        if (validPort(startPort) && validPort(endPort) && !hostname.isEmpty()) {
            
            // iterate through the ports
            for (int port = startPort; port <= endPort; port++) {
                portResults.add(scanPort(hostname, port, timeout));
            }
            
            return portResults;
            
        } else { // if port is beyond max range, break loop
            System.err.println("Error! Ports are out of range: " + startPort + "-" + endPort);
            return null;
        } // END input validation conditionalt  
    }
    
    /* Overloaded scan methods
       These methods are responsible for creating the PortScannerThreads and
       distributing the overall port load evenly between the threads.
    */
    
    /**
     * No timeout or threadCount provided
     * @param hostname
     * @param startPort
     * @param endPort
     * @return 
     */
    public static ScanResult multiThreadedScan(String hostname, int startPort, int endPort) {
        return multiThreadedScan(hostname, startPort, endPort, 150, 4);
    }
    
    /**
     * No threadCount provided
     * @param hostname
     * @param startPort
     * @param endPort
     * @param timeout
     * @return 
     */
    public static ScanResult multiThreadedScan(String hostname, int startPort, int endPort, int timeout) {
        return multiThreadedScan(hostname, startPort, endPort, timeout, 4);
    }
    
    /**
     * All parameters provided
     * @param hostname
     * @param startPort
     * @param endPort
     * @param timeout
     * @param threadCount
     * @return 
     */
    public static ScanResult multiThreadedScan(String hostname, int startPort, int endPort, int timeout, int threadCount) {
        
        // if port range is invalid, exit the method
        if (!validPort(startPort) || !validPort(endPort)) {
            System.err.println("Invalid port range: " + startPort + "- " + endPort);
            return null;
        }
        
        LinkedList<PortResult> portResults = new LinkedList<>(); // Stores all scanned port results
        long startTime;
        long endTime;
        
        // if there are more threads than ports, fix threadCount
        int totalPorts = totalPortsInRange(startPort, endPort);
        if (threadCount > totalPorts) {
            threadCount = totalPorts;
        }
        
        // Create the thread pool
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        // Create an array of tasks(these will be the threads submitted to the pool)
        PortScannerThread[] tasks = new PortScannerThread[threadCount];

        int portInterval = determinePortInterval(startPort, endPort, threadCount);
        int threadIntervalStart = startPort;
        int threadIntervalEnd = startPort + portInterval;
        
        // load the threads
        for (int i = 0; i < threadCount; i++) {

            tasks[i] = new PortScannerThread(hostname, threadIntervalStart, threadIntervalEnd, timeout);

            // Increment the start port for the next thread
            threadIntervalStart += portInterval + 1;
            // Increment the end port for the next thread
            threadIntervalEnd += portInterval + 1;

            // If threadIntervalEnd + portInterval is greater than the endPort
            // given by the user, set the threadIntervalEnd to the user specified
            // endPort
            if (threadIntervalEnd > endPort) {
                threadIntervalEnd = endPort;
            }
        }
        // Create an array of futures to store our results
        Future<LinkedList<PortResult>>[] future = new Future[threadCount];
        
        // Record the start time of the scan
        Date scanStart = new Date();
        startTime = scanStart.getTime();
        
        // run the threads by submitting the tasks to the pool
        for (int i = 0; i < threadCount; i++) {
            future[i] = pool.submit(tasks[i]);
        }
        // Access the Future to get the ordered results of our scan
        for (int i = 0; i < threadCount; i++) {
            try {
                portResults.addAll(future[i].get());
            } catch (Exception e) {
                System.err.println("Error! " + e);
            }
        }
        
        pool.shutdown(); // shutdown the pool so the program knows to continue
        
        // Record the end time of the scan
        Date scanStopped = new Date();
        endTime = scanStopped.getTime();
        
        ScanResult scanResults = new ScanResult(hostname,
                                           startPort,
                                           endPort,
                                           timeout,
                                           threadCount,
                                           startTime,
                                           endTime,
                                           portResults);
        return scanResults;
    }
    
    /**
     * Check to see if port is in valid range
     * @param port
     * @return 
     */
    public static boolean validPort(int port) {
        if (port > 0 && port < 65536) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Calculate the total number of ports
     * @param startPort
     * @param endPort
     * @return 
     */
    public static int totalPortsInRange(int startPort, int endPort) {
        return (endPort - startPort) + 1;
    }
    
    /**
     * Calculate the size of the port interval
     * @param startPort
     * @param endPort
     * @param threadCount
     * @return 
     */
    private static int determinePortInterval(int startPort, int endPort, int threadCount) {
        return (totalPortsInRange(startPort, endPort) / threadCount) + 1;
    }
    
} // END PortScannerController class