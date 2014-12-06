/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

/**
 * Created by woutertimmermans on 05-12-14.
 */

public enum Mark {

    P1, P2, EMPTY;

// ------------------ Instance variables ----------------


// --------------------- Constructors -------------------


// ----------------------- Queries ----------------------

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
