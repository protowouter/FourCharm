/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.controller;

import com.lucwo.fourcharm.exception.ServerStartException;
import com.lucwo.fourcharm.server.FourCharmServer;
import com.lucwo.fourcharm.view.FourCharmServerTUI;

import java.util.logging.Logger;

public class FourCharmServerController {

// ------------------ Instance variables ----------------

    private FourCharmServer server;
    private FourCharmServerTUI view;

// --------------------- Constructors -------------------

    public FourCharmServerController() {

        view = new FourCharmServerTUI(this);
        Thread viewThread = new Thread(view);
        viewThread.setName("FourCharmServerView");
        viewThread.start();


    }

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    public static void main(String[] args) {
        new FourCharmServerController();
    }

    public void startServer(int port) {
        try {
            server = new FourCharmServer(port);
            server.openSocket();
            new Thread(server::startServer).start();
        } catch (ServerStartException e) {
            Logger.getGlobal().throwing(getClass().toString(), "startServer", e);
            view.showError(e.getMessage());
        }

    }

    public void stopServer() {
        server.stop();
    }

}
