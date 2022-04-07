Feature: User Role

  Background:
    Given a user exists

  Scenario: When a user has role "Student" and adds a role "Teacher" they have just both roles.
    Given the user has a role "STUDENT"
    When the user adds a role "TEACHER"
    Then the user should have a role "STUDENT"
    And the user should have a role "TEACHER"

  Scenario: AC3 When a user has a single role "Student" they should not be able to remove it.
    Given the user has a role "STUDENT"
    When the user deletes a role "STUDENT"
    Then the user should have a role "STUDENT"

  Scenario: When a user tries to delete a role that they don't have no role should be deleted.
    Given the user has a role "STUDENT"
    When the user deletes a role "TEACHER"
    Then the user should have a role "STUDENT"

  Scenario: AC5 When a user tries to add a role they already have, they shouldn't be able to
    Given the user has a role "STUDENT"
    When the user adds a role "STUDENT"
    Then the user has a role "STUDENT"

  Scenario: When a user tries to remove one of many roles they have, it should be removed
    Given the user has a role "STUDENT"
    And the user has a role "TEACHER"
    When the user deletes a role "STUDENT"
    Then the user has a role "TEACHER"
    And the user should not have a role "STUDENT"