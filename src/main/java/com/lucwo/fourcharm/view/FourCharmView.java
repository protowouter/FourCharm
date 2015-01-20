/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import com.lucwo.fourcharm.model.Game;

public interface FourCharmView {

// ------------------ Instance variables ----------------

// --------------------- Constructors -------------------

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    public void showGame(Game game);

    public void showNewGame();

    public void showRematch();

    public void enableInput();

    public int requestMove();

    public void showError(String errorMessage);

}
