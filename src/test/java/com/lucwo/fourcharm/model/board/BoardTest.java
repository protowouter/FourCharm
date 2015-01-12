/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
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
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(value = Parameterized.class)
public class BoardTest {

    private Board board;
    private Class boardClass;

    public BoardTest(Class bC) {

        boardClass = bC;

    }

    @Parameterized.Parameters
    public static Collection boardClasses() {

        Collection<Class[]> retCol = new ArrayList<>();
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
        assertTrue(board.columnHasFreeSpace(col));
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

        int rows = board.getRows();
        int columns = board.getColumns();

        List<Integer> wisselLijst = new LinkedList<>();
        List<Integer> nietWisselLijst = new LinkedList<>();

        for (int i = 1; i < rows ; i = i + 4) {
            wisselLijst.add(i);
            wisselLijst.add(i + 1);
        }

        for (int i = 3; i < (rows * 2); i = i + 4) {
            nietWisselLijst.add(i);
            nietWisselLijst.add(i + 1);
        }

        if (wisselLijst.contains(rows)) {
            Mark mark = Mark.P1;
            for (int col = 0; col < columns; col++) {
                int i;
                //Verhoog met 2 aangezien hij steeds 2 moves maakt.
                for (i = 0; i < rows; i = i + 2) {
                    board.makemove(col, mark);
                    board.makemove(col, mark);
                    mark = mark.other();
                }
            }
        }

        if (nietWisselLijst.contains(rows)) {
            Mark mark = Mark.P1;
            for (int col = 0; col < columns; col++) {
                int i;
                for (i = 0; i < rows; i = i + 2) {
                    mark = mark.other();
                    board.makemove(col, mark);
                    board.makemove(col, mark);
                }
            }
        }
    }

    @Test
    public void fullColumnShouldReportFull() throws Exception {
        int col = board.getColumns() - 1;
        fillColumn(col);
        assertFalse(board.columnHasFreeSpace(col));
    }

    @Test
    public void almostFullColumnShouldReportEmpty() throws Exception {
        int rows = board.getRows();
        int col = board.getColumns() - 1;

        Mark mark = Mark.P1;
        for (int i = 0; i < rows - 1; i++) {
            board.makemove(col, mark);
            mark = mark.other();
        }
        assertTrue(board.columnHasFreeSpace(col));
        board.makemove(col, mark);
        assertFalse(board.columnHasFreeSpace(col));
    }

    @Test(expected = InvalidMoveException.class)
    public void fullColumnCannotBeFilled() throws Exception {

        int col = board.getColumns() - 1;
        fillColumn(col);
        board.makemove(col, Mark.P1);
    }

    @Test (expected = InvalidMoveException.class)
    public void columnThatDoesntExistCannotBeFilled() throws Exception {
        int col = board.getColumns();
        board.makemove(col, Mark.P1);

    }

    @Test
    public void firstColumnIsEmptyCanBeFilled() throws Exception {
        int col = 0;
        assertTrue(board.columnHasFreeSpace(col));
        board.makemove(col,Mark.P1);
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
            assertTrue(board.hasWon(mark));
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

        assertTrue(board.hasWon(winner));
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

        assertTrue(board.hasWon(winner));
    }

    @Test
    public void fullBoardShouldReportFull() throws Exception {

        fillBoard();
        assertTrue(board.isFull());
    }

    @Test(expected = InvalidMoveException.class)
    public void fullBoardCannotBeFilled() throws Exception {
        fillBoard();
        board.makemove(board.getColumns() - 1, Mark.P1);
    }

    @Test
    public void emptyBoardCanBeFilled() throws Exception {

        assertFalse(board.isFull());
    }

    @Test
    public void testGetPlieCount() throws Exception {

        int moves = board.getColumns() - 1;
        assert board.getPlieCount() == 0;
        Mark mark = Mark.P1;

        for (int i = 0; i < moves; i++, mark = mark.other()) {
            board.makemove(i, mark);
        }
        assertTrue(board.getPlieCount() == moves);
    }

    @Test
    public void testGetColumns() throws Exception {

        assertTrue(board.getColumns() > 0);
        assertTrue(board.getColumns() >= board.getWinStreak());
    }

    @Test
    public void testGetRows() throws Exception {
        assertTrue(board.getRows() > 0);

        assertTrue(board.getRows() >= board.getWinStreak());
    }

    @Test
    public void testGetWinStreak() throws Exception {

        assertTrue(board.getWinStreak() <= board.getRows());
        assertTrue(board.getWinStreak() <= board.getColumns());
        assertTrue(board.getWinStreak() >= 3);

    }

    @Test
    public void testGetSpotCount() throws Exception {

        int spots = board.getColumns() * board.getRows();

        assertTrue(board.getSpotCount() == spots);
        assertTrue(board.getSpotCount() > 0);

    }

    @Test
    public void testDeepCopy() throws Exception {

        assertFalse(board == board.deepCopy());
        assertTrue(board.equals(board.deepCopy()));

    }

    @Test
    public void testGetMark() throws Exception {
        for (int i = 0; i < board.getRows(); i++) {
            assertTrue(board.getMark(i) == Mark.EMPTY);
            board.makemove(i, Mark.P1);
            assertTrue("board.getMark(" + i + ",0) == Mark.P1", board.getMark(i, 0) == Mark.P1);
            assertTrue("board.getMark(" + i * board.getRows() + ") == Mark.P1", board.getMark(i * board.getRows()) == Mark.P1);
            setUp();
        }
    }

    @Test
    public void testGetOtherMark() throws Exception {
        for (int i = 0; i < board.getRows(); i++) {
            assertTrue(board.getMark(i) == Mark.EMPTY);
            board.makemove(i, Mark.P2);
            assertTrue("board.getMark(" + i + ",0) == Mark.P2", board.getMark(i, 0) == Mark.P2);
            assertTrue("board.getMark(" + i * board.getRows() + ") == Mark.P2", board.getMark(i * board.getRows()) == Mark.P2);
            setUp();
        }
    }

    @Test
    public void testMakeValidMove() throws Exception {
        int col = board.getColumns()- 1;
        assertTrue(board.columnHasFreeSpace(col));
        board.makemove(col, Mark.P1);
    }

    @Test
    public void testPositionCodeDiffMove() throws Exception{
        int col = board.getColumns() - 1;
        board.makemove(col, Mark.P1);
        long positie1 = board.positionCode();
        board.makemove(0, Mark.P1);
        long positie2 = board.positionCode();
        assertFalse(positie1 == positie2);
    }

    @Test
    public void testPositionCodeSameMove() throws Exception {
        int col = board.getColumns() - 1;
        board.makemove(col, Mark.P1);
        long positie1 = board.positionCode();
        setUp();

        board.makemove(col, Mark.P1);
        long positie2 = board.positionCode();
        assertTrue(positie1 == positie2);
    }


    @Test
    public void testToString() throws Exception {
        String before = board.toString();
        assertFalse(before.equals(""));

        board.makemove(0, Mark.P1);
        assertFalse(before.equals(board.toString()));

    }
}