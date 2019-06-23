# Sink information from Postgres towards ElasticSearch via Debezium


## Installation


### Create own version of debezium/connect-jdbc-es in order to have the elasticsearch configuration for kafka-connect

```
export DEBEZIUM_VERSION=0.9
cd connect-jdbc-es
docker build -t debezium/connect-jdbc-es:0.9 .
```

This image is then referenced in the docker-compose-postgres.yaml file.


### Start the topology as defined in http://debezium.io/docs/tutorial/
```
export DEBEZIUM_VERSION=0.9
docker-compose -f docker-compose-postgres.yaml up
```


### Start Postgres connector
```
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @register-postgres-smt.json
```


### Start Elasticsearch connector

```
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @es-sink-aggregated-orders.json
```




### Consume messages from a Debezium topic
```
docker-compose -f docker-compose-postgres.yaml exec kafka /kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server kafka:9092 \
    --from-beginning \
    --property print.key=true \
    --topic dbserver1.inventory.customers
```

### Consume messages from a Debezium topic with Avro

```
docker-compose -f docker-compose-postgres.yaml exec schema-registry /usr/bin/kafka-avro-console-consumer \
    --bootstrap-server kafka:9092 \
    --from-beginning \
    --property print.key=true \
    --property schema.registry.url=http://schema-registry:8081 \
    --topic dbserver1.inventory.orders
```


### Modify records in the database via Postgres client
```
docker-compose -f docker-compose-postgres.yaml exec postgres env PGOPTIONS="--search_path=inventory" bash -c 'psql -U $POSTGRES_USER postgres'
```

### Shut down the cluster
```
docker-compose -f docker-compose-postgres.yaml down
```






## Useful commands

See the kafka connect connectors configuration

```
curl -H "Accept:application/json" localhost:8083/connectors/ | jq
```

See the elasticsearch kafka-connect connector configuration

```
curl -H "Accept:application/json" localhost:8083/connectors/elastic-sink | jq
```

See the logs of the kafka-connect container

```
docker-compose -f docker-compose-postgres.yaml logs connect
```

See the contents of the `dbserver1.inventory.customers` elasticsearch index

```
curl http://localhost:9200/dbserver1.inventory.customers/_search | jq
```

Reset offsets of the kafka stream application

More details here: https://www.confluent.io/blog/data-reprocessing-with-kafka-streams-resetting-a-streams-application/

```
./kafka-streams-application-reset --application-id streaming-db-aggregator \
                                  --input-topics dbserver1.inventory.orders \
                                  --intermediate-topics streaming-db-aggregator-dbserver1.inventory.ordersSTATE-STORE-0000000000-changelog \
                                  --bootstrap-servers localhost:9092
```

Show topics

./kafka-topics --list --zookeeper localhost:2181

