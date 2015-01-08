/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import org.junit.Before;
import org.junit.Test;

import java.util.Observable;

public class FourCharmTUITest {

    FourCharmTUI tui;

    @Before
    public void setUp() throws Exception {

        tui = new FourCharmTUI();

    }

    @Test
    public void testMain() throws Exception {

        // TODO test main
        // https://rkennke.wordpress.com/2012/04/12/how-to-test-drive-a-main-method/

    }

    @Test
    public void testUpdate() throws Exception {


        tui.update(new Observable(), new Object());


    }
}