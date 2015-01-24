/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import com.lucwo.fourcharm.model.Game;

/**
 * Interface for the views (graphical and textual) of FourCharm Connect4.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public interface FourCharmView {

// ------------------ Instance variables ----------------

// --------------------- Constructors -------------------

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    /**
     * Shows a game that is given as a parameter.
     * @param game The given game.
     */
    public void showGame(Game game);

    /**
     * Shows a new game.
     */
    public void showNewGame();

    /**
     * Shows a rematch.
     */
    public void showRematch();

    /**
     * Enables the input.
     */
    public void enableInput();


    /**
     * Enable the hint functionality.
     * This must only be done if it is the current turn of a human.
     */
    public void enableHint();

    /**
     * Disables the hint functionality.
     */
    public void disableHint();

    /**
     * Request a move.
     * @return The requested column number (a.k.a. move).
     */
    public int requestMove();

    /**
     * Shows an error message.
     * @param errorMessage The given message.
     */
    public void showError(String errorMessage);

}
