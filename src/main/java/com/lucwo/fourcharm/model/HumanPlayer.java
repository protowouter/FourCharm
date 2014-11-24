/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

import com.lucwo.fourcharm.model.board.Board;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class HumanPlayer implements Player {

    private BufferedReader reader;

    /**
     * Create an new humanplayer given an way to communicate with the player.
     * 
     * @param inputReader
     *            Reader from which the human input can be parsed
     */
    public HumanPlayer(BufferedReader inputReader) {

        reader = inputReader;

    }

    /*
     * (non-Javadoc)
     * 
     * @see ft.model.Player#doMove()
     */
    public int doMove(Board board) {

        Logger.getGlobal().info("Voer kolomnummer in: ");

        int move = 0;

        String line = "";
        try {
            line = reader.readLine();
        } catch (IOException e) {
            Logger.getGlobal().warning(e.toString());
            Logger.getGlobal().throwing("HumanPlayer", "doMove", e);
        }
        if (line == null) {
            doMove(board);
        }
        for (int i = 0; i < line.length(); i++) {
            int col = line.charAt(i) - '1';
            if (col >= 0 && col < board.getColumns()
                    && board.columnHasFreeSpace(col)) {
                move = col;
            } else {
                move = doMove(board);
            }
        }
        return move;
    }

}
