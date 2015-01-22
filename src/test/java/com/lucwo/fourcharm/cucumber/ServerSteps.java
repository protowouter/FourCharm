/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.cucumber;

import com.lucwo.fourcharm.server.FourCharmServer;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class ServerSteps {

    private FourCharmServer server;
    private Socket wouterSocket;
    private Socket luceSocket;
    private BufferedWriter outWouter;
    private BufferedWriter outLuce;
    private BufferedReader inWouter;
    private BufferedReader inLuce;
    private Thread serverThread;

    @Before
    public void setup() throws Throwable {
        server = new FourCharmServer(0);
        server.openSocket();
        int port = server.getSocketPort();
        serverThread = new Thread(server::startServer);
        serverThread.start();
        wouterSocket = new Socket(InetAddress.getLocalHost(), port);
        outWouter = new BufferedWriter(new OutputStreamWriter(wouterSocket.getOutputStream()));
        inWouter = new BufferedReader(new InputStreamReader(wouterSocket.getInputStream()));
        luceSocket = new Socket(InetAddress.getLocalHost(), port);
        outLuce = new BufferedWriter(new OutputStreamWriter(luceSocket.getOutputStream()));
        inLuce = new BufferedReader(new InputStreamReader(luceSocket.getInputStream()));

    }

    @After
    public void tearDown() throws Throwable {
        wouterSocket.close();
        luceSocket.close();
        server.stop();
        serverThread.join();
    }

    @Given("^a empty server$")
    public void a_empty_server() throws Throwable {
    }

    private void joinLuce() throws Throwable {
        outLuce.write("join Luce 23\n");
        outLuce.flush();
    }

    private void joinWouter(String name) throws Throwable {
        outWouter.write("join " + name + " 23\n");
        outWouter.flush();
    }

    private void readyWouter() throws Throwable {
        outWouter.write("ready_for_game\n");
        outWouter.flush();
    }

    private void readyLuce() throws Throwable {
        outLuce.write("ready_for_game\n");
        outLuce.flush();
    }


    @When("^I join the server with name (.+)$")
    public void I_join_the_server(String name) throws Throwable {
        joinWouter(name);
    }

    @Then("^the server will accept me$")
    public void the_server_will_accept_me() throws Throwable {
        String accept = inWouter.readLine();
        assertEquals("Server should send accept", "accept 23", accept);
    }

    @Given("^a server with one connected client$")
    public void a_server_with_one_connected_client() throws Throwable {
        joinWouter("Wouter");
    }

    @When("^I join the server with the same name$")
    public void I_join_the_server_with_the_same_name() throws Throwable {
        outLuce.write("join Wouter 23\n");
        outLuce.flush();
    }

    @Then("^the server will send an InvalidUserName error$")
    public void the_server_will_send_an_InvalidUserName_error() throws Throwable {
        //String accept = inLuce.readLine();
        String error = inLuce.readLine();
        assertEquals("Server should send invalid username error",
                "error 4 The username Wouter is already in use", error);
    }

    @Then("^the server will send an InvalidParameterError with (.+)$")
    public void the_server_will_send_an_InvalidUserName_error_to_Wouter(String arg) throws Throwable {
        String error = inWouter.readLine();
        assertEquals("Server should send invalid parameter error",
                "error 8 Argument " + arg + " is not valid", error);
    }

    @Given("^a game with two players and it's the clients turn$")
    public void a_game_with_two_players_and_it_s_the_clients_turn() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @When("^I send a 'do move' command$")
    public void I_send_a_do_move_command() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Then("^the I send the move to the server$")
    public void the_I_send_the_move_to_the_server() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Then("^the server sends the move to the other player$")
    public void the_server_sends_the_move_to_the_other_player() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Given("^a game with two players and it's not the clients turn$")
    public void a_game_with_two_players_and_it_s_not_the_clients_turn() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Then("^the server will send an InvalidCommandError$")
    public void the_server_will_send_an_InvalidCommandError() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Given("^a client that is in a server, but not yet in a game$")
    public void a_client_that_is_in_a_server_but_not_yet_in_a_game() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @When("^I send the 'do move' command$")
    public void I_send_the_do_move_command() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Given("^a server with one other ready player$")
    public void a_server_with_one_other_ready_player() throws Throwable {
        joinWouter("Wouter");
        String accept = inWouter.readLine();
        readyWouter();
    }

    @When("^I send the ready command$")
    public void I_send_the_ready_command() throws Throwable {
        joinLuce();
        String accept = inLuce.readLine();
        readyLuce();
    }

    @Then("^the server starts a game with the other ready player and me$")
    public void the_server_starts_a_game_with_the_other_ready_player_and_me() throws Throwable {
        String startLuce = inLuce.readLine();
        String startWouter = inWouter.readLine();
        assertEquals("Server should send start game to Luce", "start_game Wouter Luce", startLuce);
        assertEquals("Server should send start game to Wouter", "start_game Wouter Luce", startWouter);
    }

    @Given("^a server with no other ready players$")
    public void a_server_with_no_other_ready_players() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Then("^the server waits with putting me in a game until another player is ready as well$")
    public void the_server_waits_with_putting_me_in_a_game_until_another_player_is_ready_as_well() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

}
