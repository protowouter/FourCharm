/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.board;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.Mark;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertFalse;

@RunWith(value = Parameterized.class)
public class BoardTest {

    private Board board;

    private Class boardClass;


    public BoardTest(Class bC) {

        boardClass = bC;

    }

    @Parameterized.Parameters
    public static Collection boardClasses() {

        Collection retCol = new ArrayList();
        retCol.add(new Class[]{ReferenceBoard.class});
        retCol.add(new Class[]{BinaryBoard.class});

        return retCol;

    }

    @Before
    public void setUp() throws Exception {

        board = (Board) boardClass.newInstance();

    }

    @Test
    public void emptyColumnShouldReportEmpty() throws Exception {

        int col = board.getColumns() - 1;

        assert (board.columnHasFreeSpace(col));

    }

    private void fillColumn(int col) throws Exception {

        int rows = board.getRows();

        Mark mark = Mark.P1;
        for (int i = 0; i < rows; i++) {
            board.makemove(col, mark);
            mark = mark.other();
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

        board.makemove(col, Mark.P1);


    }

    @Test
    public void testLastMoveWon() throws Exception {

        int winstreak = board.getWinStreak();
        int columns = board.getColumns();
        int p1 = 0;
        int p2 = columns - 1;

        if (p1 == p2) {
            //Unwinnable board
        } else {
            Mark mark = Mark.P1;
            for (int i = 0; i < (winstreak - 1); i++) {
                board.makemove(p1, mark);
                board.makemove(p2, mark.other());

            }

            assertFalse(board.hasWon(mark));
            board.makemove(p1, mark);
            assert (board.hasWon(mark));
        }




    }

    @Test
    public void testLeftdownRightUpDiagonal() throws InvalidMoveException {
        Mark winner = Mark.P1;
        Mark loser = winner.other();
        board.makemove(0, winner);
        board.makemove(1, loser);
        board.makemove(1, winner);
        board.makemove(2, loser);
        board.makemove(3, winner);
        board.makemove(2, loser);
        board.makemove(2, winner);
        board.makemove(3, loser);
        board.makemove(4, winner);
        board.makemove(3, loser);
        board.makemove(3, winner);

        assert board.hasWon(winner);
    }

    @Test
    public void testRightdownLeftUpDiagonal() throws InvalidMoveException {
        Mark winner = Mark.P1;
        Mark loser = winner.other();
        board.makemove(6, winner);
        board.makemove(5, loser);
        board.makemove(5, winner);
        board.makemove(4, loser);
        board.makemove(3, winner);
        board.makemove(4, loser);
        board.makemove(4, winner);
        board.makemove(3, loser);
        board.makemove(2, winner);
        board.makemove(3, loser);
        board.makemove(3, winner);

        assert board.hasWon(winner);
    }

    @Test
    public void fullBoardShouldReportFull() throws Exception {

        fillBoard();
        assert (board.isFull());

    }

    @Test(expected = InvalidMoveException.class)
    public void fullBoardCannotBeFilled() throws Exception {

        fillBoard();
        board.makemove(board.getColumns() - 1, Mark.P1);

    }



    @Test
    public void testGetPlieCount() throws Exception {

        int moves = board.getColumns() - 1;

        assert board.getPlieCount() == 0;

        Mark mark = Mark.P1;

        for (int i = 0; i < moves; i++, mark = mark.other()) {
            board.makemove(i, mark);
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

    @Test
    public void testToString() throws Exception {
        String before = board.toString();
        assertFalse(before.equals(""));

        board.makemove(0, Mark.P1);
        assertFalse(before.equals(board.toString()));

    }
}