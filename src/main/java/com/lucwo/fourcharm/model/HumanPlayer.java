/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

import com.lucwo.fourcharm.model.board.Board;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * The HumanPlayer class 'makes' a human player. To play a game of
 * Connect4 as a human, the Game class makes use of the interface
 * Player. The HumanPlayer class implements Player. The main priority
 * of this class is get the input of the human player to the board.
 *
 *
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class HumanPlayer implements Player {

    private final BufferedReader reader;
    private final Mark mark;

    /**
     * Create an new humanplayer given an way to communicate with the player.
     * 
     * @param inputReader
     *            Reader from which the human input can be parsed
     */
    public HumanPlayer(BufferedReader inputReader, Mark themark) {
        super();

        reader = inputReader;
        mark = themark;

    }

    /*
     * (non-Javadoc)
     * 
     * @see ft.model.Player#determineMove()
     */
    public int determineMove(Board board) {

        Logger.getGlobal().info("Going to parse playerinput");

        int move = 0;

        String line = "";
        try {
            line = reader.readLine();
        } catch (IOException e) {
            Logger.getGlobal().warning(e.toString());
            Logger.getGlobal().throwing("HumanPlayer", "determineMove", e);
        }
        Logger.getGlobal().info("Playerinput: " + line);
        if (line == null) {
            determineMove(board);
        } else {
            for (int i = 0; i < line.length(); i++) {
                int col = line.charAt(i) - '1';
                if ((col >= 0) && (col < board.getColumns())
                        && board.columnHasFreeSpace(col)) {
                    move = col;
                } else {
                    move = determineMove(board);
                }
            }
        }
        return move;
    }

    public Mark getMark() {
        return mark;
    }

}
