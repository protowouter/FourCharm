Feature: Client can start a game
  Scenario: The client sends a 'ready' command
    Given a server with one other ready player
    When I send the ready command
    Then the server starts a game with the other ready player and me

  Scenario: The client sends a 'ready' command
    Given a server with no other ready players
    When I send the 'ready' command
    Then the server waits with putting me in a game until another player is ready as well