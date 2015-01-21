/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.board.Board;

/**
 * Create an Artificial Intelligence (AI) player given a strategy. This class
 * is responsible for making a (smart) computer player. The LocalAIPlayer
 * class makes use of the GameStrategy Interface and the classes belonging
 * to this interface (the MDTDfStrategy class, the NegaMaxStrategy class and
 * the RandomStrategy class).
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class LocalAIPlayer implements Player {

    private final GameStrategy strategy;
    private Mark mark;

    /**
     * Create an new LocalAIPlayer using a strategy to do moves on the board.
     * 
     * @param computerStrategy
     *            strategy to be used by this player
     */
    public LocalAIPlayer(GameStrategy computerStrategy, Mark theMark) {

        strategy = computerStrategy;
        mark = theMark;


    }

    @Override
    public int determineMove(Board board) {
        return strategy.determineMove(board, mark);
    }

    @Override
    public String getName() {
        return strategy.getClass().getSimpleName();
    }

    @Override
    public Mark getMark() {
        return mark;
    }

    @Override
    public String toString() {
        return getMark() + ": " + getClass().getSimpleName() + " " + getName();
    }
}
