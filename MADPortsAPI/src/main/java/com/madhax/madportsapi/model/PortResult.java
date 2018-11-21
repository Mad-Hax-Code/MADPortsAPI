package com.madhax.madportsapi.model;

/*
The class implements Comparable so that the objects will be sortable
*/
public class PortResult implements Comparable<PortResult> {

    private int port; // store the actual port number
    private int state; // 1 for open and -1 for closed

    /*
    constructor assigns values to instance variables
    */
    public PortResult(int port, int state) {
        this.port = port; // assign input to instance variable
        this.state = state; // assign input to instance variable
    }

    /*
    Return the port value
    */
    public int getPort() {
        return this.port;
    }

    /*
    Return the state value
    */
    public int getState() {
        return this.state;
    }

    @Override
    public String toString() {
        String output = "";
        output += "Port:" + this.port + ",";
        output += "State:" + this.state;
        return output;
    }
    
    /*
    Make the class sortable with the compareTo function required by the
    Comparable interface
    */
    @Override
    public int compareTo(PortResult other) {
        return Integer.compare(this.port, other.port);
    }
}