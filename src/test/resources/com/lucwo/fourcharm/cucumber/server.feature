Feature: Client can join a server
  Scenario: Name not yet exists in server
    Given a empty server
    When I join the server
    Then the server will accept me

  Scenario: Name exists in server
    Given a server with one connected client
    When I join the server with the same name
    Then the server will send an InvalidUserName error

  Scenario: Name is not valid according to protocol
    Given a empty server
    When I join the server
    Then the server will send an InvalidUserName error

Feature: Client can start a game
    Scenario: The client sends a 'ready' command
      Given a server with one other ready player
      When I send the ready command
      Then the server starts a game with the other ready player and me

    Scenario: The client sends a 'ready' command
      Given a server with no other ready players
      When I send the 'ready' command
      Then the server waits with putting me in a game until another player is ready as well


Feature: Client can make a move
    Scenario: Client is in a game and it's his turn
      Given a game with two players and it's the clients turn
      When I send a 'do move' command
      Then the I send the move to the server
      And the server sends the move to the other player

