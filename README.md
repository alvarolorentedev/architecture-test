### :computer: How to execute

#### Dependencies

make sure you have installed java and the jdk in your machine. if not i recomend following the installation instructions on https://adoptopenjdk.net/. Or use native package manager in you os.

Current environemnt is:
```bash
java --version                                                
openjdk 14 2020-03-17
OpenJDK Runtime Environment (build 14+36-1461)
OpenJDK 64-Bit Server VM (build 14+36-1461, mixed mode, sharing)
```

you can also use a docker container to build this project as explained in the next step.
#### Build
Use gradle to build the application

```bash
./gradlew build
```
you cn also run this with a docker container 

```bash
docker run -v `pwd`:`pwd` -w `pwd` -i -t openjdk:14-jdk ./gradlew clean build check test
```


or if you want to have a docker image already ready to run
```bash
/scripts/build-docker.sh
```


#### Run
in linux & windows
```bash
docker-compose up
```

in osx 
```bash 
docker-compose -f docker-compose.mac.yml up
```

start the process by sending curl to generate messages and clean postgress
```bash 
curl localhost:9000/start
```

### :memo: Notes

#### TL;DR;
The current solution is a simple kotlin applcation that reads from both streams and is containarized and deployed to the cloud environment with the CI.

![architecture current](https://user-images.githubusercontent.com/3071208/81049647-24bc5f00-8eae-11ea-9b42-f982a13c4d94.png)


The code has been designed so if things need to scale diferent the application can easily be devided and set in diferent containers.
#### Long Version

Yuo can find the entire decision record and explanations on the next links:
- [Development Practices](docs/practices.md)
- [Design Decision](docs/design.md)
- [Language Selection](docs/language.md)
- [Frameworks Selection](docs/frameworks.md)
- [Libraries Selection](docs/libraries.md)


### :pushpin: Things to improve

Architecture level:
- [ ] provide output topic to give feedback about outcome of transaction as web is unable to know what happen with current architecture
- [ ] make sure there are no duplicated transactions on the system
- [ ] add observability (ex. configure logstash in base docker image with kibana for logs)
- [ ] add metrics (ex. configure logstash in base docker image with grafana for metrics)
- [ ] add test data management in platform (ex. add new field to payment contract and request headers 'test-mode')
- [ ] add traceability data management in platform (ex. add new field to payment contract and request headers 'correlation-id')
- [ ] have secret management in place (ssm or vault)
- [ ] Manage infrastructure with terraform and kubernetes
- [ ] Feature toggles as a service

![architecture future](https://user-images.githubusercontent.com/3071208/81050485-9648dd00-8eaf-11ea-8ee6-ff0139c3ed37.png)


Project level:
- [ ] Add integration test (important)
- [ ] Make transactional adding to the database (important)
- [ ] local containarized build (nice to have)
- [ ] Add IoC (nice to have)
- [ ] Handle deserialization errors (nice to have)
- [ ] Handle pre deserialization errors (nice to have)
- [ ] use lenses library to improve mutation (nice to have)
- [ ] use version from CI when building docker on CI (nice to have)
- [ ] add flyway for database schema evolution (nice to have)
