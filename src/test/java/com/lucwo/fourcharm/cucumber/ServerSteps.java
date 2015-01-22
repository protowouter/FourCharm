/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.cucumber;

import com.lucwo.fourcharm.FourCharmController;
import com.lucwo.fourcharm.server.FourCharmServer;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class ServerSteps {

    private FourCharmServer server;
    private Socket c1Socket;
    private Socket c2Socket;
    private BufferedWriter out1;
    private BufferedWriter out2;

    @Given("^a empty server$")
    public void a_empty_server() throws Throwable {
        startServer();
    }

    private void startServer() throws Throwable {
        server = new FourCharmServer(8085);
        server.openSocket();
        new Thread(server::startServer).start();
    }

    @When("^I join the server$")
    public void I_join_the_server() throws Throwable {
        c1Socket = new Socket(InetAddress.getLocalHost(), 8085);
        out1 = new BufferedWriter(new OutputStreamWriter(c1Socket.getOutputStream()));
        out1.write("join Wouter 23\n");
        out1.flush();
    }

    @Then("^the server will accept me$")
    public void the_server_will_accept_me() throws Throwable {
        BufferedReader in = new BufferedReader(new InputStreamReader(c1Socket.getInputStream()));
        String accept = in.readLine();
        assertEquals("Server should send accept", "accept 23", accept);
    }

    private void joinWouter() throws Throwable {
        c1Socket = new Socket(InetAddress.getLocalHost(), 8085);
        out1 = new BufferedWriter(new OutputStreamWriter(c1Socket.getOutputStream()));
        out1.write("join Wouter 23\n");
        out1.flush();
    }

    @Given("^a server with one connected client$")
    public void a_server_with_one_connected_client() throws Throwable {
        startServer();
        joinWouter();
    }

    @When("^I join the server with the same name$")
    public void I_join_the_server_with_the_same_name() throws Throwable {
        c2Socket = new Socket(InetAddress.getLocalHost(), 8085);
        out2 = new BufferedWriter(new OutputStreamWriter(c2Socket.getOutputStream()));
        out2.write("join Wouter 23\n");
        out2.flush();
    }

    @Then("^the server will send an InvalidUserName error$")
    public void the_server_will_send_an_InvalidUserName_error() throws Throwable {
        BufferedReader in = new BufferedReader(new InputStreamReader(c2Socket.getInputStream()));
        String error = in.readLine();
        assertEquals("Server should send invalid username error",
                "error 4 The username Wouter is already in use", error);
    }

}
