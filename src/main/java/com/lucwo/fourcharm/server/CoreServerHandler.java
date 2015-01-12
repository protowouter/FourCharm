/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.ParameterFormatException;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreClient;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreServer;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class CoreServerHandler implements CoreServer.Iface {

    private CoreClient.Client client;

    public CoreServerHandler(CoreClient.Client c) {

        client = c;

    }

    @Override
    public void join(String pName, int gNumber, Set<String> exts) throws ParameterFormatException {
        Logger.getGlobal().info("Received join for user " + pName);
        client.accept(23, new HashSet<>());
    }

// ------------------ Instance variables ----------------

// --------------------- Constructors -------------------

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

}
