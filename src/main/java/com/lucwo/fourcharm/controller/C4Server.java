/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.controller;

import java.net.InetAddress;

public class C4Server {

// ------------------ Instance variables ----------------

    private InetAddress address;
    private int port;
    private String name;

// --------------------- Constructors -------------------

    public C4Server(String theName, InetAddress addr, int p) {
        name = theName;
        address = addr;
        port = p;
    }

// ----------------------- Queries ----------------------

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

// ----------------------- Commands ---------------------

}
