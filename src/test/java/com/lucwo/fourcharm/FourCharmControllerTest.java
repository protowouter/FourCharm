/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm;

import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.view.FourCharmView;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FourCharmControllerTest {

    private FourCharmController controller;
    @Mocked
    private FourCharmView view;
    @Mocked
    private FourCharmView view2;
    @Mocked
    private Game game;

    @Before
    public void setUp() throws Exception {

        controller = new FourCharmController();
        controller.setView(view);

    }

    @Test
    public void testSetView() throws Exception {

        controller.setView(view2);
        assertEquals("setview should set the view of the controller", view2, controller.getView());

    }

    @Test
    public void testStartNetworkGame() throws Exception {


    }

    @Test
    public void testStartLocalGame() throws Exception {

    }

    @Test
    public void testSetGame() throws Exception {

        new Expectations() {
            {
                view.showGame(game);
                game.addObserver(controller);
            }
        };
        controller.setGame(game);

    }

    @Test
    public void testGetHumanPlayerMove() throws Exception {

        new Expectations() {
            {
                view.requestMove();
                result = 3;
            }
        };
        assertEquals("getHumanPlayerMove should ask the view for a move",
                3, controller.getHumanPlayerMove());
        controller.getHumanPlayerMove();

    }

    @Test
    public void testUpdate() throws Exception {

    }

    @Test
    public void testRematch() throws Exception {

    }

    @Test
    public void testShutdown() throws Exception {

    }

    @Test
    public void testShowError() throws Exception {

        new Expectations() {
            {
                view.showError("Hello, this is error");
            }
        };
        controller.showError("Hello, this is error");

    }
}