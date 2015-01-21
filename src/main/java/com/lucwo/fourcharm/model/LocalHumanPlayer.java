/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

/**
 * LocalHumanPlayer extends AsyncPlayer and is used to distinguish AI and Human players.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */

public class LocalHumanPlayer extends ASyncPlayer {

// --------------------- Constructors -------------------

    public LocalHumanPlayer(String namePie, Mark themark) {
        super(namePie, themark);
    }

}
