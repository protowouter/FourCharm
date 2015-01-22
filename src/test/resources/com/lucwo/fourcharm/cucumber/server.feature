Feature: Client can join a server
  Scenario: Name not yet exists in server
    Given a empty server
    When I join the server
    Then the server will accept me

  Scenario: Name exists in server
    Given a server with one connected client
    When I join the server with the same name
    Then the server will send an InvalidUserName error