/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import com.lucwo.fourcharm.FourCharmController;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.presenter.FourCharmFactory;
import com.lucwo.fourcharm.presenter.FourCharmPresenter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FourCharmGUI extends Application implements FourCharmView {

    // ---------------- Instance Variables ------------------

    private static FourCharmFactory fourCharmFactory;
    private static FourCharmController controller;
    private FourCharmPresenter fourCharmPresenter;


    // --------------------- Constructors -------------------

    public FourCharmGUI(FourCharmController contr) {
        fourCharmFactory = new FourCharmFactory();
        controller = contr;
        new Thread(() -> Application.launch(FourCharmGUI.class)).start();
    }

    public FourCharmGUI() {

    }


    // ----------------------- Commands ---------------------

    public void start(Stage stage) throws Exception {

        Logger.getGlobal().setLevel(Level.FINEST);


        ConsoleHandler cH = new ConsoleHandler();
        cH.setLevel(Level.FINEST);

        Logger.getGlobal().addHandler(cH);

        fourCharmPresenter = fourCharmFactory.getFourCharmPresenter(controller);

        Pane root = (Pane) fourCharmPresenter.getView();

        stage.setScene(new Scene(root, 1000, 1000));
        stage.setTitle("FourCharmGUI");
        stage.show();
    }

    @Override
    public void showGame(Game game) {
        fourCharmPresenter.showGame(game);
    }

    @Override
    public void showNewGame() {
        fourCharmPresenter.showNewGame();
    }

    @Override
    public void enableInput() {
        fourCharmPresenter.enableInput();
    }

    @Override
    public int requestMove() {
        return fourCharmPresenter.requestMove();
    }
}




