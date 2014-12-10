/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.ui.gui;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.*;
import com.lucwo.fourcharm.model.ai.NegaMaxStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

/**
 * Created by woutertimmermans on 26-11-14.
 */


public class FourCharmController extends VBox implements Observer {

    // ------------------ Instance variables ----------------

    PipedWriter playerInput;
    @FXML
    private TextArea boardTextArea;
    @FXML
    private TextField playerMove;
    private Game game;
    private Thread gameThread;
    private Player p1;
    private Player p2;


    // --------------------- Constructors -------------------


    public FourCharmController() {
        ClassLoader classloader = getClass().getClassLoader();
        FXMLLoader fxmlLoader = new FXMLLoader(classloader.getResource("views/fourcharmgui.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        playerInput = new PipedWriter();
        BufferedReader playerReader = new BufferedReader(new PipedReader());

        try {
            playerReader = new BufferedReader(new PipedReader(playerInput));
        } catch (IOException e) {
            Logger.getGlobal().throwing("FourCharmController", "Constructor", e);
        }

        p1 = new HumanPlayer(playerReader, Mark.P1);
        p2 = new ComputerPlayer(new NegaMaxStrategy(), Mark.P2);


        try {
            game = new Game(BinaryBoard.class, p2, p1);
        } catch (InstantiationException | IllegalAccessException e) {
            Logger.getGlobal().throwing("FourCharmController", "Constructor", e);
        }

        game.addObserver(this);

        gameThread = new Thread(game);
    }


    // ----------------------- Queries ----------------------

    public StringProperty boardTextProperty() {
        return boardTextArea.textProperty();
    }

    // ----------------------- Commands ---------------------

    public String getBoardText() {
        return boardTextProperty().get();
    }

    public void setBoardText(String value) {
        boardTextProperty().set(value);
    }

    public void update(Observable o, Object arg) {

        if (o instanceof Game) {
            Platform.runLater(() ->
                            setBoardText(((Game) o).getBoard().toString())
            );

        }

    }

    public void startGame() throws InvalidMoveException {
        update(game, null);
        gameThread.start();
    }

    @FXML
    protected void parsePlayerMove() {
        Logger.getGlobal().info("User clicked move");
        try {
            playerInput.write(playerMove.getText() + "\n");
            playerInput.flush();
            playerMove.clear();
        } catch (IOException e) {
            Logger.getGlobal().throwing("FourCharmController", "parsePlayerMove", e);
        }

    }

}
