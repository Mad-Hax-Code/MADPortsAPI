package com.madhax.madportsapi;

import com.madhax.madportsapi.model.ScanResult;
import com.madhax.madportsapi.model.PortResult;

import java.io.IOException;
import java.net.InetAddress;
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
     *
     * @param hostname
     * @param port
     * @return
     */
    public static PortResult scanPort(String hostname, int port) {
        return scanPort(hostname, port, 150);
    }

    /**
     * This method scans the port by attempting to make a Socket connection.
     *
     * @param hostname
     * @param port
     * @param timeout
     * @return
     */
    public static PortResult scanPort(String hostname, int port, int timeout) {
        if (validPort(port)) { // if port is in valid range
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
     *
     * @param startPort
     * @param endPort
     * @param hostname
     * @param timeout
     * @return
     */
    public static LinkedList<PortResult> scanPortRange(String hostname, int startPort, int endPort, int timeout) {

        LinkedList<PortResult> portResults = new LinkedList<>();

        if (validPortRange(startPort, endPort) && !hostname.isEmpty()) {
            // iterate through the ports
            for (int port = startPort; port <= endPort; port++) {
                portResults.add(scanPort(hostname, port, timeout));
            }
            return portResults;
        } else { // if port is beyond max range, break loop
            System.err.println("Error! Ports are out of range: " + startPort + "-" + endPort);
            return null;
        } // END input validation conditional

    }
    
    /* Overloaded scan methods
       These methods are responsible for creating the PortScannerThreads and
       distributing the overall port load evenly between the threads.
    */

    /**
     * No timeout or threadCount provided
     * @param hostname
     * @param startinPortForScan
     * @param endingPortForScan
     * @return
     */
    public static ScanResult multiThreadedScan(String hostname, int startinPortForScan, int endingPortForScan) {
        return multiThreadedScan(hostname, startinPortForScan, endingPortForScan, 150, 4);
    }

    /**
     * No threadCount provided
     * @param hostname
     * @param startinPortForScan
     * @param endingPortForScan
     * @param timeout
     * @return
     */
    public static ScanResult multiThreadedScan(String hostname, int startinPortForScan, int endingPortForScan, int timeout) {
        return multiThreadedScan(hostname, startinPortForScan, endingPortForScan, timeout, 4);
    }

    /**
     * All parameters provided
     * @param hostname
     * @param startingPortForScan
     * @param endingPortForScan
     * @param timeout
     * @param totalThreads
     * @return
     */
    public static ScanResult multiThreadedScan(String hostname, int startingPortForScan, int endingPortForScan, int timeout, int totalThreads) {

        // if port range is invalid, exit the method
        if (!validPortRange(startingPortForScan, endingPortForScan)) {
            System.err.println("Invalid port range: " + startingPortForScan + "- " + endingPortForScan);
            return null;
        }

        totalThreads = validateThreadCount(totalPortsInRange(startingPortForScan, endingPortForScan), totalThreads);

        ExecutorService pool = Executors.newFixedThreadPool(totalThreads);
        PortScannerThread[] tasks = new PortScannerThread[totalThreads];
        Future<LinkedList<PortResult>>[] future = new Future[totalThreads];

        int portInterval = determinePortInterval(startingPortForScan, endingPortForScan, totalThreads);
        int startingPortForThread = startingPortForScan;
        int endingPortForThread = startingPortForScan + portInterval;

        // load the threads
        for (int i = 0; i < totalThreads; i++) {

            if (endingPortForThread > endingPortForScan) {
                endingPortForThread = endingPortForScan;
            }

            tasks[i] = new PortScannerThread(hostname, startingPortForThread, endingPortForThread, timeout);

            // Update the port range for next thread
            startingPortForThread += portInterval;
            endingPortForThread += portInterval;

            System.out.println("LOADED THREAD #: " + i);
        }

        // Record the start time of the scan
        long startTime = new Date().getTime();

        // run the threads by submitting the tasks to the pool
        for (int i = 0; i < tasks.length; i++) {
            System.out.println("TASK INDEX: " + i);
            future[i] = pool.submit(tasks[i]);
        }

        LinkedList<PortResult> portResults = new LinkedList<>(); // Stores all scanned port results
        // Access the Future to get the ordered results of our scan
        for (int i = 0; i < future.length; i++) {
            try {
                portResults.addAll(future[i].get());
            } catch (Exception e) {
                System.err.println("Error! " + e);
            }
        }

        pool.shutdown(); // shutdown the pool so the program knows to continue

        // Record the end time of the scan
        long endTime = new Date().getTime();

        ScanResult scanResults = new ScanResult(hostname,
                startingPortForScan,
                endingPortForScan,
                timeout,
                totalThreads,
                startTime,
                endTime,
                portResults);

        return scanResults;
    }

    public static String hostnameLookup(String hostAddress) {
        String hostName = "";
        try {
            InetAddress address = InetAddress.getByName(hostAddress);
            hostName = address.getCanonicalHostName(); // force DNS lookup
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hostName;
    }

    /**
     * Check to see if port is in valid range
     *
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

    public static boolean validPortRange(int startPort, int endPort) {
        if (validPort(startPort) && validPort(endPort) && startPort < endPort) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calculate the total number of ports
     *
     * @param startPort
     * @param endPort
     * @return
     */
    public static int totalPortsInRange(int startPort, int endPort) {
        return (endPort - startPort) + 1;
    }

    public static int validateThreadCount(int totalPorts, int totalThreads) {
        int validThreadCount = totalThreads;
        // if there are more threads than ports, fix threadCount
        if (totalThreads > totalPorts) {
            validThreadCount = totalPorts;
        }
        return validThreadCount;
    }

    /**
     * Calculate the size of the port interval
     *
     * @param startPort
     * @param endPort
     * @param threadCount
     * @return
     */
    public static int determinePortInterval(int startPort, int endPort, int threadCount) {
        if ( (totalPortsInRange(startPort, endPort) % threadCount) == 0) {
            return (totalPortsInRange(startPort, endPort) / threadCount);
        } else {
            return (totalPortsInRange(startPort, endPort) / threadCount) + 1;
        }
    }

} // END PortScannerController class