/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import com.lucwo.fourcharm.model.Game;

import java.util.Observer;

public interface FourCharmView extends Observer, Runnable {

// ------------------ Instance variables ----------------

// --------------------- Constructors -------------------

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    public void showGame(Game game);

    public void showNewGame();

    public void enableInput();

    public int requestMove();

}
