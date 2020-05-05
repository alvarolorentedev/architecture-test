 # Development Principles
 
 ## TDD
 
 Code should be based on the specs that have been defined. Implementation of each function started with test and later the code
 
 ## Testing Strategy
 
Most test should be unit and boundary.
 
boundary that interact with kafka or postgress should not use mocks as logic is based on interaction with systems.

Boundaries:
- Postgress: Use in memory databases for interaction with database (other options exist like testcontainers, decision is explained in libraries section).
- Kafka: use default testing library for kafka and pipe messages validating behaviour.
- Services: use wiremock to start afake server that will mock responses (mock over the wire, other options would have been mountebank, decision is explained in libraries section)

Integration, smoke and end to end tests can be added. Some of them will require changes in the architecture.
 
 ## Trunk Based Development
 
 Every commit should be able to go to production this simplifies mutiple things like:
 
 - Helps get into the mindset of CI/CD
 - Small incremental changes can simplify fixing issues, as with the correct observability and automated testing they can be caught easily.
 - Reduce merge issues and collision in work inside the team.
 
 ## Feature Toggles
 
 To make sure progress can continue without affecting live users a strategy of feature toggles was used, only activate in the environment that is expected.
 
 This allows continue commiting code and deploy it to production without affecting the production environment.
 
 The next iteration of this is to use feature toggles as a service in the platform to decouple build time from activation/deactivation time.
 
 ## Containerization
 
 Make aplication exist in a reproducible enviroment that is easyly to contain and replicate in the cloud.
 
 ## CI/CD
 
 An external service should make reproducible all the development process, including: 
 * Building
 * Testing
 * Deployment
 
 ## Infrastructure as code (WishList)
 
 infrastructure as the code it should be reproducible and be able to be explained in the repository files. This makes sure that the correct set of infrastructure pieces (and their attached security) are in place for the application. 
 
 ## Single Environment (WishList)
 
 Having multiple environments like dev/test/prod, adds complexity for keeping in sync data and code consistency.
 
 With a single environment and having test data flagged to escape the normal behavior complexity and cost can be reduced.
 
 This requires architectural changes to mark the data as test, and services should be able to consume this flag and interpret it.  
 
 ## Kanban
 
 After been given the task the first thing was to understand the requirements and what would make them complete stories can be found on [the kanban board](https://github.com/kanekotic/wefox-test/projects/1). Eaxch of them have their own Acceptance criteria. With the agility of addapt to changing priorities.
 
 ## MVP Orientation 
 
 The kanban board is divided in MVP (meaning were I think we should go live), and post mvp as value need to be delivered incrementaly.