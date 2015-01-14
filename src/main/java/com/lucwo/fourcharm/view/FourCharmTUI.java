/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import com.lucwo.fourcharm.model.*;
import com.lucwo.fourcharm.model.ai.NegaMaxStrategy;
import com.lucwo.fourcharm.model.board.ReferenceBoard;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
class FourCharmTUI implements Observer, MoveRequestable {


    // ------------------ Instance variables ----------------

    private Game game;
    private Scanner nameScanner;
    private boolean running;
    private BlockingQueue<Integer> rij;
    private boolean gameOn;
    
    // --------------------- Constructors -------------------

    /**
     * Constructor
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    protected FourCharmTUI() throws InstantiationException, IllegalAccessException {
        super();

        nameScanner = new Scanner(System.in);
        rij = new LinkedBlockingQueue<>(1);
        gameOn = true;
        running = true;
    }

    /**
     * The main method of the FourCharmTui.
     * @param args none applicable
     */
    public static void main(String[] args) {

        Logger globalLogger = Logger.getGlobal();
        LogManager.getLogManager().reset();
        globalLogger.setLevel(Level.FINE);

        ConsoleHandler cH = new ConsoleHandler();
        cH.setLevel(Level.FINE);

        globalLogger.addHandler(cH);

        try {
            new FourCharmTUI().run();
        } catch (InstantiationException | IllegalAccessException e) {
            Logger.getGlobal().throwing("FourCharmTUI", "main", e);
        }



    }

    // ----------------------- Commands ---------------------

    /**
     * Reads the command and places it in an array.
     * @param commandString the command in string format
     */
    private void parseCommand(String commandString) {
        Scanner commandScanner = new Scanner(commandString);
        String command;
        List<String> args = new ArrayList<String>();

        if (commandScanner.hasNext()) {
            command = commandScanner.next();
            while (commandScanner.hasNext()) {
                args.add(commandScanner.next());
            }
            checkCommand(command, args.toArray(new String[0]));
        }
        commandScanner.close();

    }

    /**
     * Checks if the given command is valid.
     * @param commandString the command in string format
     * @param args          the amount of commands
     */
    private void checkCommand(String commandString, String[] args) {
        Command command = Command.parseString(commandString);
        if (command != null) {
            if (args.length == command.argCount()) {
                executeCommand(command, args);
            } else {
                showError("The command " + command.toString()
                        + " requires " + command.argCount()
                        + " parameters. You gave " + args.length + ".");
            }
        } else {
            showError("This command has no power here!");
        }
    }


    /**
     * Executes the given command.
     * @param command the given command
     * @param args string array of arguments
     */
    private void executeCommand(Command command, String[] args) {
        switch (command) {
            //Start new game if true
            case YES:
                if (!gameOn) {
                    startGame();
                }
                break;
            //Use the chat function
            case CHAT:
                break;
            //Make a move
            case MOVE:
                int moove = Integer.parseInt(args[0]) - 1;
                try {
                    rij.put(moove);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            //Ask for a hint in the current game.
            case HINT:
                break;
            //Challenge another player to play a game
            case CHALLENGE:
                break;
            //List all of the players in the lobby
            case LIST_PLAYERS:
                break;
            //List all the available commands
            case HELP:
                showHelp();
                break;
            //Exit the game
            case EXIT:
                running = false;
                break;
        }
    }


    /**
     * Shows the given message.
     * @param message the given message
     */
    private void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Shows all of the available commands.
     */
    private void showHelp() {
        showMessage("These commands I know: ");
        for (Command c : Command.values()) {
            showMessage(c.toString());
        }
    }

    /**
     * Shows the (error) message.
     * @param message the (error) message
     */
    public void showError(String message) {
        System.err.println(message);

    }

    /**
     * Updates everything, like omg.
     * @param o
     * @param arg het object
     */
    @Override
    public void update(Observable o, Object arg) {
        Logger.getGlobal().finer("Tui is getting message from: " + o.toString());
        if (o instanceof Game) {
            Game newGame = (Game) o;
            System.out.println((newGame).getBoard().toString());
            if (newGame.hasFinished()) {
                showMessage("The game has finished. ");
                gameOn = false;
                if (newGame.hasWinner()) {
                    showMessage("The winner is " + newGame.getWinner());
                }
                showMessage("Would you like to play a new game? [yes/exit]");
                if (nameScanner.hasNextLine()) {
                    if (nameScanner.nextLine().equals("Yes")) {
                        startGame();
                    }
                }


            }
        }

    }

    /**
     * Run the game.
     */
    protected void run() {
        showMessage("Welcome to FourCharm Connect4.");
        startGame();


        while (running && nameScanner.hasNextLine()) {
            parseCommand(nameScanner.nextLine());

        }

        game.stop();
        try {
            rij.put(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Starts the game.
     */
    private void startGame() {
        String name = "";
        showMessage("Please enter your name");

        if (nameScanner.hasNextLine()) {
            name = nameScanner.nextLine();
            showMessage("Welcome " + name);
        }

        Player player1 = new ASyncPlayer(name, this, Mark.P1);
        Player player2 = new LocalAIPlayer(new NegaMaxStrategy(10), Mark.P2);

        try {
            game = new Game(ReferenceBoard.class, player1, player2);
            game.addObserver(this);

            new Thread(game).start();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asks for a move and puts this move in the queue.
     * @return the requested move
     */
    @Override
    public int requestMove() {
        showMessage("It's your turn now! Please make a move by using command 'move [number]'");
        int column = -1;
        try {
            column = rij.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return column;
    }

    /**
     * Enum class that 'holds' the commands of the Connect4 game.
     */
    private enum Command {
        CHAT(new String[]{"Chatmessage"}),
        HINT(new String[0]),
        EXIT(new String[0]),
        CHALLENGE(new String[]{"Player name"}),
        HELP(new String[0]),
        MOVE(new String[]{"The column"}),
        LIST_PLAYERS(new String[0]),
        YES(new String[0]);


        String[] parameterNames;

        Command(String[] cNames) {
            parameterNames = cNames;
        }

        /**
         * @param cString
         * @return
         */
        public static Command parseString(String cString) {
            for (Command c : Command.values()) {
                if (c.name().equalsIgnoreCase(cString)) {
                    return c;
                }

            }
            return null;
        }

        /**
         * @return an integer with the total amount of arguments
         */
        public int argCount() {
            return parameterNames.length;
        }

        /**
         * @return
         */
        public String argDesc() {
            String desc = "";
            for (String arg : parameterNames) {
                desc += "[" + arg + "]";
            }

            return desc;
        }

        /**
         * @return the string representation
         */
        @Override
        public String toString() {
            return name() + " " + argDesc();
        }
    }
}
