/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.space;

import com.lucwo.fourcharm.model.player.Mark;
import com.lucwo.fourcharm.presenter.board.BoardPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


/**
 * TODO spacepresenter javadoc.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */

public class SpacePresenter {

    // ------------------ Instance variables ----------------

    private static final double PIECE_RADIUS = 100;


    private static final Border HIGHLIGHT_BORDER = new Border(new BorderStroke(Paint.valueOf("Blue"), BorderStrokeStyle.SOLID, new CornerRadii(0,0,0,0, true),new BorderWidths(1)));

    @FXML
    private Button spaceButton;
    private Mark mark;
    private BoardPresenter boardPresenter;

    private int col;

    // --------------------- Constructors -------------------

    // ----------------------- Queries ----------------------

    public BoardPresenter getBoardPresenter() {
        return boardPresenter;
    }


    // ----------------------- Commands ---------------------

    public void setBoardPresenter(BoardPresenter newBoardPresenter) {
        boardPresenter = newBoardPresenter;
    }

    public void setMark(Mark m) {
        spaceButton.setBorder(Border.EMPTY);
        mark = m;

        if (m == Mark.P1) {
            spaceButton.textProperty().setValue("");
            spaceButton.setBackground(new Background(new BackgroundFill(Color.PINK,
                    new CornerRadii(PIECE_RADIUS), null)));
        } else if (m == Mark.P2) {
            spaceButton.textProperty().setValue("");
            spaceButton.setBackground(new Background(new BackgroundFill(Color.SKYBLUE,
                    new CornerRadii(PIECE_RADIUS), null)));
        } else {
            spaceButton.textProperty().setValue(m.toString());
        }



    }

    @FXML
    protected void processClick() {

        boardPresenter.disableSpaces();
        boardPresenter.getGamePresenter().doPlayerMove(col);

    }

    public void disable() {
        spaceButton.disableProperty().setValue(true);
    }

    public void enable() {
        if (mark == Mark.EMPTY) {
            spaceButton.disableProperty().set(false);
        }

    }

    public void highlight() {
        spaceButton.setBorder(HIGHLIGHT_BORDER);
    }

    public void setCol(int column) {
        col = column;
    }

    public void initialize() {
        disable();
    }
}
