/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by woutertimmermans on 27-11-14.
 */


public class FourCharm extends Application {

    // ------------------ Instance variables ----------------

    // --------------------- Constructors -------------------

    // ----------------------- Queries ----------------------


    // ----------------------- Commands ---------------------

    public void start(Stage stage) throws Exception {

        ClassLoader classloader = getClass().getClassLoader();

        Parent root = FXMLLoader.load(classloader.getResource("views/fourcharmgui.fxml"));


        Scene scene = new Scene(root);

        stage.setTitle("FourCharm");
        stage.setScene(scene);
        stage.show();
    }
}




