/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

public interface MoveRequestable {

    /**
     * Asks for a move and puts this move in the queue.
     *
     * @return the requested move
     */
    public int requestMove();
}
