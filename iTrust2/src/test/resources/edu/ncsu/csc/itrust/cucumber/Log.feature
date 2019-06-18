#Author tmfiggin

Feature: View a personnel's logs
	As a user of the system (admin, hcp, patient)
	I want to be able to view my top 10 recorded logs when I login
	And go to a separate page that lists all my logs
	That I can view in any existing time range
	
Scenario Outline: Personnel views their logs
Given I am logged in as a <user>
And I can see my top 10 logs on the home page
When I navigate to the View Logs page
Then the top 2 logs are successful login and access of log page for <user>

Examples:
	| user    |
	| patient |


Scenario: Invalid time range
Given I am a patient that has logged in
When I navigate to the View Logs page
And enter a date range of 2019/01/01 to 2019/01/02
Then the log table is empty
When I enter a valid date range of 2018/01/01 to 2019/01/02
Then the log table re-populates