Feature: Server recognizes invalid commands

  Scenario: Server sends error when invalid command is sent
    Given a empty server
    When I send the command bogus
    Then the server will send an InvalidCommandError

  Scenario: Client sends ready in connected state
    Given a empty server
    When I send the ready command
    Then the server will send an InvalidCommandError

  Scenario: Client sends ready twice
    Given a empty server
    When I join the server with name Wouter
    Then the server will accept me
    And I send the ready command
    And I send the ready command
    Then the server will send an InvalidCommandError

  Scenario: Client sends join in lobby state
    Given a empty server
    When I join the server with name Wouter
    Then the server will accept me
    And I join the server with name Frits
    Then the server will send an InvalidCommandError