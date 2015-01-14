/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import com.lucwo.fourcharm.presenter.FourCharmFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FourCharmGUI extends Application {

    // ---------------- Instance Variables ------------------

    private FourCharmFactory fourCharmFactory;


    // --------------------- Constructors -------------------

    public FourCharmGUI() {


        fourCharmFactory = new FourCharmFactory();


    }


    // ----------------------- Commands ---------------------

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {

        Logger.getGlobal().setLevel(Level.FINEST);


        ConsoleHandler cH = new ConsoleHandler();
        cH.setLevel(Level.FINEST);

        Logger.getGlobal().addHandler(cH);

        Pane root = (Pane) fourCharmFactory.getFourCharmPresenter().getView();

        stage.setScene(new Scene(root, 1000, 1000));
        stage.setTitle("FourCharmGUI");
        stage.show();
    }
}




