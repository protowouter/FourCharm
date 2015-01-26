/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.board;

import com.lucwo.fourcharm.model.board.Board;
import com.lucwo.fourcharm.presenter.game.GamePresenter;
import com.lucwo.fourcharm.presenter.space.SpacePresenter;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * TODO: boardpresenter javadoc.
 */

public class BoardPresenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoardPresenter.class);


// ------------------ Instance variables ----------------


    @FXML
    private VBox spacesPane;

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

    public ReadOnlyDoubleProperty widthProperty() {
        return spacesPane.widthProperty();
    }

    public ReadOnlyDoubleProperty heigthProperty() {
        return spacesPane.heightProperty();
    }

    public void initBoard(Board board) {
        VBox.setVgrow(spacesPane, Priority.ALWAYS);

        spaces = new SpacePresenter[board.getColumns()][board.getRows()];

        spacesPane.getChildren().removeAll();

        ClassLoader classloader = getClass().getClassLoader();

        for (int row = board.getRows() - 1; row >= 0; row--) {
            HBox rowBox = new HBox();
            HBox.setHgrow(rowBox, Priority.ALWAYS);
            for (int col = 0; col < board.getColumns(); col++) {
                FXMLLoader fxmlLoader =
                        new FXMLLoader(classloader.getResource("views/space/show.fxml"));

                try {
                    fxmlLoader.load();
                    rowBox.getChildren().add(fxmlLoader.getRoot());
                    spaces[col][row] = fxmlLoader.getController();
                    spaces[col][row].setBoardPresenter(this);
                    spaces[col][row].setMark(board.getMark(col, row));
                    spaces[col][row].setCol(col);
                } catch (IOException exception) {
                    LOGGER.trace("initBoard", exception);
                }
            }
            spacesPane.getChildren().add(rowBox);
        }

    }

    public void drawBoard(Board board) {

        for (int col = 0; col < board.getColumns(); col++) {
            for (int row = 0; row < board.getRows(); row++) {
                spaces[col][row].setMark(board.getMark(col, row));
            }
        }


    }


    /**
     * Highlight a column to indicate a hint.
     * @param col Value between zero and the amount of columns of the board.
     */
    public void highlightColumn(int col) {
        for (SpacePresenter space : spaces[col]) {
            space.highlight();
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
