/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.space;

import com.lucwo.fourcharm.model.player.Mark;
import com.lucwo.fourcharm.presenter.board.BoardPresenter;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;


/**
 * TODO spacepresenter javadoc.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */

public class SpacePresenter {

    // ------------------ Instance variables ----------------

    @FXML
    private VBox parent;
    @FXML
    private Circle space;
    private Mark mark;
    private BoardPresenter boardPresenter;

    private int col;

    private Paint current;

    // --------------------- Constructors -------------------

    // ----------------------- Queries ----------------------

    public BoardPresenter getBoardPresenter() {
        return boardPresenter;
    }


    // ----------------------- Commands ---------------------

    public void setBoardPresenter(BoardPresenter newBoardPresenter) {
        boardPresenter = newBoardPresenter;
        space.radiusProperty().bind(boardPresenter.getGamePresenter().getFourCharmPresenter().getStage().widthProperty().divide(20));
    }

    public void setMark(Mark m) {
        System.out.println(boardPresenter.getGamePresenter().getFourCharmPresenter());
        unHighlight();
        mark = m;

        if (m == Mark.P1) {
            space.setFill(Color.PINK);
        } else if (m == Mark.P2) {
            space.setFill(Color.SKYBLUE);
        } else {
            space.setFill(Color.WHITE);
        }



    }

    @FXML
    protected void processClick() {

        boardPresenter.disableSpaces();
        boardPresenter.getGamePresenter().doPlayerMove(col);

    }

    public void disable() {
        space.disableProperty().setValue(true);
    }

    public void enable() {
        if (mark == Mark.EMPTY) {
            space.disableProperty().set(false);
        }

    }

    public void highlight() {
        current = space.getFill();
        space.setStroke(Color.CORNFLOWERBLUE);
        space.setFill(Color.BEIGE);
    }

    public void unHighlight() {
        space.setFill(current);
        space.setStroke(Color.BLACK);
    }

    public void setCol(int column) {
        col = column;
    }

    public void initialize() {
        disable();
    }
}
