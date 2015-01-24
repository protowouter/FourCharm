/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.controller;

import com.lucwo.fourcharm.server.FourCharmServer;
import com.lucwo.fourcharm.view.FourCharmServerTUI;

public class FourCharmServerController {

// ------------------ Instance variables ----------------

    private FourCharmServer server;
    private FourCharmServerTUI view;

// --------------------- Constructors -------------------

    public FourCharmServerController() {

        view = new FourCharmServerTUI();
        Thread viewThread = new Thread(view);
        viewThread.setName("FourCharmServerView");
        viewThread.start();


    }

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    public static void main(String[] args) {
        new FourCharmServerController();
    }

}
