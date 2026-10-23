## Database credentials from launch.json not reaching the app

- Tool/doc: Spring Boot, MySQL, VS Code launch configuration
- Task: Start the backend against MySQL using credentials from environment variables
- Steps taken: Set values in launch.json, referenced them as ${DB_Username} and ${DB_Password} in application.yml, started the app
- Expected: Spring replaces the placeholders with the values from launch.json
- Actual: Startup failed with "Access denied for user '${DB_Username}'@'localhost'", showing the unreplaced placeholder as the username
- Severity: low
- Time lost: 15mins
- Workaround: restarted the project 
- Suggestion: Spring could warn when a datasource property still contains an unresolved ${...} placeholder, since the current message looks like a wrong-password error


## Spring Data derived-query errors at startup 
- Tool/doc: Spring Data JPA
- Task: Add repository methods for date-range queries and a record-type query
- Steps taken: Wrote methods whose names referenced fields that did not match the entity (timestamp vs recordedAt, and a misspelled recordeType)
- Expected: Mismatches would be reported clearly
- Actual: The application failed at startup with long "Error creating bean" chains, with the real cause only in the last   "Caused by" line. That final message was clear and even suggested the correct spelling
- Severity: low
- Time lost: 5 min
- Workaround: Renamed methods to match the entity fields
- Suggestion: Print the final cause first in the failure analysis output
- What worked well: The "Did you mean 'recordType'" hint pointed straight at the fix

## Proxy token and the pre-filled link were confusing
- Tool/doc: MCP Inspector (terminal output on startup)
- Task: Open the Inspector and connect to my server
- Steps taken: Started the Inspector, saw an auth token and an API token in the terminal, was unsure what to do with them
- Expected: A clear instruction such as "open this link"
- Actual: It was not obvious that the token protects the Inspector's own local proxy (not my MCP server), or that the link printed in the terminal already includes the token
- Severity: medium
- Time lost: 5 mins
- Workaround: Copied the full link from the terminal into the browser
- Suggestion: Print one plain line: "Open this link to start; the token only protects this local proxy, your server needs no token"

## Session not found" after the server auto-restarted
- Tool/doc: Spring AI MCP server (Streamable HTTP) with Spring Boot DevTools, and MCP Inspector
- Task: Run log_health_record after editing code
- Steps taken: Edited and saved code (DevTools restarted the app), then ran a tool from the Inspector without reconnecting
- Expected: Either the call works, or the error says what to do
- Actual: The tool call failed with a JSON-RPC error "Session not found: <id>" followed by a very long stack trace. Nothing said the server had restarted or that    reconnecting would fix it. The cause was that the server keeps sessions in memory and a restart clears them
- Severity: medium
- Time lost: 10mins
- Workaround: Disconnect and Connect again in the Inspector after every restart
- Suggestion: Return a clear message such as "Session expired or server restarted, reinitialize the connection", and have the Inspector offer a Reconnect button on this error. The Spring AI docs could mention the STATELESS protocol option for development
