/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.controller;

import com.lucwo.fourcharm.exception.ServerStartException;
import com.lucwo.fourcharm.server.FourCharmServer;
import com.lucwo.fourcharm.view.FourCharmServerTUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FourCharmServerController {

    public static final Logger LOGGER = LoggerFactory.getLogger(FourCharmServerController.class);

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
            view.showMessage("Listening for connections on port " + server.getSocketPort());
            Thread serverThread = new Thread(server::startServer);
            serverThread.setName("serverThread");
            serverThread.start();
        } catch (ServerStartException e) {
            LOGGER.trace("startServer", e);
            view.showError(e.getMessage());
            server = null;
        }

    }

    public void stopServer() {
        if (server != null) {
            server.stop();
            server = null;
            view.showMessage("Shutting down server");
        } else {
            view.showError("There was no server running");
        }

    }

}
