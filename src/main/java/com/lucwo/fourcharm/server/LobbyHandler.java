/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.inf3.c4prot.C4Color;
import nl.woutertimmermans.inf3.c4prot.C4Lobby;
import nl.woutertimmermans.inf3.c4prot.C4Player;
import nl.woutertimmermans.inf3.c4prot.C4Session;
import org.apache.thrift.TException;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class LobbyHandler implements C4Lobby.Iface {
    @Override
    public C4Session connect(String s) throws TException {
        Logger.getGlobal().info(s + " connected");
        return new C4Session(new Random().nextInt(), new C4Player(s, C4Color.BLACK));
    }

    @Override
    public List<C4Player> getPlayerList() throws TException {
        return null;
    }

// ------------------ Instance variables ----------------

// --------------------- Constructors -------------------

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

}
