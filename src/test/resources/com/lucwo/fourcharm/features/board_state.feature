Feature: Keeping board state
    Scenario: Board full
        Given an board with only one free spot
        When I fill the last spot
        Then the board should report its full
        And I should not be able to make an move
        
    Scenario: Move on empty board
        Given an empty board