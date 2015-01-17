/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

import com.lucwo.fourcharm.exception.InvalidMoveException;
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

    public static void main(String[] args) throws InvalidMoveException {
        setup();
        Board board = new BinaryBoard();
        board.makemove(0, Mark.P1);
        NegaMaxStrategy strat = new NegaMaxStrategy(0);
        for (int i = 0; i < board.getColumns(); i++) {
            try {
                board = new BinaryBoard();
                board.makemove(i, Mark.P1);
                System.out.println("Column: " + i + " Value: " + strat.nodeValue(board));
            } catch (InvalidMoveException e) {

            }
        }

        //strat.determineMove(board, Mark.P1);


    }

}
