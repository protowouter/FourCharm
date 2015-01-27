/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.space;

import com.lucwo.fourcharm.model.player.Mark;
import com.lucwo.fourcharm.presenter.board.BoardPresenter;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
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
    }

    public void setMark(Mark m) {
        unHighlight();
        mark = m;

        if (m == Mark.P1) {
            space.getStyleClass().removeAll("p2", "empty");
            space.getStyleClass().add("p1");
        } else if (m == Mark.P2) {
            space.getStyleClass().removeAll("p1", "empty");
            space.getStyleClass().add("p2");
        } else {
            space.getStyleClass().removeAll("p1", "p2");
            space.getStyleClass().add("empty");
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
        space.getStyleClass().add("highlight");
    }

    public void unHighlight() {
        space.getStyleClass().remove("highlight");
    }

    public void setCol(int column) {
        col = column;
    }

    public void setRadius(double radius) {
        space.radiusProperty().setValue(radius);
    }

    public void initialize() {
        disable();
    }
}
