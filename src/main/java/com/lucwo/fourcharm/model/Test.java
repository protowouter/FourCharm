/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.ai.NegaMaxStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.model.board.Board;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {

// ------------------ Instance variables ----------------

// --------------------- Constructors -------------------

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    public static void setup() {
        Logger.getGlobal().setLevel(Level.FINEST);


        ConsoleHandler cH = new ConsoleHandler();
        cH.setLevel(Level.FINEST);

        Logger.getGlobal().addHandler(cH);

    }

    public static void main(String[] args) {
        setup();
        Board board = new BinaryBoard();
        GameStrategy strat = new NegaMaxStrategy(1);
        try {
            board.makemove(3, Mark.P1);
            board.makemove(3, Mark.P1);
            board.makemove(3, Mark.P1);
        } catch (InvalidMoveException e) {
            e.printStackTrace();
        }
        strat.determineMove(new BinaryBoard(), Mark.P1);


    }

}
