package com.madhax.madportsapi.service;

import com.madhax.madportsapi.model.PortResult;
import com.madhax.madportsapi.model.ScanResult;
import com.madhax.madportsapi.utility.PortScanner;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Scan {

    public static ScanResult scanPorts(
            String hostname,
            int startPort,
            int endPort,
            int timeout,
            boolean parallel) throws ExecutionException, InterruptedException {

        return scanPorts(
                hostname,
                startPort,
                endPort,
                timeout,
                parallel,
                0);
    }

    public static ScanResult scanPorts(
            String hostname,
            int startPort,
            int endPort,
            int timeout,
            boolean parallel,
            int sleep) throws ExecutionException, InterruptedException {

        CompletableFuture<ScanResult> completableFuture = CompletableFuture.supplyAsync(() -> {

            long startTime = new Date().getTime();
            List<PortResult> portResults = PortScanner.scanRange(
                    hostname,
                    startPort,
                    endPort,
                    timeout,
                    parallel,
                    sleep);

            long endTime = new Date().getTime();

            return new ScanResult(
                    hostname,
                    startPort,
                    endPort,
                    timeout,
                    startTime,
                    endTime,
                    portResults);
        });

        return completableFuture.get();
    }

}
