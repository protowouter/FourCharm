/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.ai;

import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.board.Board;

/**
 * An strategy used by an computerplayer.
 * 
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public interface GameStrategy {

    public int determineMove(Board board, Mark mark);

}
