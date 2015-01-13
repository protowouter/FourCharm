/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

import com.lucwo.fourcharm.model.board.Board;

/**
 * The ASyncPlayer class 'makes' a human player. To play a game of
 * Connect4 as a human, the Game class makes use of the interface
 * Player. The ASyncPlayer class implements Player. The main priority
 * of this class is get the input of the human player to the board.
 *
 *
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class ASyncPlayer implements Player {

    private final Mark mark;
    private MoveRequestable moveRequester;
    private String name;

    /**
     * Create an new humanplayer given an way to communicate with the player.
     * 
     * @param inputReader
     *            Reader from which the human input can be parsed
     */
    public ASyncPlayer(String namePie, MoveRequestable moveReq, Mark themark) {
        super();
        mark = themark;
        moveRequester = moveReq;
        name = namePie;

    }

    /*
     * (non-Javadoc)
     * 
     * @see ft.model.Player#determineMove()
     */
    public int determineMove(Board board) {


        return moveRequester.requestMove();

    }

    public Mark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getMark() + ": " + getClass().getSimpleName() + " " + getName();
    }

}
