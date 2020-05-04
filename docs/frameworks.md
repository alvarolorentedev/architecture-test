#Framework 

## App

Due this application do not need any endpoints it was not required to add any server application like play, akka, spring... 

A bear console application was developed.
## Kafka

On kafka the posibilities was to use the bear kafka library but this will require management of commit the current offset of the messages. This is already solved in the streams libraries like:
* Kafka Streams: based on the same creators of kafka so the support is very good it allows to use kafka as a stream or a table, having even its own query language.
* Akka Streams: created by lightbend it has a very good extensibility and is well maintained. 

The desition was to use Kafka streams due it is the original library and it has good testing tools. Unless the composition wants to be done with abstractions akka stream is more complex on the learning curve and it does not bring a lot more of advantages.
 