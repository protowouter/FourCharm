/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.junit;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.board.Board;
import com.lucwo.fourcharm.model.board.ReferenceBoard;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class BoardTest {

    private Board board;

    // TODO make this an parameterized test
    // http://stackoverflow.com/questions/16237135/junit-writing-a-single-unit-test-for-multiple-implementations-of-an-interface

    @Before
    public void setUp() throws Exception {

        board = new ReferenceBoard();

    }

    @Test
    public void emptyColumnShouldReportEmpty() throws Exception {

        int col = board.getColumns() - 1;

        assert (board.columnHasFreeSpace(col));

    }

    private void fillColumn(int col) throws Exception {

        int rows = board.getRows();

        for (int i = 0; i < rows; i++) {
            board.makemove(col);
        }

    }

    private void fillBoard() throws Exception {

        for (int i = 0; i < board.getColumns(); i++) {
            fillColumn(i);
        }

    }

    @Test
    public void fullColumnShouldReportFull() throws Exception {

        int col = board.getColumns() - 1;

        fillColumn(col);

        assertFalse(board.columnHasFreeSpace(col));


    }

    @Test(expected = InvalidMoveException.class)
    public void fullColumnCannotBeFilled() throws Exception {

        int col = board.getColumns() - 1;
        fillColumn(col);

        board.makemove(col);


    }

    @Test
    public void testLastMoveWon() throws Exception {

        //board.getWinStreak()

    }

    @Test
    public void fullBoardShouldReportFull() throws Exception {

        fillBoard();
        assert (board.isFull());

    }

    @Test(expected = InvalidMoveException.class)
    public void fullBoardCannotBeFilled() throws Exception {

        fillBoard();
        board.makemove(board.getColumns() - 1);

    }



    @Test
    public void testGetPlieCount() throws Exception {

        int moves = board.getColumns() - 1;

        assert board.getPlieCount() == 0;

        for (int i = 0; i < moves; i++) {
            board.makemove(i);
        }

        assert board.getPlieCount() == moves;

    }

    @Test
    public void testGetColumns() throws Exception {

        assert (board.getColumns() > 0);

    }

    @Test
    public void testGetRows() throws Exception {

        assert (board.getRows() > 0);

    }

    @Test
    public void testGetPlayers() throws Exception {

        assert board.getPlayers() > 0;

    }

    @Test
    public void testGetWinStreak() throws Exception {

        assert board.getWinStreak() <= board.getRows();
        assert board.getWinStreak() <= board.getColumns();

    }

    @Test
    public void testGetSpotCount() throws Exception {

        int spots = board.getColumns() * board.getRows();

        assert board.getSpotCount() == spots;

    }

    @Test
    public void testDeepCopy() throws Exception {

        assertFalse(board == board.deepCopy());

    }
}