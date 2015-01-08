/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.client;

import nl.woutertimmermans.inf3.c4prot.C4Lobby;
import nl.woutertimmermans.inf3.c4prot.C4Session;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.logging.Logger;

public class C4Client {

// ------------------ Instance variables ----------------

// --------------------- Constructors -------------------

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    public static void main(String[] args) {
        TTransport transport;
        transport = new TSocket("localhost", 9090);
        try {
            transport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
        }

        //TProtocol protocol = new TBinaryProtocol(transport);
        TProtocol protocol = new TBinaryProtocol(transport);
        C4Lobby.Client client = new C4Lobby.Client(protocol);
        try {
            C4Session ses = client.connect("Wouter");
            Logger.getGlobal().info(ses.toString());
        } catch (TException e) {
            e.printStackTrace();
        }
        transport.close();
    }



}
