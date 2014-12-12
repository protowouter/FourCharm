/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by woutertimmermans on 27-11-14.
 */


public class FourCharm extends Application {

    // ------------------ Instance variables ----------------

    // --------------------- Constructors -------------------

    // ----------------------- Queries ----------------------


    // ----------------------- Commands ---------------------

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {

        Logger.getGlobal().setLevel(Level.FINEST);


        ConsoleHandler cH = new ConsoleHandler();
        cH.setLevel(Level.FINEST);

        Logger.getGlobal().addHandler(cH);

        ClassLoader classloader = getClass().getClassLoader();
        FXMLLoader fxmlLoader = new FXMLLoader(classloader.getResource("views/game.fxml"));

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }


        Pane root = fxmlLoader.getRoot();
        GameController fC = fxmlLoader.getController();


        stage.setScene(new Scene(root));
        stage.setTitle("FourCharm");
        stage.show();

        fC.startGame();
    }
}




