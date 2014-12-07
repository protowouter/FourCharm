/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.ui.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

        Logger.getGlobal().fine("hallo");

        FourCharmController fC = new FourCharmController();

        fC.setBoardText("Hallo");

        stage.setScene(new Scene(fC));
        stage.setTitle("FourCharm");
        stage.setWidth(300);
        stage.setHeight(200);
        stage.show();

        fC.startGame();
    }
}




