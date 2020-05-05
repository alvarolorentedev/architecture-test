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

Decision record:
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
