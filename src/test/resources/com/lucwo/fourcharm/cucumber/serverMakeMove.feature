Feature: Client can make a move
  Scenario: Client is in a game and it's his turn
    Given a game with two players and it's the clients turn
    When I send a 'do move' command
    Then the I send the move to the server
    And the server sends the move to the other player

  Scenario: Client is in a game, but it's not his turn
    Given a game with two players and it's not the clients turn
    When I send a 'do move' command
    Then the server will send an InvalidCommandError

  Scenario: Client is not in a game
    Given a client that is in a server, but not yet in a game
    When I send the 'do move' command
    Then the server will send an InvalidCommandError