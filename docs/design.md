 # Design
 
## Overall
 This is a streaming application that does not require endpoints so the desition was to do a simple command line application.
 
 The application is Containerized to make sure it can reproduce its functionality in any machine the idea should be to expose this app in the aws cloud enviroment with scaling enabled.

 The pipeline is used to build and test everything before the deployment for this i used github and github actions. 

## Cross functional requirements

### Security

make sure we do not put production secrets in the code. For this Jasypt and environment variables in the prod machine will be used, until this is moved to AWS and the posibility of using ssm or vault is provided

### Performance

Is a streaming application that is design to grow horizontally using kafka streams with the same application id.

### Addear to coding standards

Use linter to validate coding standards for kotlin are followed, if the are not followed the build will break on the commit.

## Code principles

- Testability through dependency inversion. 
- Self contained information correctly encapsulated.
- Treat functional streams as functional extension of specific type of stream
- Test everything, except configuration and domain models 

## Code Structure

```
./src/
|-- main
|   |-- kotlin
|   |   `-- com
|   |       `-- wefox
|   |           `-- kanekotic
|   |               `-- centralizedPayments
|   |                   |-- clients         //clients to interact with other http/https systems
|   |                   |   |-- ...
|   |                   |-- configurations  // all configurations to setup application
|   |                   |   |-- ...
|   |                   |-- models          // Domain data models
|   |                   |   |-- ...
|   |                   |-- persistors      // interaction with persistency environments
|   |                   |   `-- ...
|   |                   |-- processors      // Funtional DSL manipulation of streams
|   |                   |   |-- ...
|   |                   |-- serdes          // Serialization and Deserialization to domain models
|   |                   |-- utils           // Helper tools and classes
|   |                   |   |-- ...
|   |                   `-- Main.kt         //entrypoint
|   `-- resources                           //Configuration Files
|       |-- ...
`-- test
    |-- kotlin
    |   `-- com
    |       `-- wefox
    |           `-- kanekotic
    |               `-- centralizedPayments // Unit/boundary tests
    |                   |-- ...
    `-- resources                           //Configuration Files
        `-- ...

```
