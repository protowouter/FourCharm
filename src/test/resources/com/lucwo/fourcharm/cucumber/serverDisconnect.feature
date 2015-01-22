Feature: Client disconnects
  Scenario: Client disconnects while in a game
    Given the client is in a game with another player
    When I disconnect from the server
    Then the server sends a PlayerDisconnectError to the other player
    And the server sends an end_game command
    