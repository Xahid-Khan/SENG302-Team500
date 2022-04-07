Feature: Add regex to editing user account name fields

  Scenario: User has a current account, tries to edit account and enters valid name to edit name field: AC4
    Given User tries to edit account name with a valid name
    When User saves changes
    Then User account details are updated
#
#  Scenario: User has a current account, tries to edit account and enters invalid name to edit name field: AC4
#    Given User edits account name with an invalid name
#    When User saves changes
#    Then User is shown error message and no changes are saved