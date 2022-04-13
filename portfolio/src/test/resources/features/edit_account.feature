Feature: User can edit their account detail name fields and they are checked with regex validation

  Scenario Outline: User tries to edit account with valid details
    Given User sets th name fields to <firstname>, <lastname>, <nickname>
    When User saves the changes they have made
    Then User details are successfully updated
    Examples:
      | firstname | lastname | nickname |
      |"Cody"            |"Larsen"       | "Code E" |

