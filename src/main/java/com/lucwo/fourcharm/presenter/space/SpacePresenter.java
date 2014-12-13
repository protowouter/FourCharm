/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.space;

import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.presenter.board.BoardPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SpacePresenter {

    // ------------------ Instance variables ----------------

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

    public void setBoardPresenter(BoardPresenter boardPresenter) {
        this.boardPresenter = boardPresenter;
    }

    public void setMark(Mark m) {
        mark = m;
        spaceButton.textProperty().setValue(m.toString());
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

    public void setCol(int column) {
        col = column;
    }

    public void initialize() {
        disable();
    }
}
