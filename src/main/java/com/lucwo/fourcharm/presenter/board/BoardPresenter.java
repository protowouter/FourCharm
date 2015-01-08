/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.board;

import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.board.Board;
import com.lucwo.fourcharm.presenter.game.GamePresenter;
import com.lucwo.fourcharm.presenter.space.SpacePresenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class BoardPresenter implements Observer {


// ------------------ Instance variables ----------------


    @FXML
    private GridPane spacesPane;

    private SpacePresenter[][] spaces;

    private GamePresenter gamePresenter;


// --------------------- Constructors -------------------

// ----------------------- Queries ----------------------

    public GamePresenter getGamePresenter() {
        return gamePresenter;
    }

// ----------------------- Commands ---------------------

    public void setGamePresenter(GamePresenter newGamePresenter) {
        this.gamePresenter = newGamePresenter;
    }

    public void update(Observable o, Object arg) {

        if (o instanceof Game) {
            Platform.runLater(() -> drawBoard(((Game) o).getBoard()));

        }

    }

    public void initBoard(Board board) {

        spaces = new SpacePresenter[board.getColumns()][board.getRows()];

        spacesPane.getChildren().removeAll();

        ClassLoader classloader = getClass().getClassLoader();

        for (int col = 0; col < board.getColumns(); col++) {
            for (int row = 0; row < board.getRows(); row++) {
                FXMLLoader fxmlLoader = new FXMLLoader(classloader.getResource("views/space/show.fxml"));

                try {
                    fxmlLoader.load();
                    spacesPane.add(fxmlLoader.getRoot(), col, board.getRows() - row);
                    spaces[col][row] = fxmlLoader.getController();
                    spaces[col][row].setMark(board.getMark(col, row));
                    spaces[col][row].setBoardPresenter(this);
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
        for (SpacePresenter[] sAr : spaces) {
            for (SpacePresenter sContr : sAr) {
                sContr.disable();
            }
        }
    }

    public void enableSpaces() {
        for (SpacePresenter[] sAr : spaces) {
            for (SpacePresenter sContr : sAr) {
                sContr.enable();
            }
        }
    }
}
