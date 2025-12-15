# ObjectId Id Strategy

This custom strategy implementation allows valid ObjectID strings in kafka messages to be converted to ObjectIDs when writing to MongoDB. Currently

For example, if the kafka message looks like this with the `_id` field in string:

```json
{"_id": "692755b56a7df7a5e89dc29f","id": 1, "email": "me@example.com", "name": "John Doe"}
```

The MongoDB doc will have the same field in ObjectId format:

```json
{"_id": ObjectId("692755b56a7df7a5e89dc29f"),"id": 1, "email": "me@example.com", "name": "John Doe"}
```

Tested with both Atlas and MongoDB community server.

# Sample Connector Configs with ObjectId Strategy

To use the ObjectId strategy, make sure you overwrite the default one and configure your sink connector to use the custom class:

```json
    "document.id.strategy.overwrite.existing": "true",
    "document.id.strategy": "io.kaufmanng.kafka.connect.mongodb.sink.processor.id.strategy.ProvidedOidInValueStrategy"
```

If you are deploying this custom connector in Confluent Cloud:

```json
{
  "collection": "users",
  "confluent.custom.schema.registry.auto": "true",
  "connection.uri": "mongodb+srv://<MONGODB-CREDENTIALS>@<atlas-endpoint>.mongodb.net/",
  "database": "inventory",
  "document.id.strategy": "io.kaufmanng.kafka.connect.mongodb.sink.processor.id.strategy.ProvidedOidInValueStrategy",
  "document.id.strategy.overwrite.existing": "true",
  "errors.deadletterqueue.topic.name": "users-dlq",
  "errors.tolerance": "all",
  "topics": "users",
  "value.converter": "io.confluent.connect.avro.AvroConverter"
}
```

Alternatively, if you use Confluent Platform or self managing the connector in your connect cluster:

```json
{
    "name": "mongodb-sink-users",
    "config": {
        "collection": "users",
        "connection.uri": "mongodb://myuser:mypassword@localhost:27017",
        "connector.class": "com.mongodb.kafka.connect.MongoSinkConnector",
        "database": "inventory",
        "tasks.max": "1",
        "topics": "users2",
        "errors.tolerance": "all",
        "errors.deadletterqueue.topic.name": "users2-dlq",
        "document.id.strategy.overwrite.existing": "true",
        "document.id.strategy": "io.kaufmanng.kafka.connect.mongodb.sink.processor.id.strategy.ProvidedOidInValueStrategy"
    }
}
```

# How to run in Docker/Podman

## Build your custom image

Make sure you have built the artifacts first with `gradlew createConfluentArchive`.
At the top level of this project, run `docker build` (or `podman build`)

```podman build -t mongo-kafka-connector:latest -f docker/Dockerfile .```

## use Docker Compose

Create an environment file with Confluent Cloud credentials

```
BOOTSTRAP_SERVERS=<pkc-xxxxx.us-east-2.aws>.confluent.cloud:9092
CLOUD_KEY=<CC-API-KEY>
CLOUD_API_SECRET=<CC-API-SECRET>
SCHEMA_REGISTRY_URL=https://psrc-yyyyyy.us-east-2.aws.confluent.cloud
SCHEMA_REGISTRY_BASIC_AUTH_USER_INFO=<SR-API-KEY>:<SR-API-SECRET>
SASL_JAAS_CONFIG=org.apache.kafka.common.security.plain.PlainLoginModule required username="<CC-API-KEY>" password="<CC-API-SECRET>";
```

Run `docker compose`:

```docker compose -f docker/docker-compose-cc.yml --env-file my.env up -d```

# Limitations

* The string _must_ be of valid HEX format as per ObjectId spec, otherwise the writing of the record will fail (and may go to DLQ if configured to do so)
