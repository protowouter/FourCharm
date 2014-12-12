/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.controller;

import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.board.Board;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;


/**
 * Created by woutertimmermans on 12-12-14.
 */


public class BoardController implements Observer {


// ------------------ Instance variables ----------------


    @FXML
    private GridPane spacesPane;

    private SpaceController[][] spaces;

    private GameController gameController;


// --------------------- Constructors -------------------

// ----------------------- Queries ----------------------

    public GameController getGameController() {
        return gameController;
    }

// ----------------------- Commands ---------------------

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public void update(Observable o, Object arg) {

        if (o instanceof Game) {
            Platform.runLater(() -> drawBoard(((Game) o).getBoard()));

        }

    }

    public void initBoard(Board board) {

        spaces = new SpaceController[board.getColumns()][board.getRows()];

        spacesPane.getChildren().removeAll();

        ClassLoader classloader = getClass().getClassLoader();

        for (int col = 0; col < board.getColumns(); col++) {
            for (int row = 0; row < board.getRows(); row++) {
                FXMLLoader fxmlLoader = new FXMLLoader(classloader.getResource("views/space.fxml"));

                try {
                    fxmlLoader.load();
                    spacesPane.add(fxmlLoader.getRoot(), col, board.getRows() - row);
                    spaces[col][row] = fxmlLoader.getController();
                    spaces[col][row].setMark(board.getMark(col, row));
                    spaces[col][row].setBoardController(this);
                    spaces[col][row].setCol(col + 1);
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }
        }

    }

    private void drawBoard(Board board) {

        for (int col = 0; col < board.getColumns(); col++) {
            for (int row = 0; row < board.getRows(); row++) {
                spaces[col][row].setMark(board.getMark(col, row));
            }
        }


    }

    public void disableSpaces() {
        for (SpaceController[] sAr : spaces) {
            for (SpaceController sContr : sAr) {
                sContr.disable();
            }
        }
    }

    public void enableSpaces() {
        for (SpaceController[] sAr : spaces) {
            for (SpaceController sContr : sAr) {
                sContr.enable();
            }
        }
    }
}
