Database credentials from launch.json not reaching the app
Tool/doc: Spring Boot, MySQL, VS Code launch configuration
Task: Start the backend against MySQL using credentials from environment variables
Steps taken: Set values in launch.json, referenced them as ${DB_Username} and ${DB_Password} in application.yml, started the app
Expected: Spring replaces the placeholders with the values from launch.json
Actual: Startup failed with "Access denied for user '${DB_Username}'@'localhost'", showing the unreplaced placeholder as the username
Severity: low
Time lost: 15mins
Workaround: restarted the project 
Suggestion: Spring could warn when a datasource property still contains an unresolved ${...} placeholder, since the current message looks like a wrong-password error


** Spring Data derived-query errors at startup **
Tool/doc: Spring Data JPA
Task: Add repository methods for date-range queries and a record-type query
Steps taken: Wrote methods whose names referenced fields that did not match the entity (timestamp vs recordedAt, and a misspelled recordeType)
Expected: Mismatches would be reported clearly
Actual: The application failed at startup with long "Error creating bean" chains, with the real cause only in the last "Caused by" line. That final message was clear and even suggested the correct spelling
Severity: low
Time lost: 5 min
Workaround: Renamed methods to match the entity fields
Suggestion: Print the final cause first in the failure analysis output
What worked well: The "Did you mean 'recordType'" hint pointed straight at the fix
