/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm;

import com.lucwo.fourcharm.client.ServerHandler;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.LocalAIPlayer;
import com.lucwo.fourcharm.model.LocalHumanPlayer;
import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.view.FourCharmGUI;
import com.lucwo.fourcharm.view.FourCharmTUI;
import com.lucwo.fourcharm.view.FourCharmView;
import javafx.application.Application;
import mockit.Expectations;
import mockit.Injectable;
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
    public void testStartNetworkGame(@Mocked ServerHandler client) throws Exception {

        final GameStrategy p1Strat = new RandomStrategy();

        new Expectations() {
            {
                new ServerHandler("mallePietje", "localhost", "1335", controller); result = client;
                client.setStrategy(p1Strat);
            }
        };

        controller.startNetworkGame("localhost", "1335", "mallePietje", p1Strat);


    }

    @Test
    public void testStartLocalGameHumanPlayers(@Mocked final LocalHumanPlayer p1, @Mocked final LocalHumanPlayer p2) throws Exception {

        new Expectations() {
            {
                new LocalHumanPlayer("Frits", Mark.P1); result = p1;
                new LocalHumanPlayer("Wester", Mark.P2); result = p2;
                new Game(BinaryBoard.class, p1, p2);
            }
        };

        controller.startLocalGame("Frits", "Wester", null, null);
    }

    @Test
    public void testStartLocalGameAIPlayers(@Mocked final LocalAIPlayer p1, @Mocked final LocalAIPlayer p2) throws Exception {
        final GameStrategy p1Strat = new RandomStrategy();
        final GameStrategy p2Strat = new RandomStrategy();

        new Expectations() {
            {
                new LocalAIPlayer(p1Strat, Mark.P1); result = p1;
                new LocalAIPlayer(p2Strat, Mark.P2); result = p2;
                new Game(BinaryBoard.class, p1, p2);
            }
        };

        controller.startLocalGame(null, null, p1Strat, p2Strat);
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