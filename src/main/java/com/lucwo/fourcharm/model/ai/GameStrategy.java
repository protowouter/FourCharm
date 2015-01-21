/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.ai;

import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.board.Board;

/**
 * The strategy used by a computer player. This class makes use of the Mark class
 * and the Board classes to achieve its responsibilities. The game strategy can
 * be used by the hint function to return the most optimal move to the human player.
 *
 * @author Luce Sandfort and Wouter Timmermans.
 */
public interface GameStrategy {

    public int determineMove(Board board, Mark mark);

}
