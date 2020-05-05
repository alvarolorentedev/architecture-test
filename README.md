## Do Not Review yet

### :computer: How to execute

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
- [Development Practices](docs/development_practices.md)
- [Language Selection](docs/language.md)
- [Frameworks Selection](docs/frameworks.md)
- [Libraries Selection](docs/libraries.md)


### :pushpin: Things to improve

Architecture level:
- [ ] provide output topic to give feedback about outcome of transaction as web is unable to know what happen with current architecture
- [ ] make sure there are no duplicated transactions on the system
- [ ] add observability (ex. use logstash with kibana for logs)
- [ ] add metrics (ex. use logstash with grafana for logs)
- [ ] add test data management in platform (ex. add new field to payment contract and request headers 'test-mode')
- [ ] add traceability data management in platform (ex. add new field to payment contract and request headers 'correlation-id')
- [ ] have secret management in place (ssm or vault)
- [ ] Manage infrastructure with terraform and kubernetes
- [ ] Feature toggles as a service

![architecture future](https://user-images.githubusercontent.com/3071208/81050485-9648dd00-8eaf-11ea-8ee6-ff0139c3ed37.png)
Project level:
- [x] Use configuration file by environment
- [x] add linter
- [x] deploy to cloud using CI
- [x] have encrypted secrets
- [ ] Add integration test
- [ ] Add IoC
- [ ] Handle unhandle exceptions
- [ ] Handle deserialization errors
- [ ] Handle pre deserialization errors
- [ ] Make transactional adding to the database
- [ ] add retry for non 400 in clients
- [ ] use lenses library to improve mutation
- [ ] use version from CI when building docker on CI
- [ ] add flyway for database schema evolution
