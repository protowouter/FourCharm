/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.controller;

import com.lucwo.fourcharm.model.Mark;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Created by woutertimmermans on 12-12-14.
 */




public class SpaceController {

    // ------------------ Instance variables ----------------

    @FXML
    private Button spaceButton;
    private Mark mark;
    private BoardController boardController;

    private int col;

    // --------------------- Constructors -------------------

    // ----------------------- Queries ----------------------

    public BoardController getBoardController() {
        return boardController;
    }


    // ----------------------- Commands ---------------------

    public void setBoardController(BoardController boardController) {
        this.boardController = boardController;
    }

    public void setMark(Mark m) {
        mark = m;
        spaceButton.textProperty().setValue(m.toString());
    }

    @FXML
    protected void processClick() {

        boardController.disableSpaces();
        boardController.getGameController().doPlayerMove(col);

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
