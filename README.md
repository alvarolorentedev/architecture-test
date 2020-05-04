### :computer: How to execute

in linux & windows
```bash
docker-compose up
```

in osx 
```bash 
docker-compose -f docker-compose.mac.yml up
```

### :memo: Notes

_Some notes or explaination of your solution..._

### :pushpin: Things to improve

Architecture level:
- [ ] provide output topic to give feedback about outcome of transaction as web is unable to know what happen with current architecture
- [ ] make sure there are no duplicated transactions on the system
- [ ] add observability (ex. use logstash with kibana for logs)
- [ ] add metrics (ex. use logstash with grafana for logs)
- [ ] add test data management in platform (ex. add new field to payment contract and request headers 'test-mode')
- [ ] add traceability data management in platform (ex. add new field to payment contract and request headers 'correlation-id')

Project level:
- [x] Use configuration file by environment
- [x] add linter
- [ ] Add integration test
- [ ] Handle unhandle exceptions
- [ ] Handle deserialization errors
- [ ] Handle pre deserialization errors
- [ ] Make transactional adding to the database
- [ ] add retry for non 400 in clients
- [ ] use lenses library to improve mutation
