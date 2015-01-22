Feature: Client can make a move
  Scenario: Client is in a game and it's his turn
    Given a game with two players and it's the clients turn
    When I send a 'do move 4' command
    Then the server sends the move 4 to both the players
    And the server sends a requestmove to the other player

  Scenario: Client is in a game, but it's not his turn
    Given a game with two players and it's not the clients turn
    When I send a 'do move 4' command
    Then the server will send an InvalidMoveError

  Scenario: Client is not in a game
    Given a client that is in a server, but not yet in a game
    When I send a 'do move 4' command
    Then the server will send an InvalidCommandError

  Scenario: Client is in a game, but sends a invalid move
    Given a game with two players and it's the clients turn
    When I send a 'do move 7' command
    Then the server will send an InvalidParameterError with 7
    
