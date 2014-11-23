/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package main.java.model.ai;

import java.util.Random;
import java.util.logging.Logger;

import main.java.model.board.Board;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class RandomStrategy implements GameStrategy {

    public static final Random R_GENERATOR = new Random();

    /*
     * (non-Javadoc)
     * 
     * @see ft.model.GameStrategy#doMove()
     */
    public int doMove(Board board) {

        int col = R_GENERATOR.nextInt(Board.COLUMNS);

        while (!board.columnHasFreeSpace(col)) {
            col = R_GENERATOR.nextInt(Board.COLUMNS);
        }

        Logger.getGlobal().info("Found random move: " + col);
        return col;

    }

}
