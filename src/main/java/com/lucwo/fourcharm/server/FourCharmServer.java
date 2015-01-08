/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.C4Lobby;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

public class FourCharmServer {

    public static C4Lobby.Processor processor;
    public static LobbyHandler handler;

// ------------------ Instance variables ----------------



// --------------------- Constructors -------------------

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------


    public static void main(String[] args) {
        try {
            handler = new LobbyHandler();
            processor = new C4Lobby.Processor(handler);
            TServerTransport serverTransport = new TServerSocket(9090);
            TServer.AbstractServerArgs sargs = new TServer.Args(serverTransport).processor(processor);
            sargs.protocolFactory(new TBinaryProtocol.Factory());
            TServer server = new TSimpleServer(sargs);

            // Use this for a multithreaded server
            // TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

}
