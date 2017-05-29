## Create topics in kafka

```bash
./bin/kafka-topics.sh --create --topic myothertest --zookeeper localhost:2181 --partitions 1 --replication-factor 1 --config retention.ms=20000
```