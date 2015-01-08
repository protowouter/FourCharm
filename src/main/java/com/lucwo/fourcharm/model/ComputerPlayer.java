/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.board.Board;

/**
 * Create an AI player given an strategy.
 * 
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class ComputerPlayer implements Player {

    private final GameStrategy strategy;

    private Mark mark;

    /**
     * Create an new ComputerPlayer using an strategy to do moves on the board.
     * 
     * @param computerStrategy
     *            strategy to be used by this player
     */
    public ComputerPlayer(GameStrategy computerStrategy, Mark theMark) {

        strategy = computerStrategy;
        mark = theMark;


    }

    public int determineMove(Board board) {
        return strategy.determineMove(board, mark);
    }

    public Mark getMark() {
        return mark;
    }
}
