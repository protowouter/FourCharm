/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.controller;

import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.board.Board;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;


/**
 * Created by woutertimmermans on 12-12-14.
 */


public class BoardController implements Observer {


// ------------------ Instance variables ----------------


    @FXML
    private FlowPane spacesPane;


// --------------------- Constructors -------------------

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------


    public void update(Observable o, Object arg) {

        if (o instanceof Game) {
            Platform.runLater(() -> drawBoard(((Game) o).getBoard()));

        }

    }

    private void drawBoard(Board board) {


        spacesPane.getChildren().removeAll();
        ClassLoader classloader = getClass().getClassLoader();

        for (int i = 0; i < board.getSpotCount(); i++) {

            FXMLLoader fxmlLoader = new FXMLLoader(classloader.getResource("views/space.fxml"));

            try {
                fxmlLoader.load();
                spacesPane.getChildren().add(fxmlLoader.getRoot());
                ((SpaceController) fxmlLoader.getController()).setMark(board.getMark(i));
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }

        }

    }
}
