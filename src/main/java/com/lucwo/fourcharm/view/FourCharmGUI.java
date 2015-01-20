/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import com.lucwo.fourcharm.FourCharmController;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.presenter.FourCharmFactory;
import com.lucwo.fourcharm.presenter.FourCharmPresenter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FourCharmGUI extends Application implements FourCharmView {

    // ---------------- Instance Variables ------------------

    private FourCharmFactory fourCharmFactory;
    private FourCharmController controller;
    private FourCharmPresenter fourCharmPresenter;


    // --------------------- Constructors -------------------

    public FourCharmGUI(FourCharmController contr) {
        fourCharmFactory = new FourCharmFactory();
        controller = contr;
    }

    public FourCharmGUI() {

        controller = new FourCharmController();
        controller.setView(this);
        fourCharmFactory = new FourCharmFactory();

    }


    // ----------------------- Commands ---------------------

    public void start(Stage stage) throws Exception {

        fourCharmPresenter = fourCharmFactory.getFourCharmPresenter(controller);

        Pane root = (Pane) fourCharmPresenter.getView();

        stage.setScene(new Scene(root, 1000, 1000));
        stage.setTitle("FourCharmGUI");
        stage.show();
    }

    public void stop() {
        controller.shutdown();
    }



    @Override
    public void showGame(Game game) {
        Platform.runLater(() -> fourCharmPresenter.showGame(game));
    }

    @Override
    public void showNewGame() {
        fourCharmPresenter.showNewGame();
    }

    @Override
    public void showRematch() {
        fourCharmPresenter.showRematch();
    }

    @Override
    public void enableInput() {
        fourCharmPresenter.enableInput();
    }

    @Override
    public int requestMove() {
        return fourCharmPresenter.requestMove();
    }

    @Override
    public void showError(String errorMessage) {

    }
}




