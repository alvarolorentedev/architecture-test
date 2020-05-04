 # Libraries
 
 ## Source
 
 ### kafka-streams*
 
 library to interact with kafka in it has a DSL to use it in a functional way and also good testing tools 
 
 ### postgresql
 
 library to interact with postgress using jdbc.
 
 ### jackson-module-kotlin
 
 library to deserialize from a byte string representation of json in to a data model and be type safe in the application 
 
 ### fuel
 
 library to do http/https request has a good adoption in kotlin users and is well maintained
 
 ### konf
 
 library to read configuration files, is used to be able to retrieve the configuration properties for each of the enviroments.
 
 ## Test
 
 ## Junit
 
 library for testing, is probably the most used in all the java world.
 
 ## h2database
 
 library to simulate the database interaction it can be used as an in memory database with a small footprint. 
 
 IT was better to use this than test containers as it has a smaller footprint and is faster on the pipeline as test containers will require to fetch container in pipeline  each time unless cached.

 ## mockk

 mocking library, alternative to mockito. Is very simple to use and has a great adoption in kotlin. 

 ## wiremock
 
 library to do mocks over the wire allowing testing request and making sure they are well-formed.
 
 In java environments is preffered to mountebank as it can be natively integrated, while mountebank needs to run side by side being a node application.