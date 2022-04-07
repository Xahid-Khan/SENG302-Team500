Feature: Persistent Filtering on listing users
  - When a user logs out and back in, their filtering preferences should be saved.
  - If the user has no filtering saved to their user account,
  then default filtering should be loaded.
  - When the user inputs invalid filters, the database should not update to reflect this change.

  Scenario Outline: Load filtering
    Given there is a user who is logged in
    And they have <parameters> saved to their user account
    When the user loads the list of users page
    Then the filtering should be applied
    Examples:
      | parameters |
      | ""         |
      #| #TODO      |


  # TODO: Make better
  Scenario Outline: Save filtering
    Given there is a user who is logged in
    # Used to ensure overwriting
    And they have <parameters> saved to their user account
    When the user loads the list of users page
    And the user tries to filter the list of users
    Then the filtering should be applied
    And the database should reflect the changes
    Examples:
      | parameters |
      | ""         |
    # TODO: Ones to be overwritten
