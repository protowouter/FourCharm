Feature: Client disconnects
  Scenario: Client disconnects while in a game
    Given the client is in a game with another player
    When I disconnect from the game
    Then the server sends a PlayerDisconnectError to the other player
    And the server sends an end_game command

  Scenario: Client disconnects while in the lobby // not in a game
    Given the client is connected to the server, but not in a game
    When I disconnect from the game
    Then the server closes the connection
    