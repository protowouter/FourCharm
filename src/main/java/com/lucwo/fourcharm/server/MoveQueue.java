/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import com.lucwo.fourcharm.model.MoveRequestable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class MoveQueue implements MoveRequestable {

    // ---------------- Instantie variabelen ------------------

    private BlockingQueue<Integer> rij;

    // ---------------- Constructor ---------------------------

    public MoveQueue() {
        rij = new LinkedBlockingQueue<Integer>(1);
    }

    // ---------------- Queries -------------------------------

    // ---------------- Commands ------------------------------

    /**
     * Asks for a move and puts this move in the queue.
     *
     * @return the requested move
     */
    @Override
    public int requestMove() {

        int column = -1;
        try {
            column = rij.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return column;
    }

    public BlockingQueue<Integer> getQueue() {
        return rij;
    }
}