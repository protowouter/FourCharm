/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

/**
 * The Mark enums responsibility is to switch the current player
 * and the next player by using the method 'other()'. This way
 * the first player is allowed to go first, and the other player
 * will go second. It doesn't matter which player is player 1. The
 * other player will always be player 2.
 *
 *@author Luce Sandfort and Wouter Timmermans
 */

public enum Mark {

    P1, P2, EMPTY;

// ------------------ Instance variables ----------------


// --------------------- Constructors -------------------


// ----------------------- Queries ----------------------

    /**
     * Gives the other mark.
     * @return The other mark.
     */
    public Mark other() {

        Mark other = EMPTY;

        if (this == P1) {
            other = P2;
        } else if (this == P2) {
            other = P1;
        }

        return other;

    }

// ----------------------- Commands ---------------------
}
